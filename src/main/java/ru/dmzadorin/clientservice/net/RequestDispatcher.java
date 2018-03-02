package ru.dmzadorin.clientservice.net;

import ru.dmzadorin.clientservice.model.request.RequestType;
import ru.dmzadorin.clientservice.model.response.ResponseType;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class RequestDispatcher {
    private final Function<InputStream, RequestType> requestDeserializer;

    public RequestDispatcher(Function<InputStream, RequestType> requestDeserializer) {
        this.requestDeserializer = requestDeserializer;
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
