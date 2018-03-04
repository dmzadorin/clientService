package ru.dmzadorin.clientservice.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.annotation.PostMethod;
import ru.dmzadorin.clientservice.annotation.RequestParam;
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

    public RequestDispatcher(Function<InputStream, RequestType> requestDeserializer, Map<Type, TypeConverter> typeConverters, Object... controllers) {
        this.requestDeserializer = requestDeserializer;
        methodRouting = new HashMap<>();
        this.typeConverters = typeConverters;
        scanControllers(controllers);
    }

    private void scanControllers(Object... controllers) {
        for (Object controller : controllers) {
            Class<?> clazz = controller.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                PostMethod postMethod = method.getDeclaredAnnotation(PostMethod.class);
                if (postMethod != null) {
                    String methodName = postMethod.name();
                    String returnParamName = postMethod.returnParamName();
                    List<ParameterMetadata> parameterMetadata = new ArrayList<>(method.getParameterCount());
                    for (Parameter parameter : method.getParameters()) {
                        RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
                        String paramName;
                        if (requestParam != null) {
                            paramName = requestParam.name();
                        } else {
                            //No handy annotation for this param. Will try to match param by name as it is declared in code
                            throw new IllegalArgumentException("No annotation @RequestParam present for parameter " + parameter.getName() + " in method " + methodName);
                        }
                        Type type = parameter.getParameterizedType();
                        TypeConverter typeConverter = typeConverters.get(type);
                        if (typeConverter == null) {
                            throw new IllegalArgumentException("Type converter for type " + type.getTypeName() + " is not present!");
                        }
                        parameterMetadata.add(new ParameterMetadata(paramName, parameter.getType(), typeConverter));
                    }
                    methodRouting.put(methodName, new MethodMetadata(controller, method, parameterMetadata, returnParamName));
                }
            }
        }
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
        private final Method methodToInvoke;
        private final List<ParameterMetadata> parameters;
        private final String returnParamName;

        public MethodMetadata(Object controller, Method methodToInvoke, List<ParameterMetadata> parameters, String returnParamName) {
            this.controller = controller;
            this.methodToInvoke = methodToInvoke;
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
                Object result = methodToInvoke.invoke(controller, arrArgs);
                if (result instanceof Number || result instanceof String) {
                    responseType.setExtra(buildResponse(result.toString()));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Failed to invoke method: " + methodToInvoke.getName(), e);
                throw new IllegalStateException(e);
            }
            return responseType;
        }

        private ru.dmzadorin.clientservice.model.response.ExtraType buildResponse(String value) {
            ru.dmzadorin.clientservice.model.response.ExtraType extraType =
                    new ru.dmzadorin.clientservice.model.response.ExtraType();
            extraType.setName(returnParamName);
            extraType.setValue(value);
            return extraType;
        }
    }

    private class ParameterMetadata {
        private final String name;
        private final Class<?> clazz;
        private final TypeConverter<?> typeConverter;

        private ParameterMetadata(String name, Class<?> clazz, TypeConverter<?> typeConverter) {
            this.name = name;
            this.clazz = clazz;
            this.typeConverter = typeConverter;
        }
    }
}
