
package ru.dmzadorin.clientService.model.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for responseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="responseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result-code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="extra" type="{}extraType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "responseType", propOrder = {
    "resultCode",
    "extra"
})
public class ResponseType {

    @XmlElement(name = "result-code")
    protected int resultCode;
    @XmlElement(required = true)
    protected ExtraType extra;

    /**
     * Gets the value of the resultCode property.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the extra property.
     * 
     * @return
     *     possible object is
     *     {@link ExtraType }
     *     
     */
    public ExtraType getExtra() {
        return extra;
    }

    /**
     * Sets the value of the extra property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtraType }
     *     
     */
    public void setExtra(ExtraType value) {
        this.extra = value;
    }

}