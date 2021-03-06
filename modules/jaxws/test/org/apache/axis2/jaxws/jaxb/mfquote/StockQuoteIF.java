
package org.apache.axis2.jaxws.jaxb.mfquote;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_01-b15-fcs
 * Generated source version: 2.0
 * 
 */
@WebService(name = "StockQuoteIF", targetNamespace = "http://org/apache/axis2/jaxws/test")
public interface StockQuoteIF {


    /**
     * 
     * @param  10Holdings
     * @param fund
     * @param nav
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "http://tempuri.org/StockQuote/getPrice")
    @WebResult(name = "price", targetNamespace = "")
    @RequestWrapper(localName = "getPrice", targetNamespace = "urn://stock1.test.org", className = "org.test.stock1.GetPrice")
    @ResponseWrapper(localName = "getPriceResponse", targetNamespace = "urn://stock1.test.org", className = "org.test.stock1.StockPrice")
    public String getPrice(
        @WebParam(name = "fund", targetNamespace = "")
        String fund,
        @WebParam(name = "_10holdings", targetNamespace = "")
        String _10Holdings,
        @WebParam(name = "nav", targetNamespace = "")
        String nav);

}
