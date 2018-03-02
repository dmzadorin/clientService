package ru.dmzadorin.h2;

import org.h2.tools.Server;
import ru.dmzadorin.clientservice.config.DataSourceConfigurer;

import java.sql.SQLException;

public class H2Server {
    private static final int DEFAULT_PORT_VALUE = 9092;

    public static void main(String[] args) throws Exception {
        int port;
        if (args == null || args.length != 1) {
            System.out.println("Using default port value: " + DEFAULT_PORT_VALUE);
            port = DEFAULT_PORT_VALUE;
        } else {
            port = Integer.parseInt(args[0]);
        }
        Server server = Server.createTcpServer("-tcpPort", String.valueOf(port), "-tcpAllowOthers");
        try {
            server.start();
            System.out.println("Started H2 server, press any key to stop");
            initDatabase(port);
            System.in.read();
            System.out.println("Stopping H2 server");
        } finally {
            server.stop();
        }
    }

    private static void initDatabase(int port) throws SQLException {
        String url = "jdbc:h2:tcp://localhost:" + port + "/mem:clientservice;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String pass = "sa";
        String initScriptPath = "classpath:/database/schema.sql";
        DataSourceConfigurer.initDataSource(url, user, pass, initScriptPath);
        System.out.println("Database under url " + url + " has been initialized");
    }
}
