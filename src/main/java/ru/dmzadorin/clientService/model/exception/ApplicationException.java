package ru.dmzadorin.clientService.model.exception;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public abstract class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getResultCode();
}
