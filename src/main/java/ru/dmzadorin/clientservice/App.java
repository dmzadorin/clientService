package ru.dmzadorin.clientservice;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dmzadorin.clientservice.config.ApplicationConfig;
import ru.dmzadorin.clientservice.net.ClientServiceHttpHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public class App {
    private static final Logger logger = LogManager.getLogger();
    private static final int DEFAULT_PORT_VALUE = 9999;

    public static void main(String[] args) throws Exception {
        int port;
        if (args == null || args.length != 1) {
            logger.warn("Using default port value: {}", DEFAULT_PORT_VALUE);
            port = DEFAULT_PORT_VALUE;
        } else {
            port = Integer.parseInt(args[0]);
        }
        ApplicationConfig config = new ApplicationConfig();
        int threads = config.getThreadCount();
        logger.info("Using threads count {}", threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        HttpServer httpServer = HttpServer.create();
        try {
            httpServer.setExecutor(executorService);
            httpServer.bind(new InetSocketAddress(port), 0);
            httpServer.createContext("/", new ClientServiceHttpHandler(config));
            logger.info("Running http server on port: {}", port);
            httpServer.start();
            logger.info("Press any key to stop server");
            System.in.read();
            logger.info("Stopping http server....");
        } finally {
            executorService.shutdown();
            httpServer.stop(1);
        }
        logger.info("Server stopped");
    }
}
