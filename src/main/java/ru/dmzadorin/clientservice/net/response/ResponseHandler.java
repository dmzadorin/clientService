package ru.dmzadorin.clientservice.net.response;

import com.sun.net.httpserver.HttpExchange;
import ru.dmzadorin.clientservice.model.response.ResponseType;

/**
 * Created by Dmitry Zadorin on 07.03.2018
 */
public interface ResponseHandler {
    /**
     * Maps input exception into appropriate ResponseType object and serializes it to httpExchange
     *
     * @param e            exception to serialize
     * @param httpExchange http object containing output stream
     */
    void handleException(Exception e, HttpExchange httpExchange);

    /**
     * Writes down to response stream success response. That response typically has result code = 0 and one extra field.
     * Serializes it to httpExchange
     *
     * @param response     response object to serialize
     * @param httpExchange http object containing output stream
     */
    void writeSuccessResponse(ResponseType response, HttpExchange httpExchange);

    /**
     * Writes down to response stream ResponseType with result code 2 and http status code 405
     *
     * @param httpExchange http object containing output stream
     */
    void writeMethodNotSupported(HttpExchange httpExchange);
}
