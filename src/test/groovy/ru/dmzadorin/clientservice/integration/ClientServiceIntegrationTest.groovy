package ru.dmzadorin.clientservice.integration

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import ru.dmzadorin.clientservice.config.ApplicationConfig
import ru.dmzadorin.clientservice.model.request.ExtraType
import ru.dmzadorin.clientservice.model.request.RequestType
import ru.dmzadorin.clientservice.model.response.ResponseType
import ru.dmzadorin.clientservice.net.ClientServiceHttpHandler
import spock.lang.Shared
import spock.lang.Specification

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

class ClientServiceIntegrationTest extends Specification {
    @Shared
    HttpServer httpServer

    @Shared
    URL url

    void setupSpec() {
        httpServer = HttpServer.create()
        httpServer.bind(new InetSocketAddress(8080), 1)
        def config = new ApplicationConfig()
        httpServer.createContext("/", new ClientServiceHttpHandler(config))
        httpServer.start()
        url = new URL("http://localhost:8080/")
    }

    def "Simple request response test"() {
        given:
        def createClient = new RequestType()
        createClient.requestType = 'CREATE-AGT'
        createClient.getExtra().add(new ExtraType(name: 'login', value: 'testlogin'))
        createClient.getExtra().add(new ExtraType(name: 'password', value: 'pass'))

        def getBalance = new RequestType()
        getBalance.requestType = 'GET-BALANCE'
        getBalance.getExtra().add(new ExtraType(name: 'login', value: 'testlogin'))
        getBalance.getExtra().add(new ExtraType(name: 'password', value: 'pass'))
        when:
        def clientCreateResponse = sendRequest(createClient)
        def balanceResponse = sendRequest(getBalance)
        then:
        clientCreateResponse != null
        balanceResponse != null
        noExceptionThrown()
    }

    def buildRequest(RequestType request) {

        StringWriter
        marshaller.marshal(request,)
    }

    void cleanupSpec() {
        httpServer.stop(1)
    }

    String sendRequest(RequestType request) {
        def sb = new StringBuilder()
        HttpURLConnection connection = null
        try {
            connection = initConnection()

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())
            def jaxbContext = JAXBContext.newInstance(RequestType.class);
            def marshaller = jaxbContext.createMarshaller()
            marshaller.marshal(request, out)
            out.close()

            connection.connect()
            int statusCode = connection.getResponseCode();
            println("Status: $statusCode")
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))

            def inputString = ''
            while ((inputString = input.readLine()) != null) {
                sb.append(inputString)
            }
            input.close();
        } catch (Exception e) {
            throw new IllegalStateException(e)
        } finally {
            connection?.disconnect()
        }
        sb.toString()
    }

    def HttpURLConnection initConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        return connection;
    }
}
