package ru.dmzadorin.clientService.service;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public interface ClientService {

    void registerClient(String login, char[] password);

    double getBalance(String login, char[] password);
}
