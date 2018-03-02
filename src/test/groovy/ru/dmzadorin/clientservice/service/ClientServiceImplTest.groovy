package ru.dmzadorin.clientservice.service

import ru.dmzadorin.clientservice.dao.ClientDao
import ru.dmzadorin.clientservice.model.Client
import ru.dmzadorin.clientservice.model.exceptions.IncorrectPasswordException
import spock.lang.Shared
import spock.lang.Specification

class ClientServiceImplTest extends Specification {
    @Shared
    ClientDao clientDao

    @Shared
    ClientService clientService

    @Shared
    PasswordHashService passwordHashService

    void setup() {
        clientDao = Mock(ClientDao)
        passwordHashService = Mock(PasswordHashService)
        clientService = new ClientServiceImpl(clientDao, passwordHashService)
    }

    def "Verify new client is correctly registered"() {
        when:
        clientService.registerClient(login, password)
        then:
        1 * passwordHashService.hashPassword(password) >> passwordHash
        1 * clientDao.registerClient(login, passwordHash)
        where:
        login = 'test'
        password = 'pass'
        passwordHash = 'hash'
    }

    def "Correctly get client balance"() {
        when:
        def actualBalance = clientService.getClientBalance(login, password)
        then:
        1 * passwordHashService.hashPassword(password) >> passwordHash
        1 * clientDao.getClient(login) >> client
        actualBalance == client.balance
        where:
        login = 'test'
        password = 'pass'
        passwordHash = 'hash'
        client = new Client(login, passwordHash, 0.0)
    }

    def "Get password mismatch when trying to get balance"() {
        when:
        clientService.getClientBalance(login, password)
        then:
        1 * passwordHashService.hashPassword(password) >> passwordHash
        1 * clientDao.getClient(login) >> client
        thrown(IncorrectPasswordException)
        where:
        login = 'test'
        password = 'pass'
        passwordHash = 'hash'
        client = new Client(login, 'empty', 0.0)
    }
}
