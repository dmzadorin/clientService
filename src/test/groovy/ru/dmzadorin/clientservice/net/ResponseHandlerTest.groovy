package ru.dmzadorin.clientservice.net

import com.sun.net.httpserver.HttpExchange
import org.junit.Assert
import ru.dmzadorin.clientservice.model.response.ResponseType
import ru.dmzadorin.clientservice.net.response.ResponseHandlerImpl
import spock.lang.Specification

import java.util.function.Function

/**
 * Created by Dmitry Zadorin on 07.03.2018
 */
class ResponseHandlerTest extends Specification {
    def "Verify that exception is correctly serialized in http response stream"() {
        given:
        def serializer = Mock(Function)
        def exceptionMapper = Mock(Function)
        def responseHandler = new ResponseHandlerImpl(serializer, exceptionMapper)
        def ex = new Exception()
        def errResponse = new ResponseType(resultCode: 2)
        def httpExchange = Mock(HttpExchange)
        def response = "test"
        def stream = Mock(OutputStream)
        when:
        responseHandler.handleException(ex, httpExchange)
        then:
        1 * exceptionMapper.apply(ex) >> errResponse
        1 * serializer.apply(errResponse) >> response
        1 * httpExchange.sendResponseHeaders(200, response.getBytes().length);
        1 * httpExchange.getResponseBody() >> stream
        1 * stream.write(response.getBytes())
    }

    def "Verify success response is written"() {
        given:
        def serializer = Mock(Function)
        def exceptionMapper = Mock(Function)
        def responseHandler = new ResponseHandlerImpl(serializer, exceptionMapper)
        def success = new ResponseType(resultCode: 0)
        def httpExchange = Mock(HttpExchange)
        def response = "success"
        def stream = Mock(OutputStream)
        when:
        responseHandler.writeSuccessResponse(success, httpExchange)
        then:
        1 * serializer.apply(success) >> response
        1 * httpExchange.sendResponseHeaders(200, response.getBytes().length);
        1 * httpExchange.getResponseBody() >> stream
        1 * stream.write(response.getBytes())
    }

    def "Verify response with code 2 is written"() {
        given:
        def serializer = Mock(Function)
        def exceptionMapper = Mock(Function)
        def responseHandler = new ResponseHandlerImpl(serializer, exceptionMapper)
        def httpExchange = Mock(HttpExchange)
        def response = "success"
        def stream = Mock(OutputStream)
        when:
        responseHandler.writeMethodNotSupported(httpExchange)
        then:
        1 * serializer.apply(_) >> {
            def type = it[0] as ResponseType
            Assert.assertEquals(type.resultCode, 2)
            response
        }
        1 * httpExchange.sendResponseHeaders(405, response.getBytes().length);
        1 * httpExchange.getResponseBody() >> stream
        1 * stream.write(response.getBytes())
    }
}
