package ru.dmzadorin.clientservice;

import com.sun.net.httpserver.HttpServer;
import ru.dmzadorin.clientservice.config.ApplicationConfig;
import ru.dmzadorin.clientservice.net.ClientServiceHttpHandler;

import java.net.InetSocketAddress;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public class App {
    public static void main(String[] args) throws Exception {
        HttpServer httpServer = HttpServer.create();
        try {
            httpServer.bind(new InetSocketAddress(9999), 0);
            ApplicationConfig config = new ApplicationConfig();
            httpServer.createContext("/", new ClientServiceHttpHandler(config));
            System.out.println("Running http server on port 9999");
            httpServer.start();
            System.out.println("Press any key to stop server");
            System.in.read();
        } finally {
            httpServer.stop(3);
        }
    }
}
