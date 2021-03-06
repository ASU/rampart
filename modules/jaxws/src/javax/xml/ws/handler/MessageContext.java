/*
* Copyright 2004,2005 The Apache Software Foundation.
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
package javax.xml.ws.handler;

import java.util.Map;

public interface MessageContext extends Map<String, Object> {

    public enum Scope {
        APPLICATION,  
        HANDLER
    }

    public abstract void setScope(String s, Scope scope);

    public abstract Scope getScope(String s);

    public static final String MESSAGE_OUTBOUND_PROPERTY = "javax.xml.ws.handler.message.outbound";
    public static final String MESSAGE_ATTACHMENTS = "javax.xml.ws.binding.attachments";
    public static final String WSDL_DESCRIPTION = "javax.xml.ws.wsdl.description";
    public static final String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
    public static final String WSDL_PORT = "javax.xml.ws.wsdl.port";
    public static final String WSDL_INTERFACE = "javax.xml.ws.wsdl.interface";
    public static final String WSDL_OPERATION = "javax.xml.ws.wsdl.operation";
    public static final String HTTP_RESPONSE_CODE = "javax.xml.ws.http.response.code";
    public static final String HTTP_REQUEST_HEADERS = "javax.xml.ws.http.request.headers";
    public static final String HTTP_RESPONSE_HEADERS = "javax.xml.ws.http.response.headers";
    public static final String HTTP_REQUEST_METHOD = "javax.xml.ws.http.request.method";
    public static final String SERVLET_REQUEST = "javax.xml.ws.servlet.request";
    public static final String SERVLET_RESPONSE = "javax.xml.ws.servlet.response";
    public static final String SERVLET_CONTEXT = "javax.xml.ws.servlet.context";
}
