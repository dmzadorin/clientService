package ru.dmzadorin.clientservice.dao;

import ru.dmzadorin.clientservice.model.Client;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public interface ClientDao {
    void registerClient(String login, String passwordHash);

    Client getClient(String login);
}
