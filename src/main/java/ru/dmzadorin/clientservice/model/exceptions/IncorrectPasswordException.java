package ru.dmzadorin.clientservice.model.exceptions;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public class IncorrectPasswordException extends ApplicationException {
    public IncorrectPasswordException(String login) {
        super("Password for client with login '" + login + "' is incorrect");
    }

    @Override
    int getResultCode() {
        return 4;
    }
}
