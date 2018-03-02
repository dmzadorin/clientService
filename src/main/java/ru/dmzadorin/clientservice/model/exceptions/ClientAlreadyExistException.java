package ru.dmzadorin.clientservice.model.exceptions;

/**
 * Created by Dmitry Zadorin on 02.03.2018.
 */
public class ClientAlreadyExistException extends ApplicationException {

    public ClientAlreadyExistException(String login) {
        super("Client with login '" + login + "' already exist");
    }

    @Override
    public int getResultCode() {
        return 1;
    }
}
