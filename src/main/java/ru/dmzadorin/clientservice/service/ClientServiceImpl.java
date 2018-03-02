package ru.dmzadorin.clientservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.dao.ClientDao;
import ru.dmzadorin.clientservice.model.Client;
import ru.dmzadorin.clientservice.model.exceptions.IncorrectPasswordException;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public class ClientServiceImpl implements ClientService {
    private static final Logger logger = LogManager.getLogger();

    private final ClientDao clientDao;
    private final PasswordHashService passwordHashService;

    public ClientServiceImpl(ClientDao clientDao, PasswordHashService passwordHashService) {
        this.clientDao = clientDao;
        this.passwordHashService = passwordHashService;
    }

    @Override
    public void registerClient(String login, String password) {
        logger.info("Trying to register new client with login: {}", login);
        clientDao.registerClient(login, passwordHashService.hashPassword(password));
    }

    @Override
    public double getClientBalance(String login, String password) {
        logger.info("Searching for client with login: {}", login);
        Client client = clientDao.getClient(login);
        comparePasswordHashes(login, client.getPasswordHash(), passwordHashService.hashPassword(password));
        return client.getBalance();
    }

    private void comparePasswordHashes(String login, String expectedHash, String actualHash) {
        if (!expectedHash.equals(actualHash)) {
            logger.warn("Incorrect password for client with login: {}, hashes don't match", login);
            throw new IncorrectPasswordException(login);
        }
    }
}
