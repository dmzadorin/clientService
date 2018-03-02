package ru.dmzadorin.clientservice.model.exceptions;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public class ClientNotExistException extends ApplicationException {
    public ClientNotExistException(String login) {
        super("Client with login '" + login + " does not exist");
    }

    @Override
    public int getResultCode() {
        return 3;
    }
}
