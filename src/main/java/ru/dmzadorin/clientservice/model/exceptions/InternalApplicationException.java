package ru.dmzadorin.clientservice.model.exceptions;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public class InternalApplicationException extends ApplicationException {
    public InternalApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    int getResultCode() {
        return 2;
    }
}
