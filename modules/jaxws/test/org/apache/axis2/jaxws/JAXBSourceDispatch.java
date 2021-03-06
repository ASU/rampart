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
package org.apache.axis2.jaxws;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.test.dispatch.jaxbsource.Invoke;
import org.test.dispatch.jaxbsource.ObjectFactory;

import junit.framework.TestCase;


/*
 * This is a test case for Invoking Dispatch with a JAXBSource.
 * test uses JAXB Objects from org.test.dispatch.jaxbsource package, create a request of JAXBSource type
 * and invokes the service endpoint and reads the response of type Source. Assert failure if response not received.
 */


public class JAXBSourceDispatch extends TestCase {
	/**
     * Invoke a sync Dispatch<JAXBSource> in PAYLOAD mode
     */
	
	private String url = "http://localhost:8080/axis2/services/SourceProviderService";
	private QName serviceName = new QName("http://ws.apache.org/axis2", "SourceProviderService");
	private QName portName =new QName("http://ws.apache.org/axis2", "SimpleProviderServiceSOAP11port0");
	
    public void testJAXBSourceSyncPayloadMode() throws Exception {
        System.out.println("---------------------------------------");
        System.out.println("test: " + getName());
        try{
	        // Initialize the JAX-WS client artifacts
	        Service svc = Service.create(serviceName);
	        svc.addPort(portName, null, url);
	        Dispatch<JAXBSource> dispatch = svc.createDispatch(portName, 
	                JAXBSource.class, Service.Mode.PAYLOAD);
	        
	        //Create JAXBContext and JAXBSource here.
	        ObjectFactory factory = new ObjectFactory();
	        Invoke invokeObj = factory.createInvoke();
	        invokeObj.setInvokeStr("Some Request");
	        JAXBContext ctx = JAXBContext.newInstance("org.test.dispatch.jaxbsource");
	       
	        JAXBSource jbSrc = new JAXBSource(ctx.createMarshaller(), invokeObj);
	        // Invoke the Dispatch
	        System.out.println(">> Invoking sync Dispatch");
	        //Invoke Server endpoint and read response
	        Source response = dispatch.invoke(jbSrc);
	       
	        assertNotNull("dispatch invoke returned null", response);
	        //Print the response as string.
	        StringWriter writer = new StringWriter();
	        Transformer t = TransformerFactory.newInstance().newTransformer();
	        Result result = new StreamResult(writer);
	        t.transform(response, result);
	
	        System.out.println("Response On Client: \n"+writer.getBuffer().toString());
	        System.out.println("---------------------------------------");
        }catch(Exception e){
        	e.printStackTrace();
        }
        
    }
    
}
