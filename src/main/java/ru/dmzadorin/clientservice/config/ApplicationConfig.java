package ru.dmzadorin.clientservice.config;

import ru.dmzadorin.clientservice.controller.ClientController;
import ru.dmzadorin.clientservice.dao.ClientDao;
import ru.dmzadorin.clientservice.dao.ClientDaoImpl;
import ru.dmzadorin.clientservice.net.ExceptionMapper;
import ru.dmzadorin.clientservice.net.RequestDispatcher;
import ru.dmzadorin.clientservice.net.ResponseHandler;
import ru.dmzadorin.clientservice.net.TypeConverter;
import ru.dmzadorin.clientservice.net.serializer.RequestDeserializer;
import ru.dmzadorin.clientservice.net.serializer.ResponseSerializer;
import ru.dmzadorin.clientservice.service.ClientService;
import ru.dmzadorin.clientservice.service.ClientServiceImpl;
import ru.dmzadorin.clientservice.service.Sha1PasswordHashService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Dmitry Zadorin on 28.02.2018.
 */
public class ApplicationConfig {
    private final Properties properties;
    private final DataSource dataSource;
    private final ClientService clientService;
    private final ClientDao clientDao;
    private final RequestDispatcher requestDispatcher;
    private final ResponseHandler responseHandler;

    public ApplicationConfig() throws Exception{
        properties = readProperties();
        dataSource = configureDataSource(properties);
        clientDao = new ClientDaoImpl(dataSource);
        clientService = new ClientServiceImpl(clientDao, new Sha1PasswordHashService());
        Map<Type, TypeConverter> typeConverterMap = new HashMap<>();
        typeConverterMap.put(String.class, s -> s);
        typeConverterMap.put(Integer.class, Integer::parseInt);
        typeConverterMap.put(Double.class, Double::parseDouble);
        typeConverterMap.put(Boolean.class, Boolean::parseBoolean);

        requestDispatcher = new RequestDispatcher(new RequestDeserializer(), typeConverterMap, new ClientController(clientService));
        responseHandler = new ResponseHandler(new ResponseSerializer(), new ExceptionMapper());
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

    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
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
