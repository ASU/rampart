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

import java.io.IOException;

import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.io.CharArrayBuffer;
import org.apache.http.protocol.HttpContext;

public class ResponseSessionCookie implements HttpResponseInterceptor {

    public void process(final HttpResponse response, final HttpContext context) 
            throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        
        String sessionCookie = null;
        MessageContext msgctx = (MessageContext) context.getAttribute(AxisParams.MESSAGE_CONTEXT);
        if (msgctx != null) {
            sessionCookie = (String) msgctx.getProperty(HTTPConstants.COOKIE_STRING);
        }
        if (sessionCookie == null) {
          sessionCookie = (String) context.getAttribute(HTTPConstants.COOKIE_STRING);
        }
        if (sessionCookie != null) {
            CharArrayBuffer buffer = new CharArrayBuffer(sessionCookie.length() + 40);
            buffer.append(Constants.SESSION_COOKIE);
            buffer.append("=");
            buffer.append(sessionCookie);
            response.addHeader(new Header(HTTPConstants.HEADER_SET_COOKIE, buffer.toString()));
            buffer.append("; ");
            int port = response.getParams().getIntParameter(AxisParams.LISTENER_PORT, 0);
            if (port > 0) {
                buffer.append("Port=\"");
                buffer.append(Integer.toString(port));
                buffer.append("\"; ");
            }
            buffer.append("Version=1");
            response.addHeader(new Header(HTTPConstants.HEADER_SET_COOKIE2, buffer.toString()));
        }
    }
    
}
