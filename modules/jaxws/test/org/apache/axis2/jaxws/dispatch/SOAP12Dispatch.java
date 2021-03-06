/*
 * Copyright 2006 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis2.jaxws.dispatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;

import junit.framework.TestCase;

/**
 * This class uses the JAX-WS Dispatch API to test sending and receiving
 * messages using SOAP 1.2.
 */
public class SOAP12Dispatch extends TestCase {
    
    private static final QName QNAME_SERVICE = new QName(
            "http://org/apache/axis2/jaxws/test/SOAP12", "SOAP12Service");
    private static final QName QNAME_PORT = new QName(
            "http://org/apache/axis2/jaxws/test/SOAP12", "SOAP12Port");
    private static final String URL_ENDPOINT = "http://localhost:8080/axis2/services/SOAP12ProviderService";    
    
    private static final String sampleRequest = 
        "<test:echoString xmlns:test=\"http://org/apache/axis2/jaxws/test/SOAP12\">" +
        "<test:input>SAMPLE REQUEST MESSAGE</test:input>" +
        "</test:echoString>";
    private static final String sampleEnvelopeHead = 
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
        "<soapenv:Header /><soapenv:Body>";
    private static final String sampleEnvelopeTail = 
        "</soapenv:Body></soapenv:Envelope>";
    private static final String sampleEnvelope = 
        sampleEnvelopeHead + 
        sampleRequest + 
        sampleEnvelopeTail;
    
    public SOAP12Dispatch(String name) {
        super(name);
    }
    
    /**
     * Test sending a SOAP 1.2 request in PAYLOAD mode
     */
    public void testSOAP12DispatchPayloadMode() throws Exception {
        // Create the JAX-WS client needed to send the request
        Service service = Service.create(QNAME_SERVICE);
        service.addPort(QNAME_PORT, SOAPBinding.SOAP12HTTP_BINDING, URL_ENDPOINT);
        Dispatch<Source> dispatch = service.createDispatch(
                QNAME_PORT, Source.class, Mode.PAYLOAD);
        
        // Create the Source object with the payload contents.  Since
        // we're in PAYLOAD mode, we don't have to worry about the envelope.
        byte[] bytes = sampleRequest.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        StreamSource request = new StreamSource(bais);
        
        // Send the SOAP 1.2 request
        Source response = dispatch.invoke(request);
        
        // Convert the response to a more consumable format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer trans = factory.newTransformer();
        trans.transform(response, result);
        
        // Check to make sure the contents are correct.  Again, since we're
        // in PAYLOAD mode, we shouldn't have anything related to the envelope
        // in the return, just the contents of the Body.
        String responseText = baos.toString();
        assertTrue(!responseText.contains("soap"));
        assertTrue(!responseText.contains("Envelope"));
        assertTrue(!responseText.contains("Body"));
        assertTrue(responseText.contains("echoStringResponse"));        
    }
    
    /**
     * Test sending a SOAP 1.2 request in MESSAGE mode
     */
    public void testSOAP12DispatchMessageMode() throws Exception {
        // Create the JAX-WS client needed to send the request
        Service service = Service.create(QNAME_SERVICE);
        service.addPort(QNAME_PORT, SOAPBinding.SOAP12HTTP_BINDING, URL_ENDPOINT);
        Dispatch<Source> dispatch = service.createDispatch(
                QNAME_PORT, Source.class, Mode.MESSAGE);
        
        // Create the Source object with the message contents.  Since
        // we're in MESSAGE mode, we'll need to make sure we create this
        // with the right protocol.
        byte[] bytes = sampleEnvelope.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        StreamSource request = new StreamSource(bais);
        
        Source response = dispatch.invoke(request);
        
        // Convert the response to a more consumable format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer trans = factory.newTransformer();
        trans.transform(response, result);
        
        // Check to make sure the contents of the message are correct
        String responseText = baos.toString();
        assertTrue(responseText.contains("soap"));
        assertTrue(responseText.contains("Body"));
        assertTrue(responseText.contains("Envelope"));
        assertTrue(responseText.contains("echoStringResponse"));
        
        // Check to make sure the message returned had the right protocol version
        // TODO: Need to determine whether or not we should be using the hard 
        // coded URLs here, or whether we should be using a constant for the 
        // purposes of the test.
        assertTrue(responseText.contains("http://www.w3.org/2003/05/soap-envelope"));
        assertTrue(!responseText.contains("http://schemas.xmlsoap.org/soap/envelope"));
    }
}
