package org.apache.axis2.engine;

import junit.framework.TestCase;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.ServiceDescription;
import org.apache.axis2.engine.util.InvokerThread;
import org.apache.axis2.integration.UtilServer;
import org.apache.axis2.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 *
 * @author : Eran Chinthaka (chinthaka@apache.org)
 */

public class ThreadingTest extends TestCase {

    protected EndpointReference targetEPR =
            new EndpointReference("http://127.0.0.1:"
                    + (UtilServer.TESTING_PORT)
                    + "/axis/services/EchoXMLService/echoOMElement");
    protected Log log = LogFactory.getLog(getClass());
    protected QName serviceName = new QName("EchoXMLService");
    protected QName operationName = new QName("echoOMElement");
    protected QName transportName = new QName("http://localhost/my",
            "NullTransport");

    protected AxisConfiguration engineRegistry;
    protected MessageContext mc;
    protected ServiceContext serviceContext;
    protected ServiceDescription service;

    protected boolean finish = false;

    protected void setUp() throws Exception {
        UtilServer.start();
        service =
                Utils.createSimpleService(serviceName,
                        Echo.class.getName(),
                        operationName);
        UtilServer.deployService(service);
    }

    protected void tearDown() throws Exception {
        UtilServer.unDeployService(serviceName);
        UtilServer.stop();
        UtilServer.unDeployClientService();
    }

    public void testEchoXMLSync() throws Exception {


        int numberOfThreads = 5;
        InvokerThread[] invokerThreads = new InvokerThread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            InvokerThread invokerThread = new InvokerThread(i + 1);
            invokerThreads[i] = invokerThread;
            invokerThread.start();
        }

        boolean threadsAreRunning;
        Calendar cal = new GregorianCalendar();
        int min = cal.get(Calendar.MINUTE);

        do {
            threadsAreRunning = false;
            for (int i = 0; i < numberOfThreads; i++) {
                if (invokerThreads[i].isAlive()) {
                    threadsAreRunning = true;
                    break;
                }
                if (invokerThreads[i].isExceptionThrown()) {
                    fail("Exception thrown in thread "+ i + " ....");
                }
            }

            // waiting 3 seconds, if not finish, time out.
            if (Math.abs(min - new GregorianCalendar().get(Calendar.MINUTE)) > 1) {
                log.info("I'm timing out. Can't wait more than this to finish.");
                fail("Timing out");
            }

        } while (threadsAreRunning);

        assertTrue(true);
    }
}
