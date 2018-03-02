package ru.dmzadorin.clientservice.net;

import ru.dmzadorin.clientservice.annotation.PostMethod;
import ru.dmzadorin.clientservice.model.request.RequestType;
import ru.dmzadorin.clientservice.model.response.ResponseType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class RequestDispatcher {
    private final Function<InputStream, RequestType> requestDeserializer;

    public RequestDispatcher(Function<InputStream, RequestType> requestDeserializer, Object... controllers) {
        this.requestDeserializer = requestDeserializer;
    }

    public void scanControllers(Object... controllers) {
        for (Object controller : controllers) {
            Class<?> clazz = controller.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                PostMethod postMethod = method.getDeclaredAnnotation(PostMethod.class);
                if (postMethod != null) {
                    String methodName = postMethod.name();
                }
            }
        }


    }

    public ResponseType handleRequest(InputStream requestBody) {
        try {
            RequestType requestType = requestDeserializer.apply(requestBody);
        } finally {
            closeQuietly(requestBody);
        }
        return null;
    }

    private void closeQuietly(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignore) {
            }
        }
    }
}
