/*
* Copyright 2004,2005 The Apache Software Foundation.
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


package org.apache.axis2.client;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.i18n.Messages;

/**
 * Base class for generated client stubs. This defines several client API
 * (<code>public</code>) methods shared between all types of stubs, along with
 * some <code>protected</code> methods intended for use by the actual stub
 * implementation code. The client API method names start with a leading
 * underscore character to avoid conflicts with actual implementation methods.
 */
public abstract class Stub {
    
    protected AxisService _service;
    protected ArrayList modules = new ArrayList();


    protected ServiceClient _serviceClient;

    /**
     * Get service client implementation used by this stub.
     * 
     * @return service client
     */
    public ServiceClient _getServiceClient() {
        return _serviceClient;
    }

    /**
     * Set service client implementation used by this stub. Once set, the
     * service client is owned by this stub and will automatically be removed
     * from the configuration when use of the stub is done.
     * 
     * @param _serviceClient
     */
    public void _setServiceClient(ServiceClient _serviceClient) {
        this._serviceClient = _serviceClient;
    }

    /**
     * Create a SOAP message envelope using the supplied options.
     * TODO generated stub code should use this method, or similar method taking
     * an operation client
     * 
     * @param options
     * @return generated 
     * @throws SOAPProcessingException
     */
    protected static SOAPEnvelope createEnvelope(Options options) throws SOAPProcessingException {
        return getFactory(options.getSoapVersionURI()).getDefaultEnvelope();
    }

    /**
     * Read a root element from the parser.
     * TODO generated stub code should use this method
     * 
     * @param reader
     * @return root element
     */
    protected static OMElement getElementFromReader(XMLStreamReader reader) {
        StAXOMBuilder builder =
                OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), reader);

        return builder.getDocumentElement();
    }

    /**
     * Get Axiom factory appropriate to selected SOAP version.
     * 
     * @param soapVersionURI
     * @return factory
     */
    protected static SOAPFactory getFactory(String soapVersionURI) {

        if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP11Factory();
        } else if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(soapVersionURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else {
            throw new RuntimeException(Messages
                    .getMessage("unknownsoapversion"));
        }
    }

    /**
     * Finalize method called by garbage collection. This is overridden to
     * support cleanup of any associated resources.
     * 
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
         super.finalize();
         cleanup();
    }

    /**
     * Cleanup associated resources. This removes the axis service from the
     * configuration.
     * 
     * @throws AxisFault
     */
    public void cleanup() throws AxisFault {
        _service.getAxisConfiguration().removeService(_service.getName());
    }
}
