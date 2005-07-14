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
package org.apache.axis2.soap.impl.llom.builder;

import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMException;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.om.OMNode;
import org.apache.axis2.om.impl.llom.builder.StAXBuilder;
import org.apache.axis2.om.impl.llom.exception.OMBuilderException;
import org.apache.axis2.soap.SOAPBody;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.axis2.soap.SOAPHeader;
import org.apache.axis2.soap.impl.llom.SOAPConstants;
import org.apache.axis2.soap.impl.llom.SOAPEnvelopeImpl;
import org.apache.axis2.soap.impl.llom.SOAPProcessingException;
import org.apache.axis2.soap.impl.llom.soap11.SOAP11Constants;
import org.apache.axis2.soap.impl.llom.soap12.SOAP12Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Class StAXSOAPModelBuilder
 */
public class StAXSOAPModelBuilder extends StAXBuilder {
    /**
     * Field envelope
     */
    private SOAPEnvelopeImpl envelope;
    private OMNamespace envelopeNamespace;

    private SOAPFactory soapFactory;

    /**
     * Field headerPresent
     */
    private boolean headerPresent = false;

    /**
     * Field bodyPresent
     */
    private boolean bodyPresent = false;

    /**
     * Field log
     */
    private Log log = LogFactory.getLog(getClass());

    /**
     * element level 1 = envelope level element level 2 = Header or Body level
     * element level 3 = HeaderElement or BodyElement level
     */
    int elementLevel = 0;

    private boolean processingFault = false;

    //added
    /*This is used to notice whether all mandatory fault elements are present
    */
    private boolean processingMandatoryFaultElements = false;

    //added
    /* This is used to indicate whether detail element is processing in soap 1.2 builderhelper
    */
    private boolean processingDetailElements = false;

    private SOAPBuilderHelper builderHelper;

    /**
     * Constructor StAXSOAPModelBuilder
     *
     * @param parser
     */
    public StAXSOAPModelBuilder(XMLStreamReader parser) {
        super(parser);
        soapFactory = OMAbstractFactory.getDefaultSOAPFactory();
        identifySOAPVersion();
        parseHeaders();
    }

    public StAXSOAPModelBuilder(XMLStreamReader parser, SOAPFactory factory) {
        super(parser);
        soapFactory = factory;
        identifySOAPVersion();

        parseHeaders();
    }

    private void identifySOAPVersion() {
        SOAPEnvelope soapEnvelope = getSOAPEnvelope();
        if (soapEnvelope == null) {
            throw new OMException("No SOAPHeader present !!");
        }

        envelopeNamespace =
                soapEnvelope.findNamespace(
                        SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, "");
        if (envelopeNamespace == null) {
            envelopeNamespace =
                    getSOAPEnvelope().findNamespace(
                            SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI, "");
            if (envelopeNamespace == null) {
                throw new OMException(
                        "Invalid SOAP message. Doesn't have proper namespace declaration !!");
            } else {
                log.info("SOAP 1.1 message received ..");
                soapFactory = OMAbstractFactory.getSOAP11Factory();
            }
        } else {
            log.info("SOAP 1.2 message received ..");
            soapFactory = OMAbstractFactory.getSOAP12Factory();
        }

        omfactory = soapFactory;
    }

    private void parseHeaders() {
        // by the time execution comes here the nullity of SOAPEnvelope has been cheched in the
        // identifySOAPVersion() method. So not checking getSOAPEnvelope() == null here
        SOAPHeader soapHeader = getSOAPEnvelope().getHeader();

        if (soapHeader != null) {
            while (!soapHeader.isComplete()) {
                next();
            }
        } else {
            log.info("No SOAPHeaders present !!");
        }
    }


