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

package org.apache.ws.secpolicy.model;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.PolicyComponent;
import org.apache.ws.secpolicy.Constants;

public class Layout extends AbstractSecurityAssertion {

    private String value = Constants.LAYOUT_LAX;

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        if (Constants.LAYOUT_LAX.equals(value)
                || Constants.LAYOUT_STRICT.equals(value)
                || Constants.LAYOUT_LAX_TIMESTAMP_FIRST.equals(value)
                || Constants.LAYOUT_LAX_TIMESTAMP_LAST.equals(value)) {
            this.value = value;
        } else {
            // throw new WSSPolicyException("Incorrect layout value : " +
            // value);
        }
    }

    public QName getName() {
        return Constants.LAYOUT;
    }

    public PolicyComponent normalize() {
        throw new UnsupportedOperationException();
    }

    public void serialize(XMLStreamWriter writer) throws XMLStreamException {

        String localName = Constants.LAYOUT.getLocalPart();
        String namespaceURI = Constants.LAYOUT.getNamespaceURI();

        String prefix = writer.getPrefix(namespaceURI);

        if (prefix == null) {
            prefix = Constants.LAYOUT.getPrefix();
            writer.setPrefix(prefix, namespaceURI);
        }

        // <sp:Layout>
        writer.writeStartElement(prefix, localName, namespaceURI);

        // <wsp:Policy>
        writer.writeStartElement(Constants.POLICY.getPrefix(), Constants.POLICY
                .getLocalPart(), Constants.POLICY.getNamespaceURI());

        // .. <sp:Strict /> | <sp:Lax /> | <sp:LaxTsFirst /> | <sp:LaxTsLast /> ..
        if (Constants.LAYOUT_STRICT.equals(value)) {
            writer.writeStartElement(prefix, Constants.STRICT.getLocalPart(), namespaceURI);
            
        } else if (Constants.LAYOUT_LAX.equals(value)) {
            writer.writeStartElement(prefix, Constants.LAX.getLocalPart(), namespaceURI);
            
        } else if (Constants.LAYOUT_LAX_TIMESTAMP_FIRST.equals(value)) {
            writer.writeStartElement(prefix, Constants.LAXTSFIRST.getLocalPart(), namespaceURI);
            
        } else if (Constants.LAYOUT_LAX_TIMESTAMP_LAST.equals(value)) {
            writer.writeStartElement(prefix, Constants.LAXTSLAST.getLocalPart(), namespaceURI);
        }
        
        writer.writeEndElement();
        
        // </wsp:Policy>
        writer.writeEndElement();
        
        // </sp:Layout>
        writer.writeEndElement();
    }
}
