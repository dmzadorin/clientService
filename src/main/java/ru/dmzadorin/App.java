package ru.dmzadorin;

import com.sun.net.httpserver.HttpServer;
import ru.dmzadorin.clientservice.config.ApplicationConfig;
import ru.dmzadorin.clientservice.service.ClientService;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            ApplicationConfig config = new ApplicationConfig();
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(0), 1);

            ClientService clientService = config.getClientService();
            clientService.registerClient("test", "pwd");
            clientService.registerClient("test2", "pwd");
            double clientBalance = clientService.getClientBalance("test", "pwd");
            double clientBalance2 = clientService.getClientBalance("test2", "pwd");
            System.out.println(clientBalance);
            System.out.println(clientBalance2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
