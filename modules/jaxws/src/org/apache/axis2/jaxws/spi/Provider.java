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
package org.apache.axis2.jaxws.spi;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.spi.ServiceDelegate;


public class Provider extends javax.xml.ws.spi.Provider {

    @Override
    public Endpoint createAndPublishEndpoint(String s, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Endpoint createEndpoint(String s, Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceDelegate createServiceDelegate(URL url, QName qname, Class clazz){
        return new org.apache.axis2.jaxws.spi.ServiceDelegate(url, qname, clazz);
    }
}
