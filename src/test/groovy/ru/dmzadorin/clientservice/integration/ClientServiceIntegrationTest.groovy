package ru.dmzadorin.clientservice.integration

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import spock.lang.Shared
import spock.lang.Specification

class ClientServiceIntegrationTest extends Specification {
    @Shared
    HttpServer httpServer

    @Shared
    URL url

    void setupSpec() {
        httpServer = HttpServer.create()
        httpServer.bind(new InetSocketAddress(8080), 1)
        httpServer.createContext("/", new HttpHandler() {
            @Override
            void handle(HttpExchange httpExchange) throws IOException {

            }
        })
        url = new URL("http://localhost:8080/")
    }

    void cleanupSpec() {
        httpServer.stop(1)
    }

    String sendRequest(String request) {
        def sb = new StringBuilder()
        try {
            URLConnection connection = url.openConnection()
            connection.setDoOutput(true)
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())
            out.write(request)
            out.close()
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))

            def inputString = ''
            while ((inputString = input.readLine()) != null) {
                sb.append(inputString)
            }
            input.close();
        } catch (IOException e){
            throw new IllegalStateException(e)
        }
        sb.toString()
    }
}
