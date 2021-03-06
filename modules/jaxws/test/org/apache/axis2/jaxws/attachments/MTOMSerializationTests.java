package org.apache.axis2.jaxws.attachments;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axis2.jaxws.message.Block;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.message.Protocol;
import org.apache.axis2.jaxws.message.factory.BlockFactory;
import org.apache.axis2.jaxws.message.factory.JAXBBlockFactory;
import org.apache.axis2.jaxws.message.factory.MessageFactory;
import org.apache.axis2.jaxws.provider.DataSourceImpl;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.test.mtom.ImageDepot;
import org.test.mtom.ObjectFactory;
import org.test.mtom.SendImage;

public class MTOMSerializationTests extends TestCase {

    private DataSource imageDS;
    
    public void setUp() throws Exception {
        String imageResourceDir = "test-resources"+File.separator+"image";
        
        //Create a DataSource from an image 
        File file = new File(imageResourceDir+File.separator+"test.jpg");
        ImageInputStream fiis = new FileImageInputStream(file);
        Image image = ImageIO.read(fiis);
        imageDS = new DataSourceImpl("image/jpeg","test.jpg",image);
    }
    
    public MTOMSerializationTests(String name) {
        super(name);
    }
    
    /*
     * Simulate building up an OM that is sourced from JAXB and contains
     * binary data that should be optimized when serialized.  
     */
    public void testPlainOMSerialization() throws Exception {
        System.out.println("---------------------------------------");
        System.out.println("test: " + getName());
        
        OMElement payload = createPayload();
        
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setSOAP11(true);
               
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        payload.serializeAndConsume(baos, format);
        
        System.out.println("==================================");
        System.out.println(baos.toString());
        System.out.println("==================================");
    }
    
    /*
     * Simulate building up an OM SOAPEnvelope that has the contents of
     * the body sourced from JAXB and contains binary data that should be 
     * optimized when serialized.  
     */
    public void testSoapOMSerialization() throws Exception {
        System.out.println("---------------------------------------");
        System.out.println("test: " + getName());
        
        OMElement payload = createPayload();
        
        SOAPFactory factory = new SOAP11Factory();
        SOAPEnvelope env = factory.createSOAPEnvelope();
        SOAPBody body = factory.createSOAPBody(env);
        
        body.addChild(payload);
        
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setSOAP11(true);
               
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        env.serializeAndConsume(baos, format);
        
        System.out.println("==================================");
        System.out.println(baos.toString());
        System.out.println("==================================");
    }
    
    public void testMTOMAttachmentWriter() throws Exception {
        System.out.println("---------------------------------------");
        System.out.println("test: " + getName());
        
        //Create a DataHandler with the String DataSource object
        DataHandler dataHandler = new DataHandler(imageDS);
                        
        //Store the data handler in ImageDepot bean
        ImageDepot imageDepot = new ObjectFactory().createImageDepot();
        imageDepot.setImageData(dataHandler);
        
        JAXBContext jbc = JAXBContext.newInstance("org.test.mtom");
        
        //Create a request bean with imagedepot bean as value
        ObjectFactory factory = new ObjectFactory();
        SendImage request = factory.createSendImage();
        request.setInput(imageDepot);
        
        BlockFactory blkFactory = (JAXBBlockFactory) FactoryRegistry.getFactory(JAXBBlockFactory.class);
        Block block = blkFactory.createFrom(request, jbc, null);
        
        MessageFactory msgFactory = (MessageFactory) FactoryRegistry.getFactory(MessageFactory.class);
        Message msg = msgFactory.create(Protocol.soap11);
        
        msg.setBodyBlock(0, block);
        
        msg.setMTOMEnabled(true);
        
        SOAPEnvelope soapOM = (SOAPEnvelope) msg.getAsOMElement();
        
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setSOAP11(true);
               
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapOM.serializeAndConsume(baos, format);
        
        System.out.println("==================================");
        System.out.println(baos.toString());
        System.out.println("==================================");
    }
    
    private OMElement createPayload() {
        //Create a DataHandler with the String DataSource object
        DataHandler dataHandler = new DataHandler(imageDS);
                        
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("urn://mtom.test.org", "mtom");
        
        OMElement sendImage = fac.createOMElement("sendImage", omNs);
        
        OMElement input = fac.createOMElement("input", omNs);
        sendImage.addChild(input);
        
        OMElement imageData = fac.createOMElement("imageData", omNs);
        input.addChild(imageData);
        
        OMText binaryData = fac.createOMText(dataHandler, true);
        imageData.addChild(binaryData);
        
        return sendImage;
    }
}
