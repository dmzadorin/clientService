package ru.dmzadorin.clientservice.dao

import ru.dmzadorin.clientservice.config.ApplicationConfig
import ru.dmzadorin.clientservice.model.exceptions.ClientAlreadyExistException
import ru.dmzadorin.clientservice.model.exceptions.ClientNotExistException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ClientDaoImplTest extends Specification {
    @Shared
    ClientDao clientDao

    void setupSpec() {
        ApplicationConfig config = new ApplicationConfig();
        clientDao = new ClientDaoImpl(config.getDataSource())
    }

    @Unroll
    def "verify that client with '#login' is saved properly"() {
        when:
        clientDao.registerClient(login, password)
        then:
        noExceptionThrown()
        where:
        login   | password | exceptionClass
        'test'  | 'pass'   | null
        'test2' | 'pass2'  | null
    }

    @Unroll
    def "verify that client with '#login' is already existing"() {
        when:
        clientDao.registerClient(login, password)
        then:
        thrown(ClientAlreadyExistException)
        where:
        login   | password
        'test'  | 'pass'
        'test2' | 'pass'
    }

    def "verify that client not exist"() {
        when:
        clientDao.getClient('not_exist')
        then:
        thrown(ClientNotExistException)
    }

    @Unroll
    def "verify that client with '#login' is saved and retrieved properly"() {
        when:
        clientDao.registerClient(login, password)
        def client = clientDao.getClient(login)
        then:
        client.login == login
        client.passwordHash == password
        client.balance == 0.0d
        where:
        login      | password
        'testsave' | 'pass'
    }
}
