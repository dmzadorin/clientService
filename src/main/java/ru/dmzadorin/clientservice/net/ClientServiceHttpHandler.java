package ru.dmzadorin.clientservice.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.dmzadorin.clientservice.config.ApplicationConfig;
import ru.dmzadorin.clientservice.model.response.ResponseType;
import ru.dmzadorin.clientservice.net.request.RequestDispatcher;
import ru.dmzadorin.clientservice.net.response.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public class ClientServiceHttpHandler implements HttpHandler {
    private static final String POST_METHOD = "POST";
    private final RequestDispatcher dispatcher;
    private final ResponseHandler responseHandler;

    public ClientServiceHttpHandler(ApplicationConfig config) {
        this.dispatcher = config.getRequestDispatcher();
        this.responseHandler = config.getResponseHandler();
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
