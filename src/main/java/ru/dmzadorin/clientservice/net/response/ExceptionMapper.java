package ru.dmzadorin.clientservice.net.response;

import ru.dmzadorin.clientservice.model.exceptions.ApplicationException;
import ru.dmzadorin.clientservice.model.response.ResponseType;

import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class ExceptionMapper implements Function<Exception, ResponseType> {
    private static final int DEFAULT_ERROR_CODE = 2;

    @Override
    public ResponseType apply(Exception e) {
        if (e instanceof ApplicationException) {
            return buildResponse(((ApplicationException) e).getResultCode());
        } else {
            return buildResponse(DEFAULT_ERROR_CODE);
        }
    }

    private ResponseType buildResponse(int code) {
        ResponseType response = new ResponseType();
        response.setResultCode(code);
        return response;
    }
}
