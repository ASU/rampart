/*
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.axis2.jaxws.core;

import java.util.List;
import java.util.concurrent.Executor;

import javax.xml.ws.handler.Handler;

import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.jaxws.impl.AsyncListener;

/**
 * The <code>InvocationContext</code> encapsulates all of the information 
 * relevant to a particular invocation.  This ties the context of the 
 * request back to the context of the response message (if applicable)
 * through the use of the MessageContext API.  There is a separate 
 * MessageContext for both the request and the response. *
 */
public interface InvocationContext {

    public List<Handler> getHandlers();
    
    public MessageContext getRequestMessageContext();
    
    public void setRequestMessageContext(MessageContext ctx);
    
    public MessageContext getResponseMessageContext();
    
    public void setResponseMessageContext(MessageContext ctx);
    
    public Executor getExecutor();
    
    public void setExecutor(Executor e);
    
    public AsyncListener getAsyncListener();
    
    public void setAsyncListener(AsyncListener al);
    
    //FIXME: This is temporary.
    public void setServiceClient(ServiceClient client);
    
    //FIXME: This is temporary.
    public ServiceClient getServiceClient();
}
