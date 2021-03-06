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
package org.apache.axis2.jaxws.message.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.i18n.Messages;
import org.apache.axis2.jaxws.message.Block;
import org.apache.axis2.jaxws.message.MessageException;
import org.apache.axis2.jaxws.message.XMLPart;
import org.apache.axis2.jaxws.message.factory.BlockFactory;
import org.apache.axis2.jaxws.message.util.Reader2Writer;

/**
 * BlockImpl
 * Abstract Base class for various Block Implementations.
 * 
 * The base class takes care of controlling the transformations between BusinessObject, 
 * XMLStreamReader and SOAPElement
 * A derived class must minimally define the following:
 *   _getBOFromReader
 *   _getReaderFromBO
 *   _outputFromBO
 * 
 * In addtion, the derived class may want to override the following:
 *   _getBOFromBO ...if the BusinessObject is consumed when read (i.e. it is an InputSource)
 *   
 * The derived classes don't have direct access to the instance data.
 * This ensures that BlockImpl controls the transformations.
 */
public abstract class BlockImpl implements Block {

	private Object busObject;
	private Object busContext;
	
	private OMElement omElement = null;
	
	private QName qName;
	private BlockFactory factory;
	private boolean consumed = false;
    private XMLPart parent;
	
	/**
	 * A Block has the following components
	 * @param busObject
	 * @param busContext or null
	 * @param qName or null if unknown
	 * @param factory that creates the Block
	 */
	protected BlockImpl(Object busObject, Object busContext, QName qName, BlockFactory factory) {
		this.busObject = busObject;
		this.busContext = busContext;
		this.qName = qName;
		this.factory = factory;
	}
	
	/**
	 * A Block has the following components
	 * @param reader
	 * @param busContext or null
	 * @param qName or null if unknown
	 * @param factory that creates the Block
	 */
	protected BlockImpl(OMElement omElement, Object busContext, QName qName, BlockFactory factory) {
		this.omElement = omElement;
		this.busContext = busContext;
		this.qName = qName;
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.Block#getBlockFactory()
	 */
	public BlockFactory getBlockFactory() {
		return factory;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.Block#getBusinessContext()
	 */
	public Object getBusinessContext() {
		return busContext;
	}
    
    public XMLPart getParent() {
        return parent;
    }
    
    public void setParent(XMLPart p) {
        parent = p;
    }
     
	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.Block#getBusinessObject(boolean)
	 */
	public Object getBusinessObject(boolean consume) throws XMLStreamException, MessageException {
		if (consumed) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("BlockImplErr1", this.getClass().getName()));
		}
		if (busObject != null) {
			busObject =  _getBOFromBO(busObject, busContext, consume);
		} else {
			// Transform reader into business object
			XMLStreamReader reader;
			if (consume) {
				reader = omElement.getXMLStreamReaderWithoutCaching();
			} else {
				reader = omElement.getXMLStreamReader();
			}
			busObject = _getBOFromReader(reader, busContext);
			omElement = null;
		}
		
		// Save the businessObject in a local variable
		// so that we can reset the Block if consume was indicated
		Object newBusObject = busObject;
		setConsumed(consume);
		return newBusObject;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.Block#getQName()
	 */
	public QName getQName() throws MessageException {
		// If the QName is not known, find it
		try {
			if (qName == null) {
				if (omElement == null) {
					XMLStreamReader newReader = _getReaderFromBO(busObject, busContext);
					busObject = null;
					StAXOMBuilder builder = new StAXOMBuilder(newReader);  
					omElement = builder.getDocumentElement();
				}
				qName = omElement.getQName();
			}
			return qName;
		} catch (XMLStreamException xse) {
			throw ExceptionFactory.makeMessageException(xse);
		}
	}
	
	/**
	 * This method is intended for derived objects to set the qName
	 * @param qName
	 */
	protected void setQName(QName qName) {
		this.qName = qName;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.Block#getXMLStreamReader(boolean)
	 */
	public XMLStreamReader getXMLStreamReader(boolean consume) throws XMLStreamException, MessageException {
		XMLStreamReader newReader = null;
		if (consumed) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("BlockImplErr1", this.getClass().getName()));
		}
		if (omElement != null) {
			if (consume) {
				newReader = omElement.getXMLStreamReaderWithoutCaching();
				omElement = null;
			} else {
				newReader = omElement.getXMLStreamReader();
			}
		} else if (busObject !=null) {
			// Getting the reader does not destroy the BusinessObject
			busObject = _getBOFromBO(busObject, busContext, consume);
			newReader = _getReaderFromBO(busObject, busContext);
		}
		setConsumed(consume);
		return newReader;
	}
	
