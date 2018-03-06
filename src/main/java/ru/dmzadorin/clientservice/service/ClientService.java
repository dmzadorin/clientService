package ru.dmzadorin.clientservice.service;

import ru.dmzadorin.clientservice.model.exceptions.IncorrectPasswordException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public interface ClientService {
    /**
     * Registers new client. First hashes incoming password and calls dao to save client in db
     *
     * @param login    login of new client
     * @param password password of new client
     */
    void registerClient(String login, String password);

    /**
     * Searches for client with particular login, compares passwords.
     * If passwords match returns client balance. In other case throws IncorrectPasswordException
     *
     * @param login    client login
     * @param password client password
     * @return balance of found client
     * @throws IncorrectPasswordException - if input password and stored password do not match
     */
    double getClientBalance(String login, String password) throws IncorrectPasswordException;
}
