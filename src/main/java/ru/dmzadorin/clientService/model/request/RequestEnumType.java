
package ru.dmzadorin.clientService.model.request;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for request-enum-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="request-enum-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CREATE-AGT"/>
 *     &lt;enumeration value="GET-BALANCE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "request-enum-type")
@XmlEnum
public enum RequestEnumType {

    @XmlEnumValue("CREATE-AGT")
    CREATE_AGT("CREATE-AGT"),
    @XmlEnumValue("GET-BALANCE")
    GET_BALANCE("GET-BALANCE");
    private final String value;

    RequestEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RequestEnumType fromValue(String v) {
        for (RequestEnumType c: RequestEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
