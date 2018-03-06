package ru.dmzadorin.clientservice.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.annotation.PostMethod;
import ru.dmzadorin.clientservice.annotation.RequestParam;
import ru.dmzadorin.clientservice.model.exceptions.ApplicationException;
import ru.dmzadorin.clientservice.model.request.ExtraType;
import ru.dmzadorin.clientservice.model.request.RequestType;
import ru.dmzadorin.clientservice.model.response.ResponseType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class RequestDispatcher {
    private static final Logger logger = LogManager.getLogger();
    private final Function<InputStream, RequestType> requestDeserializer;
    private final Map<String, MethodMetadata> methodRouting;
    private final Map<Type, TypeConverter> typeConverters;

    public RequestDispatcher(Function<InputStream, RequestType> requestDeserializer,
                             Map<Type, TypeConverter> typeConverters,
                             Object... controllers) {
        this.requestDeserializer = requestDeserializer;
        this.typeConverters = typeConverters;
        methodRouting = new HashMap<>();
        scanControllers(controllers);
    }

    private void scanControllers(Object... controllers) {
        for (Object controller : controllers) {
            Class<?> clazz = controller.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                PostMethod postMethod = method.getDeclaredAnnotation(PostMethod.class);
                if (postMethod != null) {
                    scanPostMethod(controller, method, postMethod);
                }
            }
        }
    }

    private void scanPostMethod(Object controller, Method method, PostMethod postMethod) {
        String methodName = postMethod.name();
        String returnParamName = postMethod.returnParamName();
        List<ParameterMetadata> parameterMetadata = new ArrayList<>(method.getParameterCount());
        for (Parameter parameter : method.getParameters()) {
            RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
            String paramName;
            if (requestParam != null) {
                paramName = requestParam.name();
            } else {
                //No RequestParam annotation for this param, it won't be possible to bind it for request
                throw new IllegalArgumentException("No annotation @RequestParam present for parameter " +
                        parameter.getName() + " in method " + methodName);
            }
            Type type = parameter.getParameterizedType();
            TypeConverter typeConverter = typeConverters.get(type);
            if (typeConverter == null) {
                throw new IllegalArgumentException("Type converter for type " + type.getTypeName() + " is not present!");
            }
            parameterMetadata.add(new ParameterMetadata(paramName, typeConverter));
        }
        methodRouting.put(methodName, new MethodMetadata(controller, method, parameterMetadata, returnParamName));
    }

    public ResponseType handleRequest(InputStream requestBody) {
        try {
            RequestType requestType = requestDeserializer.apply(requestBody);
            String request = requestType.getRequestType();
            MethodMetadata methodMetadata = methodRouting.get(request);
            if (methodMetadata != null) {
                Map<String, String> requestParams = requestType.getExtra().stream()
                        .collect(Collectors.toMap(ExtraType::getName, ExtraType::getValue));
                return methodMetadata.invokeMethod(requestParams);
            } else {
                throw new IllegalArgumentException("Request type " + request + " is not supported by any controller method");
            }
        } finally {
            closeQuietly(requestBody);
        }
    }

    private void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignore) {
            }
        }
    }

    private class MethodMetadata {
        private final Object controller;
        private final Method method;
        private final List<ParameterMetadata> parameters;
        private final String returnParamName;

        public MethodMetadata(Object controller, Method method, List<ParameterMetadata> parameters, String returnParamName) {
            this.controller = controller;
            this.method = method;
            this.parameters = parameters;
            this.returnParamName = returnParamName;
        }

        public ResponseType invokeMethod(Map<String, String> params) {
            List<Object> args = new ArrayList<>(parameters.size());
            for (ParameterMetadata parameter : parameters) {
                if (params.containsKey(parameter.name)) {
                    String inputValue = params.get(parameter.name);
                    Object inputArgument = parameter.typeConverter.convertFromString(inputValue);
                    args.add(inputArgument);
                } else {
                    throw new IllegalArgumentException("Mandatory parameter " + parameter.name + " is absent in request");
                }
            }

            ResponseType responseType = new ResponseType();
            responseType.setResultCode(0);
            try {
                Object[] arrArgs = args.toArray();
                Object result = method.invoke(controller, arrArgs);
                if (result instanceof Number || result instanceof String) {
                    responseType.setExtra(buildExtra(result.toString()));
                }
            } catch (IllegalAccessException e) {
                logger.error("Failed to invoke method: " + method.getName() + " since it's not accessible", e);
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                //If target exception is instance of ApplicationException then rethrow it. Else throw
                // IllegalStateException since we don't actually know what caused that exception. It could be either NPE,
                // or other runtime exception
                if (targetException instanceof ApplicationException) {
                    throw (ApplicationException) targetException;
                } else {
                    logger.error("Got non-application exception while invoking method: " + method.getName(),
                            targetException);
                    throw new IllegalStateException(targetException);
                }
            }
            return responseType;
        }

        private ru.dmzadorin.clientservice.model.response.ExtraType buildExtra(String value) {
            ru.dmzadorin.clientservice.model.response.ExtraType extraType =
                    new ru.dmzadorin.clientservice.model.response.ExtraType();
            extraType.setName(returnParamName);
            extraType.setValue(value);
            return extraType;
        }
    }

    private class ParameterMetadata {
        private final String name;
        private final TypeConverter<?> typeConverter;

        private ParameterMetadata(String name, TypeConverter<?> typeConverter) {
            this.name = name;
            this.typeConverter = typeConverter;
        }
    }
}
