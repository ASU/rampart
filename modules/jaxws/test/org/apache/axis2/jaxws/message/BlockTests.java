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
package org.apache.axis2.jaxws.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.jaxws.message.factory.BlockFactory;
import org.apache.axis2.jaxws.message.factory.JAXBBlockFactory;
import org.apache.axis2.jaxws.message.factory.MessageFactory;
import org.apache.axis2.jaxws.message.factory.OMBlockFactory;
import org.apache.axis2.jaxws.message.factory.SourceBlockFactory;
import org.apache.axis2.jaxws.message.factory.XMLStringBlockFactory;
import org.apache.axis2.jaxws.message.util.Reader2Writer;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import test.EchoString;
import test.ObjectFactory;

import junit.framework.TestCase;

/**
 * BlockTests
 * Tests to create and validate blocks.
 * These are not client/server tests.
 */
public class BlockTests extends TestCase {

	// String test variables
	private static final String sampleText =
		"<pre:a xmlns:pre=\"urn://sample\">" +
		"<b>Hello</b>" +
		"<c>World</c>" +
		"</pre:a>";
	private static final QName sampleQName = new QName("urn://sample", "a");
	
	
	private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private static XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	
	
	
	public BlockTests() {
		super();
	}

	public BlockTests(String arg0) {
		super(arg0);
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 * normal Dispatch<String> flow
	 * @throws Exception
	 */
	public void testStringOutflow() throws Exception {
		// Get the BlockFactory
		XMLStringBlockFactory f = (XMLStringBlockFactory)
			FactoryRegistry.getFactory(XMLStringBlockFactory.class);
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<String> client
		Block block = f.createFrom(sampleText, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}

	/**
	 * Create a Block representing an XMLString and
	 * simulate a different Dispatch<String> flow
	 * @throws Exception
	 */
	public void testStringOutflow2() throws Exception {
		// Get the BlockFactory
		XMLStringBlockFactory f = (XMLStringBlockFactory)
			FactoryRegistry.getFactory(XMLStringBlockFactory.class);
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<String> client
		Block block = f.createFrom(sampleText, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + sampleQName + " but found: " + qName, sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
	
	/**
	 * Create a Block representing an XMLString and
	 * simulate a different String parameter flow
	 * @throws Exception
	 */
	public void testStringOutflow3() throws Exception {
		// Get the BlockFactory
		XMLStringBlockFactory f = (XMLStringBlockFactory)
			FactoryRegistry.getFactory(XMLStringBlockFactory.class);
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS String parameter on the client.
		// In this case, we know the QName prior to creating the Block...so let's pass it in.
		Block block = f.createFrom(sampleText, null, sampleQName);
		
		// We passed in a qname, so it should be immediately available
		assertTrue(block.isQNameAvailable());
		
		// Make sure the QName is correct.
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 * normal Dispatch<String> input flow
	 * @throws Exception
	 */
	public void testStringInflow() throws Exception {
		// Get the BlockFactory
		XMLStringBlockFactory f = (XMLStringBlockFactory)
			FactoryRegistry.getFactory(XMLStringBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof String);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check the String for accuracy
		assertTrue(sampleText.equals(bo.toString()));
		
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 * slightly more complicated Dispatch<String> inflow
	 * @throws Exception
	 */
	public void testStringInflow2() throws Exception {
		// Get the BlockFactory
		XMLStringBlockFactory f = (XMLStringBlockFactory)
			FactoryRegistry.getFactory(XMLStringBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Let's assume we need to get the QName to find the operation name.
		// This will cause an underlying parse
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof String);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check the String for accuracy
		assertTrue(sampleText.equals(bo.toString()));
		
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 * slightly more complicated String  inflow
	 * @throws Exception
	 */
	public void testStringInflow3() throws Exception {
		// Get the BlockFactory
		XMLStringBlockFactory f = (XMLStringBlockFactory)
			FactoryRegistry.getFactory(XMLStringBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  Assume that we know the QName already
		Block block = f.createFrom(inflow, null, sampleQName);
		
		// We passed in a qname, so the following should return false
		assertTrue(block.isQNameAvailable());
		
		// Let's assume we need to get the QName to find the operation name.
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof String);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check the String for accuracy
		assertTrue(sampleText.equals(bo.toString()));
		
	}
	
	/**
	 * Create a Block representing an JAXB and simulate a 
	 * normal Dispatch<JAXB> flow
	 * @throws Exception
	 */
	public void testJAXBOutflow() throws Exception {
		// Get the BlockFactory
		JAXBBlockFactory f = (JAXBBlockFactory)
			FactoryRegistry.getFactory(JAXBBlockFactory.class);
		
        // Create a jaxb object
        ObjectFactory factory = new ObjectFactory();
        EchoString jaxb = factory.createEchoString(); 
        jaxb.setInput("Hello World");
        JAXBContext jbc = JAXBContext.newInstance("test");
        JAXBIntrospector jbi = jbc.createJAXBIntrospector();
        QName expectedQName = jbi.getElementName(jaxb);
        
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<JAXB> client
		Block block = f.createFrom(jaxb, jbc, null);
		
		// JAXB objects set the qname from their internal data
		assertTrue(block.isQNameAvailable());
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + expectedQName + " but found: " + qName, expectedQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(newText.contains("Hello World"));
		assertTrue(newText.contains("echoString"));
		
	}
	
	/**
	 * Create a Block representing an JAXB and simulate a 
	 * slightly more complicated Dispatch<JAXB> flow
	 * @throws Exception
	 */
	public void testJAXBOutflow2() throws Exception {
		// Get the BlockFactory
		JAXBBlockFactory f = (JAXBBlockFactory)
			FactoryRegistry.getFactory(JAXBBlockFactory.class);
		
        // Create a jaxb object
        ObjectFactory factory = new ObjectFactory();
        EchoString jaxb = factory.createEchoString(); 
        jaxb.setInput("Hello World");
        JAXBContext jbc = JAXBContext.newInstance("test");
        JAXBIntrospector jbi = jbc.createJAXBIntrospector();
        QName expectedQName = jbi.getElementName(jaxb);
        
		// Create a Block using the sample string as the content.  This simulates
		// what occurs with an outbound JAX-WS JAXB parameter
		Block block = f.createFrom(jaxb, jbc, expectedQName);
		
		// We did pass in a qname, so the following should return false
		assertTrue(block.isQNameAvailable());
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + expectedQName + " but found: " + qName, expectedQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(newText.contains("Hello World"));
		assertTrue(newText.contains("echoString"));
		
	}
	
	/**
	 * Create a Block representing an JAXB and simulate a 
	 * normal Dispatch<JAXB> input flow
	 * @throws Exception
	 */
	public void testJAXBInflow() throws Exception {
		// Get the BlockFactory
		JAXBBlockFactory f = (JAXBBlockFactory)
			FactoryRegistry.getFactory(JAXBBlockFactory.class);
		
        // Create a jaxb object
        ObjectFactory factory = new ObjectFactory();
        EchoString jaxb = factory.createEchoString(); 
        jaxb.setInput("Hello World");
        JAXBContext jbc = JAXBContext.newInstance("test");
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
        jbc.createMarshaller().marshal(jaxb, writer);
        writer.flush();
        sw.flush();
		StringReader sr = new StringReader(sw.toString());
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, jbc, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object.
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof EchoString);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check for accuracy
		assertTrue("Unexpected:" + ((EchoString)bo).getInput(), ((EchoString)bo).getInput().equals(jaxb.getInput()));
		
	}
	
	/**
	 * Create a Block representing an JAXB and simulate a 
	 * normal Dispatch<JAXB> input flow
	 * @throws Exception
	 */
	public void testJAXBInflow2() throws Exception {
		// Get the BlockFactory
		JAXBBlockFactory f = (JAXBBlockFactory)
			FactoryRegistry.getFactory(JAXBBlockFactory.class);
		
        // Create a jaxb object
        ObjectFactory factory = new ObjectFactory();
        EchoString jaxb = factory.createEchoString(); 
        jaxb.setInput("Hello World");
        JAXBContext jbc = JAXBContext.newInstance("test");
        JAXBIntrospector jbi = jbc.createJAXBIntrospector();
        QName expectedQName = jbi.getElementName(jaxb);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
        jbc.createMarshaller().marshal(jaxb, writer);
        writer.flush();
        sw.flush();
		StringReader sr = new StringReader(sw.toString());
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, jbc, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + expectedQName + " but found: " + qName, expectedQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object.
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof EchoString);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check for accuracy
		assertTrue("Unexpected:" + ((EchoString)bo).getInput(), ((EchoString)bo).getInput().equals(jaxb.getInput()));
		
	}
	
	/**
	 * Create a Block representing an JAXB and simulate a 
	 * normal Dispatch<JAXB> input flow
	 * @throws Exception
	 */
	public void testJAXBInflow3() throws Exception {
		// Get the BlockFactory
		JAXBBlockFactory f = (JAXBBlockFactory)
			FactoryRegistry.getFactory(JAXBBlockFactory.class);
		
        // Create a jaxb object
        ObjectFactory factory = new ObjectFactory();
        EchoString jaxb = factory.createEchoString(); 
        jaxb.setInput("Hello World");
        JAXBContext jbc = JAXBContext.newInstance("test");
        JAXBIntrospector jbi = jbc.createJAXBIntrospector();
        QName expectedQName = jbi.getElementName(jaxb);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(sw);
        jbc.createMarshaller().marshal(jaxb, writer);
        writer.flush();
        sw.flush();
		StringReader sr = new StringReader(sw.toString());
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, jbc, expectedQName);
		
		// We passed in a qname, so the following should return false
		assertTrue(block.isQNameAvailable());
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + expectedQName + " but found: " + qName, expectedQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object.
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof EchoString);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check for accuracy
		assertTrue("Unexpected:" + ((EchoString)bo).getInput(), ((EchoString)bo).getInput().equals(jaxb.getInput()));
		
	}
	
	/**
	 * Create a Block representing an OM and simulate a 
	 * normal Dispatch<OMElement> flow
	 * @throws Exception
	 */
	public void testOMOutflow() throws Exception {
		// Get the BlockFactory
		OMBlockFactory f = (OMBlockFactory)
			FactoryRegistry.getFactory(OMBlockFactory.class);
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<OMElement> client
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inputReader = inputFactory.createXMLStreamReader(sr);
		StAXOMBuilder builder = new StAXOMBuilder(inputReader);  
		OMElement om = builder.getDocumentElement();
		Block block = f.createFrom(om, null, null);
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
	

	/**
	 * Create a Block representing an OM and simulate a 
	 * different Dispatch<OMElement> flow
	 * @throws Exception
	 */
	public void testOMOutflow2() throws Exception {
		// Get the BlockFactory
		OMBlockFactory f = (OMBlockFactory)
			FactoryRegistry.getFactory(OMBlockFactory.class);
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<OMElement> client
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inputReader = inputFactory.createXMLStreamReader(sr);
		StAXOMBuilder builder = new StAXOMBuilder(inputReader);  
		OMElement om = builder.getDocumentElement();
		Block block = f.createFrom(om, null, null);
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + sampleQName + " but found: " + qName, sampleQName.equals(qName));
		
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 *  Dispatch<OMElement> inflow
	 * @throws Exception
	 */
	public void testOMInflow() throws Exception {
		// Get the BlockFactory
		OMBlockFactory f = (OMBlockFactory)
			FactoryRegistry.getFactory(OMBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, null, null);
		
		// Let's assume we need to get the QName to find the operation name.
		// This will cause an underlying parse
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof OMElement);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check the String for accuracy
		assertTrue(sampleText.equals(bo.toString()));
		
	}
	
	/**
	 * Create a Block representing a Source and simulate a 
	 * normal Dispatch<Source> flow
	 * @throws Exception
	 */
	public void testStreamSourceOutflow() throws Exception {
		// Get the BlockFactory
		SourceBlockFactory f = (SourceBlockFactory)
			FactoryRegistry.getFactory(SourceBlockFactory.class);
		
		StreamSource ss = new StreamSource(new StringReader(sampleText));
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<Source> client
		Block block = f.createFrom(ss, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}

	/**
	 * Create a Block representing a Source and
	 * simulate a different Dispatch<Source> flow
	 * @throws Exception
	 */
	public void testStreamSourceOutflow2() throws Exception {
		// Get the BlockFactory
		SourceBlockFactory f = (SourceBlockFactory)
			FactoryRegistry.getFactory(SourceBlockFactory.class);
		
		StreamSource ss = new StreamSource(new StringReader(sampleText));
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS dispatch<Source> client
		Block block = f.createFrom(ss, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assume that we need to find the QName (perhaps to identify the operation and 
		// determine if handlers are installed).   This is not very perfomant since 
		// it causes an underlying parse of the String...but we need to support this.
		QName qName = block.getQName();
		assertTrue("Expected: " + sampleQName + " but found: " + qName, sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
	
	/**
	 * Create a Block representing a Source and
	 * simulate a different Source parameter flow
	 * @throws Exception
	 */
	public void testStreamSourceOutflow3() throws Exception {
		// Get the BlockFactory
		SourceBlockFactory f = (SourceBlockFactory)
			FactoryRegistry.getFactory(SourceBlockFactory.class);
		
		StreamSource ss = new StreamSource(new StringReader(sampleText));
		
		// Create a Block using the sample string as the content.  This simulates
		// what occurs on the outbound JAX-WS String parameter on the client.
		// In this case, we know the QName prior to creating the Block...so let's pass it in.
		Block block = f.createFrom(ss, null, sampleQName);
		
		// We passed in a qname, so it should be immediately available
		assertTrue(block.isQNameAvailable());
		
		// Make sure the QName is correct.
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is a XMLStreamReader will be requested...to go to OM.   At this point the
		// block should be consumed.
		XMLStreamReader reader = block.getXMLStreamReader(true);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// To check that the output is correct, get the String contents of the 
		// reader
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 * normal Dispatch<Source> input flow
	 * @throws Exception
	 */
	public void testStreamSourceInflow() throws Exception {
		// Get the BlockFactory
		SourceBlockFactory f = (SourceBlockFactory)
			FactoryRegistry.getFactory(SourceBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof Source);
		
		// The block should be consumed
		assertTrue(block.isConsumed());
		
		// Check the String for accuracy
		XMLStreamReader reader = inputFactory.createXMLStreamReader((Source) bo);
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
	
	/**
	 * Create a Block representing an XMLString and simulate a 
	 * slightly more complicated Dispatch<Source> inflow
	 * @throws Exception
	 */
	public void testStreamSourceInflow2() throws Exception {

		// Get the BlockFactory
		SourceBlockFactory f = (SourceBlockFactory)
			FactoryRegistry.getFactory(SourceBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  
		Block block = f.createFrom(inflow, null, null);
		
		// We didn't pass in a qname, so the following should return false
		assertTrue(!block.isQNameAvailable());
		
		// Let's assume we need to get the QName to find the operation name.
		// This will cause an underlying parse
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof Source);
		
		// The block should be consumed
		assertTrue(block.isConsumed());

		// Check the String for accuracy
		XMLStreamReader reader = inputFactory.createXMLStreamReader((Source) bo);
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
	
	/**
	 * Create a Block representing an Source and simulate a 
	 * slightly more complicated Source inflow
	 * @throws Exception
	 */
	public void testStreamSourceInflow3() throws Exception {

		// Get the BlockFactory
		SourceBlockFactory f = (SourceBlockFactory)
			FactoryRegistry.getFactory(SourceBlockFactory.class);
		
		// On inbound, there will already be a XMLStreamReader (probably from OM)
		// which represents the message.  We will simulate this with inflow.
		StringReader sr = new StringReader(sampleText);
		XMLStreamReader inflow = inputFactory.createXMLStreamReader(sr);
		
		// Create a Block from the inflow.  Assume that we know the QName already
		Block block = f.createFrom(inflow, null, sampleQName);
		
		// We passed in a qname, so the following should return false
		assertTrue(block.isQNameAvailable());
		
		// Let's assume we need to get the QName to find the operation name.
		QName qName = block.getQName();
		assertTrue(sampleQName.equals(qName));
		
		// Assuming no handlers are installed, the next thing that will happen
		// is the proxy code will ask for the business object (String).
		Object bo = block.getBusinessObject(true);
		assertTrue(bo instanceof Source);
		
		// The block should be consumed
		assertTrue(block.isConsumed());

		// Check the String for accuracy
		XMLStreamReader reader = inputFactory.createXMLStreamReader((Source) bo);
		Reader2Writer r2w = new Reader2Writer(reader);
		String newText = r2w.getAsString();
		assertTrue(sampleText.equals(newText));
		
	}
    /*
     * Testing JAXBSource, Creating Source Block using JAXBSource and then
     * Serializing it.
     */
	public void testJAXBSourceInFlow1()throws Exception{
		//  Create a jaxb object
		try{
	        ObjectFactory factory = new ObjectFactory();
	        EchoString jaxb = factory.createEchoString(); 
	        jaxb.setInput("Hello World");
	        JAXBContext jbc = JAXBContext.newInstance("test");
	        JAXBSource src = new JAXBSource(jbc.createMarshaller(), jaxb);
	        BlockFactory f = (SourceBlockFactory)
				FactoryRegistry.getFactory(SourceBlockFactory.class);
	        
	        Block block =f.createFrom(src, null, null);
	        
	        MessageFactory mf = (MessageFactory) FactoryRegistry.getFactory(MessageFactory.class);
	        Message msg = mf.create(Protocol.soap11);
	        msg.setBodyBlock(0,block);
	        org.apache.axiom.soap.SOAPEnvelope env = (org.apache.axiom.soap.SOAPEnvelope)msg.getAsOMElement();
	        // Serialize the Envelope using the same mechanism as the 
	        // HTTP client.
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        env.serializeAndConsume(baos, new OMOutputFormat());
	        
	        // To check that the output is correct, get the String contents of the 
	        // reader
	        String newText = baos.toString();
	        System.out.println(newText);
	        assertTrue(block.isConsumed());
		}catch(Exception e){
			e.printStackTrace();
		}     
	}
	
	public void testJAXBSourceOutflow() throws Exception {
		
		//Sample text for JAXBSource
		String echoSample = "<echoString xmlns=\"http://test\"><input>Hello World</input></echoString>";
		
        // Get the BlockFactory
        SourceBlockFactory f = (SourceBlockFactory)
            FactoryRegistry.getFactory(SourceBlockFactory.class);
        //Create a JAXBSource
        
        JAXBContext jbc = JAXBContext.newInstance("test");
        Unmarshaller u = jbc.createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(echoSample.getBytes());
        EchoString jaxb = (EchoString)u.unmarshal(inputStream);
        JAXBSource src = new JAXBSource(jbc.createMarshaller(), jaxb);
        
        // Create a Block using the sample string as the content.  This simulates
        // what occurs on the outbound JAX-WS dispatch<Source> client
        Block block = f.createFrom(src, null, null);
        
        // We didn't pass in a qname, so the following should return false
        assertTrue(!block.isQNameAvailable());
        
        // Assuming no handlers are installed, the next thing that will happen
        // is a XMLStreamReader will be requested...to go to OM.   At this point the
        // block should be consumed.
        XMLStreamReader reader = block.getXMLStreamReader(true);
        
        // The block should be consumed
        assertTrue(block.isConsumed());
        
        // To check that the output is correct, get the String contents of the 
        // reader
        Reader2Writer r2w = new Reader2Writer(reader);
        String newText = r2w.getAsString();
        assertTrue(echoSample.equals(newText));
    }
    /**
     * Create a Block representing a DOMSource instance and simulate an 
     * outbound flow
     * @throws Exception
     */
    public void testDOMSourceOutflow() throws Exception {
        // Get the BlockFactory
        SourceBlockFactory f = (SourceBlockFactory)
            FactoryRegistry.getFactory(SourceBlockFactory.class);
        
        // Turn the content into a stream
        ByteArrayInputStream bais = new ByteArrayInputStream(sampleText.getBytes());
        
        // Create a DOM tree from the sample text
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
        Document domTree = domBuilder.parse(bais);
        Node node = domTree.getDocumentElement();
        System.out.println(node.toString());
        
        // Create a DOMSource object from the DOM tree
        DOMSource ds = new DOMSource(node);
        node = ds.getNode();
        
        // Create a Block using the sample string as the content.  This simulates
        // what occurs on the outbound JAX-WS dispatch<Source> client
        Block block = f.createFrom(ds, null, null);
        
        // We didn't pass in a qname, so the following should return false
        assertTrue(!block.isQNameAvailable());
        
        // Assuming no handlers are installed, the next thing that will happen
        // is a XMLStreamReader will be requested...to go to OM.   At this point the
        // block should be consumed.
        XMLStreamReader reader = block.getXMLStreamReader(true);
        
        // The block should be consumed
        assertTrue(block.isConsumed());
        
        // To check that the output is correct, get the String contents of the 
        // reader
        Reader2Writer r2w = new Reader2Writer(reader);
        String newText = r2w.getAsString();
        assertTrue(sampleText.equals(newText));
    }
    
    /**
     * Create a Block representing a SAXSource instance and simulate an 
     * outbound flow
     * @throws Exception
     */
    public void testSAXSourceOutflow() throws Exception {
        // Get the BlockFactory
        SourceBlockFactory f = (SourceBlockFactory)
            FactoryRegistry.getFactory(SourceBlockFactory.class);
        
        // Create a SAXSource from the sample text
        byte[] bytes = sampleText.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        InputSource input = new InputSource(stream);
        SAXSource ss = new SAXSource(input);
        
        // Create a Block using the sample string as the content.  This simulates
        // what occurs on the outbound JAX-WS dispatch<Source> client
        Block block = f.createFrom(ss, null, null);
        
        // We didn't pass in a qname, so the following should return false
        assertTrue(!block.isQNameAvailable());
        
        // Assuming no handlers are installed, the next thing that will happen
        // is a XMLStreamReader will be requested...to go to OM.   At this point the
        // block should be consumed.
        XMLStreamReader reader = block.getXMLStreamReader(true);
        
        // The block should be consumed
        assertTrue(block.isConsumed());
        
        // To check that the output is correct, get the String contents of the 
        // reader
        Reader2Writer r2w = new Reader2Writer(reader);
        String newText = r2w.getAsString();
        assertTrue(sampleText.equals(newText));
    }

}
