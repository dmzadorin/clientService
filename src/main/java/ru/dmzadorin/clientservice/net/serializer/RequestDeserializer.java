package ru.dmzadorin.clientservice.net.serializer;

import ru.dmzadorin.clientservice.model.request.RequestType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class RequestDeserializer implements Function<InputStream, RequestType> {
    private final ThreadLocal<Unmarshaller> unmarshallerThreadLocal = new ThreadLocal<>();
    private final JAXBContext jaxbContext;

    public RequestDeserializer() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(RequestType.class);
    }

    @Override
    public RequestType apply(InputStream inputStream) {
        try {
            return (RequestType) getUnmarshaller().unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Cannot parse input request", e);
        }
    }

    private Unmarshaller getUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = unmarshallerThreadLocal.get();
        if (unmarshaller == null) {
            unmarshaller = jaxbContext.createUnmarshaller();
            unmarshallerThreadLocal.set(unmarshaller);
        }
        return unmarshaller;
    }
}
