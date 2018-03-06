package ru.dmzadorin.clientservice.dao;

import ru.dmzadorin.clientservice.model.Client;
import ru.dmzadorin.clientservice.model.exceptions.ClientAlreadyExistException;
import ru.dmzadorin.clientservice.model.exceptions.ClientNotExistException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public interface ClientDao {
    /**
     * Registers new client if he is not present in database
     *
     * @param login        login of new client
     * @param passwordHash hashed password
     * @throws ClientAlreadyExistException if client with specified login is already present in database
     */
    void registerClient(String login, String passwordHash) throws ClientAlreadyExistException;

    /**
     * Retrieves client by login
     *
     * @param login login to search
     * @return client
     * @throws ClientNotExistException if client with login is not found
     */
    Client getClient(String login) throws ClientNotExistException;
}