	public OMElement getOMElement() throws XMLStreamException, MessageException {
		OMElement newOMElement = null;
		boolean consume =true;  // get the OM consumes the message
		if (consumed) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("BlockImplErr1", this.getClass().getName()));
		}
		if (omElement != null) {
			newOMElement = omElement;
		} else if (busObject !=null) {
			// Getting the reader does not destroy the BusinessObject
			busObject = _getBOFromBO(busObject, busContext, consume);
			XMLStreamReader newReader = _getReaderFromBO(busObject, busContext);
			StAXOMBuilder builder = new StAXOMBuilder(newReader);  
			newOMElement = builder.getDocumentElement();
		}
		setConsumed(consume);
		return newOMElement;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.Block#isConsumed()
	 */
	public boolean isConsumed() {
		return consumed;
	}
	
	/**
	 * Once consumed, all instance data objects are nullified to prevent subsequent access
	 * @param consume
	 * @return
	 */
	private void setConsumed(boolean consume) { 
		if (consume) {
			this.consumed = true;
			busObject = null;
			busContext = null;
			omElement = null;
		} else {
			consumed = false;
		}
	}

	public boolean isQNameAvailable() {
		return (qName != null);
	}

	public void outputTo(XMLStreamWriter writer, boolean consume) throws XMLStreamException, MessageException {
		if (consumed) {
			throw ExceptionFactory.makeMessageException(Messages.getMessage("BlockImplErr1", this.getClass().getName()));
		}
		if (omElement != null) {
			if (consume) {
				omElement.serializeAndConsume(writer);
			} else {
				omElement.serialize(writer);
			}
		} else if (busObject !=null) {
			busObject = _getBOFromBO(busObject, busContext, consume);
			_outputFromBO(busObject, busContext, writer);
		}
		setConsumed(consume);
		return;
	}

	/**
	 * @return true if the representation of the block is currently a business object.
	 * Derived classes may use this information to get information in a performant way.
	 */
	protected boolean isBusinessObject() {
		return busObject != null;
	}
	
	public String traceString(String indent) {
		// TODO add trace string
		return null;
	}
	
	/**
	 * The default implementation is to return the business object.
	 * A derived block may want to override this class if the business object
	 * is consumed when read (thus the dervived block may want to make a buffered copy)
	 * (An example use case for overriding this method is the businessObject is an InputSource)
	 * @param busObject
	 * @param busContext
	 * @param consume
	 * @return
	 */
	protected Object _getBOFromBO(Object busObject, Object busContext, boolean consume) {
		return busObject;
	}
	
	
	/**
	 * The derived class must provide an implementation that builds the business object from the reader
	 * @param reader XMLStreamReader, which is consumed
	 * @param busContext
	 * @return
	 */
	protected abstract Object _getBOFromReader(XMLStreamReader reader, Object busContext) throws XMLStreamException, MessageException;
	
	/**
	 * Get an XMLStreamReader for the BusinessObject
	 * The derived Block must implement this method
	 * @param busObj
	 * @param busContext
	 * @return
	 */
	protected abstract XMLStreamReader _getReaderFromBO(Object busObj, Object busContext) throws XMLStreamException, MessageException;
	
	/**
	 * Output Reader contents to a Writer.
	 * The default implementation is probably sufficient for most derived classes.
	 * @param reader
	 * @param writer
	 * @throws XMLStreamException
	 */
	protected void _outputFromReader(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
		Reader2Writer r2w = new Reader2Writer(reader);
		r2w.outputTo(writer);
	}
	
	/**
	 * Output BusinessObject contents to a Writer.
	 * Derived classes must provide this implementation
	 * @param busObject
	 * @param busContext
	 * @param writer
	 * @throws XMLStreamException
	 * @throws MessageException
	 */
	protected abstract void _outputFromBO(Object busObject, Object busContext, XMLStreamWriter writer) throws XMLStreamException, MessageException;
	
}
