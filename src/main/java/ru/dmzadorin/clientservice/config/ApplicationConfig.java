package ru.dmzadorin.clientservice.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.controller.ClientController;
import ru.dmzadorin.clientservice.dao.ClientDao;
import ru.dmzadorin.clientservice.dao.ClientDaoImpl;
import ru.dmzadorin.clientservice.net.request.RequestDispatcher;
import ru.dmzadorin.clientservice.net.response.ExceptionMapper;
import ru.dmzadorin.clientservice.net.request.RequestDispatcherImpl;
import ru.dmzadorin.clientservice.net.response.ResponseHandler;
import ru.dmzadorin.clientservice.net.response.ResponseHandlerImpl;
import ru.dmzadorin.clientservice.net.request.TypeConverter;
import ru.dmzadorin.clientservice.net.serializer.RequestDeserializer;
import ru.dmzadorin.clientservice.net.serializer.ResponseSerializer;
import ru.dmzadorin.clientservice.service.ClientService;
import ru.dmzadorin.clientservice.service.ClientServiceImpl;
import ru.dmzadorin.clientservice.service.Sha1PasswordHashService;

import javax.sql.DataSource;
import java.io.FileInputStream;
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
    private static final Logger logger = LogManager.getLogger();
    private final Properties properties;
    private final DataSource dataSource;
    private final int threadCount;
    private final ClientService clientService;
    private final ClientDao clientDao;
    private final RequestDispatcher requestDispatcher;
    private final ResponseHandler responseHandler;

    public ApplicationConfig() throws Exception {
        this.properties = readProperties();
        this.dataSource = configureDataSource(properties);
        this.threadCount = parseThreadCountProperty(properties.getProperty("threadCount"));
        this.clientDao = new ClientDaoImpl(dataSource);
        this.clientService = new ClientServiceImpl(clientDao, new Sha1PasswordHashService());
        Map<Type, TypeConverter> typeConverterMap = new HashMap<>();
        typeConverterMap.put(String.class, s -> s);
        typeConverterMap.put(Integer.class, Integer::parseInt);
        typeConverterMap.put(Double.class, Double::parseDouble);
        typeConverterMap.put(Boolean.class, Boolean::parseBoolean);

        requestDispatcher = new RequestDispatcherImpl(new RequestDeserializer(), typeConverterMap,
                new ClientController(clientService));
        responseHandler = new ResponseHandlerImpl(new ResponseSerializer(), new ExceptionMapper());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public int getThreadCount() {
        return threadCount;
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
        try (InputStream is = getPropertiesStream()) {
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load application properties", e);
        }
        return properties;
    }

    private static InputStream getPropertiesStream() throws IOException {
        String configLocation = System.getProperty("config.location");
        if (configLocation != null) {
            logger.info("Loading config from external file: {}", configLocation);
            return new FileInputStream(configLocation);
        } else {
            return ApplicationConfig.class.getClassLoader().getResourceAsStream("config.properties");
        }
    }

    private static int parseThreadCountProperty(String threads) {
        if (threads != null) {
            return Integer.parseInt(threads);
        } else {
            return Runtime.getRuntime().availableProcessors();
        }
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
