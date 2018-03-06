package ru.dmzadorin.clientservice.net;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.model.response.ResponseType;

import java.io.IOException;
import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class ResponseHandler {
    private static final Logger logger = LogManager.getLogger();
    private static final int SUCCESS_CODE = 200;
    private static final int METHOD_NOT_SUPPORTED_CODE = 405;
    private final Function<ResponseType, String> responseSerializer;
    private final Function<Exception, ResponseType> exceptionMapper;

    public ResponseHandler(Function<ResponseType, String> responseSerializer,
                           Function<Exception, ResponseType> exceptionMapper) {
        this.responseSerializer = responseSerializer;
        this.exceptionMapper = exceptionMapper;
    }

    public void handleException(Exception e, HttpExchange httpExchange) {
        ResponseType exceptionResponse = exceptionMapper.apply(e);
        writeSuccessResponse(exceptionResponse, httpExchange);
    }

    public void writeSuccessResponse(ResponseType response, HttpExchange httpExchange) {
        writeResponse(response, SUCCESS_CODE, httpExchange);
    }

    public void writeMethodNotSupported(HttpExchange httpExchange) {
        ResponseType responseType = new ResponseType();
        responseType.setResultCode(2);
        writeResponse(responseType, METHOD_NOT_SUPPORTED_CODE, httpExchange);
    }

    private void writeResponse(ResponseType response, int httpCode, HttpExchange httpExchange) {
        String s = responseSerializer.apply(response);
        try {
            byte[] bytes = s.getBytes();
            httpExchange.sendResponseHeaders(httpCode, bytes.length);
            httpExchange.getResponseBody().write(bytes);
        } catch (IOException e) {
            logger.error("Cannot write response");
        }
    }
}
