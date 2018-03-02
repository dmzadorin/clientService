package ru.dmzadorin.clientservice.model.exceptions;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message, null, true, false);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

    public abstract int getResultCode();
}
