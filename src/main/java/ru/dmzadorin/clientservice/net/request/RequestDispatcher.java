package ru.dmzadorin.clientservice.net.request;

import ru.dmzadorin.clientservice.model.response.ResponseType;

import java.io.InputStream;

/**
 * Created by Dmitry Zadorin on 07.03.2018
 */
public interface RequestDispatcher {
    /**
     * Parses request body into xml RequestType and dispatches that request to appropriate controller
     *
     * @param requestBody body of the request
     * @return response generated by controller
     */
    ResponseType handleRequest(InputStream requestBody);
}
