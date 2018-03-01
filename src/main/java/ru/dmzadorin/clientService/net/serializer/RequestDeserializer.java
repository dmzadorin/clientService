package ru.dmzadorin.clientService.net.serializer;

import ru.dmzadorin.clientService.model.request.RequestType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class RequestDeserializer implements Function<InputStream, RequestType> {
    private final JAXBContext jaxbContext;

    public RequestDeserializer() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(RequestType.class);
    }

    @Override
    public RequestType apply(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (RequestType) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Cannot parse input request", e);
        }
    }
}
