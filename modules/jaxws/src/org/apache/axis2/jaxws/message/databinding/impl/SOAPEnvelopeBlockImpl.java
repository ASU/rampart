/**
 * 
 */
package org.apache.axis2.jaxws.message.databinding.impl;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.message.MessageException;
import org.apache.axis2.jaxws.message.databinding.SOAPEnvelopeBlock;
import org.apache.axis2.jaxws.message.factory.BlockFactory;
import org.apache.axis2.jaxws.message.factory.MessageFactory;
import org.apache.axis2.jaxws.message.impl.BlockImpl;
import org.apache.axis2.jaxws.message.util.SOAPElementReader;
import org.apache.axis2.jaxws.registry.FactoryRegistry;

/**
 * 
 *
 */
public class SOAPEnvelopeBlockImpl extends BlockImpl implements SOAPEnvelopeBlock {

	/**
	 * Called by SOAPEnvelopeBlockFactory
	 * @param busObject
	 * @param busContext
	 * @param qName
	 * @param factory
	 */
	public SOAPEnvelopeBlockImpl(Object busObject, Object busContext,
			QName qName, BlockFactory factory) {
		super(busObject, 
				busContext, 
				(qName==null) ? getQName((SOAPEnvelope)busObject): qName , 
				factory);
	}

	/**
	 * Called by SOAPEnvelopeBlockFactory
	 * @param omElement
	 * @param busContext
	 * @param qName
	 * @param factory
	 */
	public SOAPEnvelopeBlockImpl(OMElement omElement, Object busContext,
			QName qName, BlockFactory factory) {
		super(omElement, busContext, qName, factory);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.impl.BlockImpl#_getBOFromReader(javax.xml.stream.XMLStreamReader, java.lang.Object)
	 */
	@Override
	protected Object _getBOFromReader(XMLStreamReader reader, Object busContext)
			throws XMLStreamException, MessageException {
		
		// TODO Temporary solution.  The better way is to get an OM
		// and convert with the SAAJConverter
		MessageFactory mf = (MessageFactory) FactoryRegistry.getFactory(MessageFactory.class);
		Message message = mf.createFrom(reader);
		SOAPEnvelope env = message.getAsSOAPEnvelope();
		this.setQName(getQName(env));
		return env;
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.impl.BlockImpl#_getReaderFromBO(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected XMLStreamReader _getReaderFromBO(Object busObj, Object busContext)
			throws XMLStreamException, MessageException {
		return new SOAPElementReader((SOAPElement)busObj);
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.message.impl.BlockImpl#_outputFromBO(java.lang.Object, java.lang.Object, javax.xml.stream.XMLStreamWriter)
	 */
	@Override
	protected void _outputFromBO(Object busObject, Object busContext,
			XMLStreamWriter writer) throws XMLStreamException, MessageException {
		XMLStreamReader reader = _getReaderFromBO(busObject, busContext);
		_outputFromReader(reader, writer);	
	}

	/**
	 * Get the QName of the envelope
	 * @param env
	 * @return QName
	 */
	private static QName getQName(SOAPEnvelope env) {
		return new QName(env.getNamespaceURI(), env.getLocalName(),env.getPrefix());
	}
}
