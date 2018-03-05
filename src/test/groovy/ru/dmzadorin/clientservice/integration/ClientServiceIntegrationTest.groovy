package ru.dmzadorin.clientservice.integration

import com.sun.net.httpserver.HttpServer
import ru.dmzadorin.clientservice.config.ApplicationConfig
import ru.dmzadorin.clientservice.model.request.ExtraType
import ru.dmzadorin.clientservice.model.request.RequestType
import ru.dmzadorin.clientservice.model.response.ResponseType
import ru.dmzadorin.clientservice.net.ClientServiceHttpHandler
import spock.lang.Shared
import spock.lang.Specification

import javax.xml.bind.JAXBContext

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

    def "Register client and request balance"() {
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
        noExceptionThrown()
        clientCreateResponse.resultCode == 0
        balanceResponse.resultCode == 0
        balanceResponse.extra.name == 'balance'
        balanceResponse.extra.value == '0.0'
    }

    def "Try to register client with existing login"() {
        given:
        def createClient = new RequestType()
        createClient.requestType = 'CREATE-AGT'
        createClient.getExtra().add(new ExtraType(name: 'login', value: 'testlogin'))
        createClient.getExtra().add(new ExtraType(name: 'password', value: 'pass'))
        when:
        def clientCreateResponse = sendRequest(createClient)
        then:
        noExceptionThrown()
        clientCreateResponse.resultCode == 1
    }

    def "Get balance for non existing client"() {
        given:
        def getBalance = new RequestType()
        getBalance.requestType = 'GET-BALANCE'
        getBalance.getExtra().add(new ExtraType(name: 'login', value: 'testlogin2'))
        getBalance.getExtra().add(new ExtraType(name: 'password', value: 'pass'))
        when:
        def clientCreateResponse = sendRequest(getBalance)
        then:
        noExceptionThrown()
        clientCreateResponse.resultCode == 3
    }

    def "Get balance for client with incorrect password"() {
        given:
        def getBalance = new RequestType()
        getBalance.requestType = 'GET-BALANCE'
        getBalance.getExtra().add(new ExtraType(name: 'login', value: 'testlogin'))
        getBalance.getExtra().add(new ExtraType(name: 'password', value: 'pass2'))
        when:
        def clientCreateResponse = sendRequest(getBalance)
        then:
        noExceptionThrown()
        clientCreateResponse.resultCode == 4
    }

    void cleanupSpec() {
        httpServer.stop(1)
    }

    ResponseType sendRequest(RequestType request) {
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
            jaxbContext = JAXBContext.newInstance(ResponseType.class);
            def unMarshaller = jaxbContext.createUnmarshaller()
            def inputStream = connection.getInputStream()
            def resp = unMarshaller.unmarshal(inputStream)
            inputStream.close();
            resp
        } catch (Exception e) {
            throw new IllegalStateException(e)
        } finally {
            connection?.disconnect()
        }
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
