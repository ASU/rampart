/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Copyright 1999-2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.axis2.transport.http.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.engine.DependencyManager;
import org.apache.axis2.util.UUIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionManager {

    private static final Log LOG = LogFactory.getLog(SessionManager.class);
    
    private final Map sessionmap;
    
    public SessionManager() {
        super();
        this.sessionmap = new HashMap();
    }
    
    public synchronized SessionContext getSessionContext(String sessionKey) {
        SessionContext sessionContext = null;
        if (sessionKey != null && sessionKey.length() != 0) {
            sessionContext = (SessionContext) this.sessionmap.get(sessionKey);
        }
        if (sessionContext == null) {
            sessionKey = UUIDGenerator.getUUID();
            sessionContext = new SessionContext(null);
            sessionContext.setCookieID(sessionKey);
            this.sessionmap.put(sessionKey, sessionContext);
        }
        sessionContext.touch();
        cleanupServiceGroupContexts();
        return sessionContext;
    }

    private void cleanupServiceGroupContexts() {
        long currentTime = System.currentTimeMillis();
        for (Iterator it = this.sessionmap.keySet().iterator(); it.hasNext(); ) {
            String cookieID = (String) it.next();
            SessionContext sessionContext = (SessionContext) this.sessionmap.get(cookieID);
            if ((currentTime - sessionContext.getLastTouchedTime()) >
                    sessionContext.sessionContextTimeoutInterval) {
                it.remove();
                Iterator serviceGroupContext = sessionContext.getServiceGroupContext();
                if (serviceGroupContext != null) {
                    while (serviceGroupContext.hasNext()) {
                        ServiceGroupContext groupContext = (ServiceGroupContext) serviceGroupContext.next();
                        cleanupServiceContexts(groupContext);
                    }
                }
            }
        }
    }

    private void cleanupServiceContexts(final ServiceGroupContext serviceGroupContext) {
        for (Iterator it = serviceGroupContext.getServiceContexts(); it.hasNext(); ) {
            ServiceContext serviceContext = (ServiceContext) it.next();
            try {
                DependencyManager.destroyServiceObject(serviceContext);
            } catch (AxisFault axisFault) {
                LOG.info(axisFault.getMessage());
            }
        }
    }
    
}
