<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisOperation" %>
<%@ page import="org.apache.axis2.description.AxisService" %>
<%@ page import="org.apache.axis2.description.Parameter" %>
<%@ page import="org.apache.axis2.engine.AxisConfiguration" %>
<%@ page import="org.apache.axis2.util.JavaUtils" %>
<%@ page import="java.util.*" %>
<%--
/*
 * Copyright 2002,2004 The Apache Software Foundation.
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
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="include/httpbase.jsp"/>
<head><title>List Services</title>
    <link href="axis2-web/css/axis-style.css" rel="stylesheet" type="text/css"/>
</head>

<body>
<jsp:include page="include/header.inc" />
<jsp:include page="include/link-footer.jsp" />
<h1>Available services</h1>
<% String prefix = request.getAttribute("frontendHostUrl") + (String)request.getSession().getAttribute(Constants.SERVICE_PATH) + "/";
    String restPrefix = request.getAttribute("frontendHostUrl") + "rest/";
%>
<%
    HashMap serviceMap = (HashMap) request.getSession().getAttribute(Constants.SERVICE_MAP);
    request.getSession().setAttribute(Constants.SERVICE_MAP, null);
    Hashtable errornessservice = (Hashtable) request.getSession().getAttribute(Constants.ERROR_SERVICE_MAP);
    boolean status = false;
    if (serviceMap != null && !serviceMap.isEmpty()) {
        Iterator opItr;
        //HashMap operations;
        String serviceName;
        Collection servicecol = serviceMap.values();
        // Collection operationsList;
        for (Iterator iterator = servicecol.iterator(); iterator.hasNext();) {
            AxisService axisService = (AxisService) iterator.next();
            opItr = axisService.getOperations();
            //operationsList = operations.values();
            serviceName = axisService.getName();
%><h2><font color="blue"><a href="<%=prefix + axisService.getName()%>?wsdl"><%=serviceName%></a></font></h2>
<font color="blue">Service EPR : </font><font color="black"><%=prefix + axisService.getName()%></font><br>
<%
    // do we need to enable REST in the main servlet so that it handles both REST and SOAP messages
    boolean enableRESTInAxis2MainServlet = false;
    boolean disableREST = false;
    boolean disableSeperateEndpointForREST = false;
    AxisConfiguration axisConfiguration = axisService.getAxisConfiguration();

    Parameter parameter = axisConfiguration.getParameter(Constants.Configuration.ENABLE_REST_IN_AXIS2_MAIN_SERVLET);
    if (parameter != null) {
        enableRESTInAxis2MainServlet = !JavaUtils.isFalseExplicitly(parameter.getValue());
    }

    // do we need to completely disable REST support
    parameter = axisConfiguration.getParameter(Constants.Configuration.DISABLE_REST);
    if (parameter != null) {
        disableREST = !JavaUtils.isFalseExplicitly(parameter.getValue());
    }

    // Do we need to have a separate endpoint for REST
    parameter = axisConfiguration.getParameter(Constants.Configuration.DISABLE_SEPARATE_ENDPOINT_FOR_REST);
    if (parameter != null) {
        disableSeperateEndpointForREST = !JavaUtils.isFalseExplicitly(parameter.getValue());
    }

    if (enableRESTInAxis2MainServlet) {
%>
<font color="blue">Service REST epr : </font><font color="black"><%=prefix + axisService.getName()%></font>
<%
    }
    if (!disableREST && !disableSeperateEndpointForREST) {
        if (!enableRESTInAxis2MainServlet) {
%>
<font color="blue">Service REST epr : </font><font color="black"><%=restPrefix + axisService.getName()%></font>
<%
} else {
%>
<br />
<font color="blue">                  : </font><font color="black"><%=restPrefix + axisService.getName()%></font>
<%

    }
%>
<%
    }


    String serviceDescription = axisService.getServiceDescription();
    if (serviceDescription == null || "".equals(serviceDescription)) {
        serviceDescription = "No description available for this service";
    }
%>
<h4>Service Description : <font color="black"><%=serviceDescription%></h4>
<i><font color="blue">Service Status : <%=axisService.isActive() ? "Active" : "InActive"%></font></i><br>
<%
    if (opItr.hasNext()) {
%><i>Available Operations</i><%
} else {
%><i> There are no Operations specified</i><%
    }
    opItr = axisService.getOperations();
%><ul><%
    while (opItr.hasNext()) {
        AxisOperation axisOperation = (AxisOperation) opItr.next();
%><li><%=axisOperation.getName().getLocalPart()%></li>
    <%--                <br>Operation EPR : <%=prifix + axisService.getName().getLocalPart() + "/"+ axisOperation.getName().getLocalPart()%>--%>
    <%
        }
    %></ul>
<%
            status = true;
        }
    }
    if (errornessservice != null) {
        if (errornessservice.size() > 0) {
            request.getSession().setAttribute(Constants.IS_FAULTY, Constants.IS_FAULTY);
%>
<hr>

<h3><font color="blue">Faulty Services</font></h3>
<%
    Enumeration faultyservices = errornessservice.keys();
    while (faultyservices.hasMoreElements()) {
        String faultyserviceName = (String) faultyservices.nextElement();
%><h3><font color="blue"><a href="services/ListFaultyServices?serviceName=<%=faultyserviceName%>">
    <%=faultyserviceName%></a></font></h3>
<%
            }
        }
        status = true;
    }
    if (!status) {
%> No services listed! Try hitting refresh. <%
    }
%>
<jsp:include page="include/footer.inc" />
</body>
</html>
