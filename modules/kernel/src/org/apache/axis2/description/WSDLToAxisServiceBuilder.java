package org.apache.axis2.description;

import org.apache.axis2.namespace.Constants;
import org.apache.axis2.AxisFault;
import org.apache.neethi.PolicyRegistry;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

public abstract class WSDLToAxisServiceBuilder {

    protected static final String XMLSCHEMA_NAMESPACE_URI = Constants.URI_2001_SCHEMA_XSD;

    protected static final String XMLSCHEMA_NAMESPACE_PREFIX = "xs";

    protected static final String XML_SCHEMA_LOCAL_NAME = "schema";

    protected static final String XML_SCHEMA_SEQUENCE_LOCAL_NAME = "sequence";

    protected static final String XML_SCHEMA_COMPLEX_TYPE_LOCAL_NAME = "complexType";

    protected static final String XML_SCHEMA_ELEMENT_LOCAL_NAME = "element";

    protected static final String XML_SCHEMA_IMPORT_LOCAL_NAME = "import";

    protected static final String XSD_NAME = "name";

    protected static final String XSD_TARGETNAMESPACE = "targetNamespace";

    protected static final String XMLNS_AXIS2WRAPPED = "xmlns:axis2wrapped";

    protected static final String AXIS2WRAPPED = "axis2wrapped";

    protected static final String XSD_TYPE = "type";

    protected static final String XSD_REF = "ref";

    protected static int nsCount = 0;

    protected Map resolvedRpcWrappedElementMap = new HashMap();

    protected static final String XSD_ELEMENT_FORM_DEFAULT = "elementFormDefault";

    protected static final String XSD_UNQUALIFIED = "unqualified";

    protected InputStream in;

    protected AxisService axisService;

    protected PolicyRegistry registry;

    protected QName serviceName;
    protected boolean isServerSide = true;
    protected String style = null;
    private URIResolver customResolver;
    private String baseUri = null;
    protected static final String TYPES = "Types";
    
    protected WSDLToAxisServiceBuilder(){
        
    }

    public WSDLToAxisServiceBuilder(InputStream in, QName serviceName) {
        this.in = in;
        this.serviceName = serviceName;
        this.axisService = new AxisService();
        setPolicyRegistryFromService(axisService);
    }

    public WSDLToAxisServiceBuilder(InputStream in, AxisService axisService) {
        this.in = in;
        this.axisService = axisService;
        setPolicyRegistryFromService(axisService);
    }

    /**
     * Sets a custom xmlschema resolver
     *
     * @param customResolver
     */
    public void setCustomResolver(URIResolver customResolver) {
        this.customResolver = customResolver;
    }

    public boolean isServerSide() {
        return isServerSide;
    }

    public void setServerSide(boolean serverSide) {
        isServerSide = serverSide;
    }

    protected void setPolicyRegistryFromService(AxisService axisService) {
        PolicyInclude policyInclude = axisService.getPolicyInclude();
        this.registry = policyInclude.getPolicyRegistry();
    }

    protected XmlSchema getXMLSchema(Element element, String baseUri) {
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection();

        if (baseUri != null) schemaCollection.setBaseUri(baseUri);

        if (customResolver != null) {
            schemaCollection.setSchemaResolver(customResolver);
        }

        return schemaCollection.read(element);
    }

    /**
     * Find the XML schema prefix
     */
    protected String findSchemaPrefix() {
        String xsdPrefix = null;
        Map declaredNameSpaces = axisService.getNameSpacesMap();
        if (declaredNameSpaces.containsValue(XMLSCHEMA_NAMESPACE_URI)) {
            //loop and find the prefix
            Iterator it = declaredNameSpaces.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = (String) it.next();
                if (XMLSCHEMA_NAMESPACE_URI.equals(declaredNameSpaces.get(key))) {
                    xsdPrefix = key;
                    break;
                }
            }
        } else {
            xsdPrefix = XMLSCHEMA_NAMESPACE_PREFIX; //default prefix
        }
        return xsdPrefix;
    }

    public abstract AxisService populateService() throws AxisFault;

    /**
     * Utility method that returns a DOM Builder
     */
    protected DocumentBuilder getDOMDocumentBuilder() {
        DocumentBuilder documentBuilder;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return documentBuilder;
    }

    /**
     */
    protected String getTemporaryNamespacePrefix() {
        return "ns" + nsCount++;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
