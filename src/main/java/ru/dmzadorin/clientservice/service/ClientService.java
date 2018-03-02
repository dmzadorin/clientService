package ru.dmzadorin.clientservice.service;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public interface ClientService {
    void registerClient(String login, String password);
    double getClientBalance(String login, String password);
}
