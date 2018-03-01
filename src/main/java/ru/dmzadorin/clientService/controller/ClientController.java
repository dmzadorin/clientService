package ru.dmzadorin.clientService.controller;

import ru.dmzadorin.clientService.annotation.PostMethod;
import ru.dmzadorin.clientService.annotation.RequestParam;
import ru.dmzadorin.clientService.service.ClientService;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMethod(name = "CREATE-AGT")
    public void registerClient(@RequestParam(name = "login") String login, @RequestParam(name = "password") String password) {
        service.registerClient(login, password.toCharArray());
    }

    @PostMethod(name = "GET-BALANCE", returnParamName = "balance")
    public double getBalance(String login, String password) {
        return service.getBalance(login, password.toCharArray());
    }}