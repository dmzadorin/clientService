package ru.dmzadorin.h2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.Server;
import ru.dmzadorin.clientservice.config.DataSourceConfigurer;

import java.sql.SQLException;

public class H2Server {
    private static final Logger logger = LogManager.getLogger();
    private static final int DEFAULT_PORT_VALUE = 9092;

    public static void main(String[] args) throws Exception {
        int port;
        if (args == null || args.length != 1) {
            logger.info("Using default port value: {}", DEFAULT_PORT_VALUE);
            port = DEFAULT_PORT_VALUE;
        } else {
            port = Integer.parseInt(args[0]);
        }
        Server server = Server.createTcpServer("-tcpPort", String.valueOf(port), "-tcpAllowOthers");
        try {
            server.start();
            logger.info("Started H2 server on port, press any key to stop...", port);
            initDatabase(port);
            System.in.read();
            logger.info("Stopping H2 server...");
        } finally {
            server.stop();
        }
        logger.info("H2 server stopped");
    }

    private static void initDatabase(int port) {
        String url = "jdbc:h2:tcp://localhost:" + port + "/mem:clientservice;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String pass = "sa";
        String initScriptPath = "classpath:/database/schema.sql";
        DataSourceConfigurer.initDataSource(url, user, pass, initScriptPath);
        logger.info("Database under url {} has been initialized", url);
    }
}
