package ru.dmzadorin.clientService.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.dmzadorin.clientService.model.response.ResponseType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public class ClientServiceHttpHandler implements HttpHandler {
    public static final String POST_METHOD = "POST";
    private final RequestDispatcher dispatcher;
    private final ResponseHandler responseHandler;

    public ClientServiceHttpHandler(RequestDispatcher dispatcher, ResponseHandler responseHandler) {
        this.dispatcher = dispatcher;
        this.responseHandler = responseHandler;
    }


    @Override
    public void handle(HttpExchange httpExchange) {
        String requestMethod = httpExchange.getRequestMethod();
        try {
            if (POST_METHOD.equalsIgnoreCase(requestMethod)) {
                InputStream requestBody = httpExchange.getRequestBody();
                ResponseType response = dispatcher.handleRequest(requestBody);
                responseHandler.writeSuccessResponse(response, httpExchange);
            } else {
                responseHandler.writeMethodNotSupported(httpExchange);
            }
        } catch (Exception e) {
            responseHandler.handleException(e, httpExchange);
        } finally {
            closeQuietly(httpExchange.getResponseBody());
        }
    }

    private void closeQuietly(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException ignore) {
            }
        }
    }
}