    /**
     * Method getSOAPEnvelope
     *
     * @return
     * @throws OMException
     */
    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        while ((envelope == null) && !done) {
            next();
        }
        return envelope;
    }

    /**
     * Method createOMElement
     *
     * @return
     * @throws OMException
     */
    protected OMNode createOMElement() throws OMException {
        OMElement node;
        String elementName = parser.getLocalName();
        if (lastNode == null) {
            node = constructNode(null, elementName, true);
        } else if (lastNode.isComplete()) {
            node =
                    constructNode((OMElement) lastNode.getParent(),
                            elementName,
                            false);
            lastNode.setNextSibling(node);
            node.setPreviousSibling(lastNode);
        } else {
            OMElement e = (OMElement) lastNode;
            node = constructNode((OMElement) lastNode, elementName, false);
            e.setFirstChild(node);
        }


        log.info(
                "Build the OMElelment " + node.getLocalName() +
                "By the StaxSOAPModelBuilder");
        return node;
    }

    /**
     * Method constructNode
     *
     * @param parent
     * @param elementName
     * @param isEnvelope
     * @return
     */
    protected OMElement constructNode(OMElement parent, String elementName,
                                      boolean isEnvelope) {
        OMElement element = null;
        if (parent == null) {
            if (!elementName.equalsIgnoreCase(
                    SOAPConstants.SOAPENVELOPE_LOCAL_NAME)) {
                throw new OMException("First Element must contain the local name, "
                        + SOAPConstants.SOAPENVELOPE_LOCAL_NAME);
            }
            envelope =
                    (SOAPEnvelopeImpl) soapFactory.createSOAPEnvelope(this);
            element = envelope;
            processNamespaceData(element, true);
            // fill in the attributes
            processAttributes(element);

        } else if (elementLevel == 2) {

            // this is either a header or a body
            if (elementName.equals(SOAPConstants.HEADER_LOCAL_NAME)) {
                if (headerPresent) {
                    throw new OMBuilderException(
                            "Multiple headers encountered!");
                }
                if (bodyPresent) {
                    throw new OMBuilderException("Header Body wrong order!");
                }
                headerPresent = true;
                element =
                        soapFactory.createSOAPHeader((SOAPEnvelope) parent,
                                this);

                // envelope.setHeader((SOAPHeader)element);
                processNamespaceData(element, true);
                processAttributes(element);

            } else if (elementName.equals(SOAPConstants.BODY_LOCAL_NAME)) {
                if (bodyPresent) {
                    throw new OMBuilderException(
                            "Multiple body elements encountered");
                }
                bodyPresent = true;
                element =
                        soapFactory.createSOAPBody((SOAPEnvelope) parent,
                                this);

                // envelope.setBody((SOAPBody)element);
                processNamespaceData(element, true);
                processAttributes(element);

            } else {
                throw new OMBuilderException(
                        elementName
                        +
                        " is not supported here. Envelope can not have elements other than Header and Body.");
            }
        } else if ((elementLevel == 3)
                &&
                parent.getLocalName().equalsIgnoreCase(
                        SOAPConstants.HEADER_LOCAL_NAME)) {

            // this is a headerblock
            try {
                element =
                        soapFactory.createSOAPHeaderBlock(elementName, null,
                                (SOAPHeader) parent, this);
            } catch (SOAPProcessingException e) {
                throw new OMBuilderException(e);
            }
            processNamespaceData(element, false);
            processAttributes(element);

        } else if ((elementLevel == 3) &&
                parent.getLocalName().equalsIgnoreCase(
                        SOAPConstants.BODY_LOCAL_NAME) &&
                elementName.equalsIgnoreCase(
                        SOAPConstants.BODY_FAULT_LOCAL_NAME)) {

            // this is a headerblock
            element = soapFactory.createSOAPFault((SOAPBody) parent, this);
            processNamespaceData(element, false);
            processAttributes(element);


            processingFault = true;

            //added
            processingMandatoryFaultElements = true;
            if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                    envelopeNamespace.getName())) {
                builderHelper = new SOAP12BuilderHelper(this);
            } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(
                    envelopeNamespace.getName())) {
                builderHelper = new SOAP11BuilderHelper(this);
            }

        } else if (elementLevel > 3 && processingFault) {
            element = builderHelper.handleEvent(parser, parent, elementLevel);
        } else {

            // this is neither of above. Just create an element
            element = soapFactory.createOMElement(elementName, null,
                    parent, this);
            processNamespaceData(element, false);
            processAttributes(element);

        }
        return element;
    }

    /**
     * Method next
     *
     * @return
     * @throws OMException
     */
    public int next() throws OMException {
        try {
            if (done) {
                throw new OMException();
            }
            int token = parser.next();
            if (!cache) {
                return token;
            }
            switch (token) {
                case XMLStreamConstants.START_ELEMENT:
                    elementLevel++;
                    lastNode = createOMElement();
                    break;
                case XMLStreamConstants.CHARACTERS:
                    lastNode = createOMText();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (lastNode.isComplete()) {
                        OMElement parent = (OMElement) lastNode.getParent();

//                        //added
//                        /*check whether all mandatory fault elements are present
//                        */
//                        if (parent.getLocalName().equals(SOAP12Constants.SOAPFAULT_LOCAL_NAME) && processingMandatoryFaultElements) {
//                            throw new OMBuilderException("Missing mandatory fault elements");
//                        }
//                        //added
//                        /*finish processing detail element in soap 1.2 builderhelper
//                        */
//                        if (parser.getLocalName().equals(SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME)) {
//                            this.setProcessingDetailElements(false);
//                        }

                        parent.setComplete(true);
                        lastNode = parent;
                    } else {
                        OMNode e = lastNode;
                        e.setComplete(true);
                    }
                    elementLevel--;
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    done = true;
                    break;
                case XMLStreamConstants.SPACE:
                    next();
                    break;
                default :
                    throw new OMException();
            }
            return token;
        } catch (OMException e) {
            throw e;
        } catch (Exception e) {
            throw new OMException(e);
        }
    }

    /**
     * Method getDocumentElement
     *
     * @return
     */
    public OMElement getDocumentElement() {
        return getSOAPEnvelope();
    }

    /**
     * Method processNamespaceData
     *
     * @param node
     * @param isSOAPElement
     */
    protected void processNamespaceData(OMElement node, boolean isSOAPElement) {
        int namespaceCount = parser.getNamespaceCount();
        for (int i = 0; i < namespaceCount; i++) {
            node.declareNamespace(parser.getNamespaceURI(i),
                    parser.getNamespacePrefix(i));
        }

        // set the own namespace
        String namespaceURI = parser.getNamespaceURI();
        String prefix = parser.getPrefix();
        OMNamespace namespace = null;
        if (!"".equals(namespaceURI)) {
            if (prefix == null) {
                // this means, this elements has a default namespace or it has inherited a default namespace from its parent
                namespace = node.findNamespace(namespaceURI, "");
                if (namespace == null) {
                    namespace = node.declareNamespace(namespaceURI, "");
                }
            } else {
                namespace = node.findNamespace(namespaceURI, prefix);
            }
            node.setNamespace(namespace);
        } else {

        }



        // TODO we got to have this to make sure OM reject mesagess that are not name space qualified
        // But got to comment this to interop with Axis.1.x
        // if (namespace == null) {
        // throw new OMException("All elements must be namespace qualified!");
        // }
        if (isSOAPElement) {
            if (node.getNamespace() != null &&
                    !node.getNamespace().getName().equals(
                            SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI) &&
                    !node.getNamespace().getName().equals(
                            SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
                throw new OMBuilderException("invalid SOAP namespace URI");
            }
        }

    }

    //added
    /*these three methods to set and check detail element processing or mandatory fault element are present
    */
    public OMNamespace getEnvelopeNamespace() {
        return envelopeNamespace;
    }

    public void setBooleanProcessingMandatoryFaultElements(boolean value) {
        this.processingMandatoryFaultElements = value;
    }

    public boolean isProcessingDetailElements() {
        return processingDetailElements;
    }

    public void setProcessingDetailElements(boolean value) {
        processingDetailElements = value;
    }

}
