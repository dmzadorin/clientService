package ru.dmzadorin.clientService;

import com.sun.net.httpserver.HttpServer;
import ru.dmzadorin.clientService.net.RequestDispatcher;
import ru.dmzadorin.clientService.net.ResponseHandler;
import ru.dmzadorin.clientService.net.ExceptionMapper;
import ru.dmzadorin.clientService.net.ClientServiceHttpHandler;
import ru.dmzadorin.clientService.net.serializer.RequestDeserializer;
import ru.dmzadorin.clientService.net.serializer.ResponseSerializer;

import java.net.InetSocketAddress;

/**
 * Created by Dmitry Zadorin on 01.03.2018
 */
public class App {
    public static void main(String[] args) throws Exception {
        HttpServer httpServer = HttpServer.create();
        try {
            httpServer.bind(new InetSocketAddress(9999), 0);
            RequestDispatcher dispatcher = new RequestDispatcher(new RequestDeserializer());
            ResponseHandler handler = new ResponseHandler(new ResponseSerializer(), new ExceptionMapper());
            httpServer.createContext("/", new ClientServiceHttpHandler(dispatcher, handler));
            System.out.println("Running http server on port 9999");
            httpServer.start();
            System.out.println("Press any key to stop server");
            System.in.read();
        } finally {
            httpServer.stop(3);
        }
    }
}
