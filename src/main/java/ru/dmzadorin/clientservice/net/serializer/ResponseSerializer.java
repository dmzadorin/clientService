package ru.dmzadorin.clientservice.net.serializer;

import ru.dmzadorin.clientservice.model.response.ResponseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.function.Function;

/**
 * Created by Dmitry Zadorin on 02.03.2018
 */
public class ResponseSerializer implements Function<ResponseType, String> {
    private final JAXBContext jaxbContext;

    public ResponseSerializer() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(ResponseType.class);
    }

    @Override
    public String apply(ResponseType responseType) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(responseType, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Failed to serialize response", e);
        }
    }
}
