/*
 * Copyright 2004,2005 The Apache Software Foundation.
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
package org.apache.axis2.jaxws.message.util;

import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

/**
 * Reader2Writer
 * This is a simple converter that is constructed with an XMLStreamReader
 * and allows you to write the contents to an XMLStreamWriter.
 */
public class Reader2Writer {

	private XMLStreamReader reader;
	private static XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	
	/**
	 * Construct from a Reader
	 * @param reader -- the input to the converter
	 */
	public Reader2Writer(XMLStreamReader reader) {
		this.reader = reader;
	}

	/**
	 * outputTo the writer.
	 * @param writer -- the output of the converter
	 */
	public void outputTo(XMLStreamWriter writer) throws XMLStreamException {
		// Using OM to convert the reader to a writer.  This seems to be
		// the safest way to make the conversion, and it promotes code re-use.
		StAXOMBuilder builder = new StAXOMBuilder(reader);  
		OMDocument omDocument = builder.getDocument();
		Iterator it = omDocument.getChildren();
		while(it.hasNext()) {
			OMNode omNode = (OMNode) it.next();
			omNode.serializeAndConsume(writer);
		}
	}
	
	/**
	 * Utility method to write the reader contents to a String
	 * @return String
	 */
	public String getAsString() throws XMLStreamException {
		StringWriter sw = new StringWriter();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
		
		// Write the reader to the writer
		outputTo(writer);
		
		// Flush the writer and get the String
		writer.flush();
		sw.flush();
		String str = sw.toString();
		return str;
	}
}
