package ru.dmzadorin.clientservice.config;

import ru.dmzadorin.clientservice.dao.ClientDao;
import ru.dmzadorin.clientservice.dao.ClientDaoImpl;
import ru.dmzadorin.clientservice.service.ClientService;
import ru.dmzadorin.clientservice.service.ClientServiceImpl;
import ru.dmzadorin.clientservice.service.Sha1PasswordHashService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public class ApplicationConfig {
    private final Properties properties;
    private final DataSource dataSource;
    private final ClientService clientService;
    private final ClientDao clientDao;

    public ApplicationConfig() {
        properties = readProperties();
        dataSource = configureDataSource(properties);
        clientDao = new ClientDaoImpl(dataSource);
        clientService = new ClientServiceImpl(clientDao, new Sha1PasswordHashService());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public ClientDao getClientDao() {
        return clientDao;
    }

    private static Properties readProperties() {
        Properties properties = new Properties();
        try {
            InputStream stream = ApplicationConfig.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load application properties");
        }
        return properties;
    }

    private static DataSource configureDataSource(Properties configProperties) {
        return DataSourceConfigurer.initDataSource(
                configProperties.getProperty("dbUrl"),
                configProperties.getProperty("dbUser"),
                configProperties.getProperty("dbPassword"),
                configProperties.getProperty("dbInitScript")
        );
    }
}
