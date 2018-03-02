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
                BufferedReader input = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()))
                def inputString = ''
                def sb = new StringBuilder()
                while ((inputString = input.readLine()) != null) {
                    sb.append(inputString)
                }
                println("Input request: $sb")
                def builder = new StringBuilder()
                builder.append("RESPONSE!")
                byte[] bytes = builder.toString().getBytes();
                httpExchange.sendResponseHeaders(200, bytes.length);

                OutputStream os = httpExchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        })
        httpServer.start()
        url = new URL("http://localhost:8080/")
    }

    def "Simple request response test"() {
        when:
        def response = sendRequest("request")
        then:
        response != null
        noExceptionThrown()
    }

    void cleanupSpec() {
        httpServer.stop(1)
    }

    String sendRequest(String request) {
        def sb = new StringBuilder()
        HttpURLConnection connection = null
        try {
            connection = initConnection()

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())
            out.write(request)
            out.close()

            connection.connect()
            int statusCode = connection.getResponseCode();

            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))

            def inputString = ''
            while ((inputString = input.readLine()) != null) {
                sb.append(inputString)
            }
            input.close();
        } catch (IOException e) {
            throw new IllegalStateException(e)
        } finally {
            connection?.disconnect()
        }
        sb.toString()
    }

    def HttpURLConnection initConnection() throws IOException {
        HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setDoOutput( true );
        connection.setDoInput( true );
        connection.setInstanceFollowRedirects( false );
        return connection;
    }
}
