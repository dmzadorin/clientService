package ru.dmzadorin.clientService.net;

import com.sun.net.httpserver.HttpExchange;
import ru.dmzadorin.clientService.model.response.ResponseType;

import java.io.IOException;
import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class ResponseHandler {
    public static final int SUCCESS_CODE = 200;
    public static final int METHOD_NOT_SUPPORTED_CODE = 405;
    private final Function<ResponseType, String> responseSerializer;
    private final Function<Exception, ResponseType> exceptionHandler;

    public ResponseHandler(Function<ResponseType, String> responseSerializer,
                           Function<Exception, ResponseType> exceptionHandler) {
        this.responseSerializer = responseSerializer;
        this.exceptionHandler = exceptionHandler;
    }

    public void handleException(Exception e, HttpExchange httpExchange) {
        ResponseType exceptionResponse = exceptionHandler.apply(e);
        writeSuccessResponse(exceptionResponse, httpExchange);
    }

    public void writeSuccessResponse(ResponseType response, HttpExchange httpExchange) {
        writeResponse(response, SUCCESS_CODE, httpExchange);
    }

    public void writeMethodNotSupported(HttpExchange httpExchange) {
        ResponseType responseType = new ResponseType();
        responseType.setResultCode(4);
        writeResponse(responseType, METHOD_NOT_SUPPORTED_CODE, httpExchange);
    }

    private void writeResponse(ResponseType response, int httpCode, HttpExchange httpExchange) {
        String s = responseSerializer.apply(response);
        try {
            byte[] bytes = s.getBytes();
            httpExchange.sendResponseHeaders(httpCode, bytes.length);
            httpExchange.getResponseBody().write(bytes);
        } catch (IOException e) {
            System.out.println("Cannot write response");
        }
    }
}
