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


package org.apache.axis2.util;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.RelatesTo;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.*;
import org.apache.axis2.description.*;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisError;
import org.apache.axis2.engine.Handler;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.receivers.RawXMLINOutMessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

public class Utils {
    public static void addHandler(Flow flow, Handler handler, String phaseName) {
        HandlerDescription handlerDesc = new HandlerDescription();
        PhaseRule rule = new PhaseRule(phaseName);

        handlerDesc.setRules(rule);
        handler.init(handlerDesc);
        handlerDesc.setHandler(handler);
        flow.addHandler(handlerDesc);
    }

    public static MessageContext createOutMessageContext(MessageContext inMessageContext) throws AxisFault {
        MessageContext newmsgCtx = new MessageContext();

        newmsgCtx.setConfigurationContext(inMessageContext.getConfigurationContext());
        newmsgCtx.setSessionContext(inMessageContext.getSessionContext());
        newmsgCtx.setTransportIn(inMessageContext.getTransportIn());
        newmsgCtx.setTransportOut(inMessageContext.getTransportOut());

        Options oldOptions =
                inMessageContext.getOptions();

        newmsgCtx.setMessageID(UUIDGenerator.getUUID());
        newmsgCtx.setTo(oldOptions.getReplyTo());
        newmsgCtx.setProperty(AddressingConstants.WS_ADDRESSING_VERSION,
                inMessageContext.getProperty(AddressingConstants.WS_ADDRESSING_VERSION));
        newmsgCtx.setProperty(AddressingConstants.DISABLE_ADDRESSING_FOR_OUT_MESSAGES,
                inMessageContext.getProperty(AddressingConstants.DISABLE_ADDRESSING_FOR_OUT_MESSAGES));

        // do Target Resolution
        TargetResolver targetResolver = newmsgCtx.getConfigurationContext().getAxisConfiguration().getTargetResolverChain();
        if(targetResolver != null){
            targetResolver.resolveTarget(newmsgCtx);
        }

        newmsgCtx.addRelatesTo(new RelatesTo(oldOptions.getMessageId()));

        AxisService axisService = inMessageContext.getAxisService();
        if (axisService != null && Constants.SCOPE_SOAP_SESSION.equals(axisService.getScope())) {
            newmsgCtx.setReplyTo(new EndpointReference(AddressingConstants.Final.WSA_ANONYMOUS_URL));
            // add the service group id as a reference parameter
            String serviceGroupContextId = inMessageContext.getServiceGroupContextId();
            if (serviceGroupContextId != null && !"".equals(serviceGroupContextId)) {
                EndpointReference replyToEPR = newmsgCtx.getReplyTo();
                replyToEPR.addReferenceParameter(new QName(Constants.AXIS2_NAMESPACE_URI,
                        Constants.SERVICE_GROUP_ID, Constants.AXIS2_NAMESPACE_PREFIX), serviceGroupContextId);
            }
        } else {
            newmsgCtx.setReplyTo(new EndpointReference(AddressingConstants.Final.WSA_NONE_URI));
        }

        AxisOperation ao = inMessageContext.getAxisOperation();
        if (ao.getOutputAction() != null) {
            newmsgCtx.setWSAAction(ao.getOutputAction());
        } else {
            newmsgCtx.setWSAAction(oldOptions.getAction());
        }

        newmsgCtx.setAxisMessage(ao.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE));
        newmsgCtx.setOperationContext(inMessageContext.getOperationContext());
        newmsgCtx.setServiceContext(inMessageContext.getServiceContext());
        newmsgCtx.setProperty(MessageContext.TRANSPORT_OUT,
                inMessageContext.getProperty(MessageContext.TRANSPORT_OUT));
        newmsgCtx.setProperty(Constants.OUT_TRANSPORT_INFO,
                inMessageContext.getProperty(Constants.OUT_TRANSPORT_INFO));

        // Setting the charater set encoding
        newmsgCtx.setProperty(Constants.Configuration.CHARACTER_SET_ENCODING,
                inMessageContext.getProperty(Constants.Configuration.CHARACTER_SET_ENCODING));
        newmsgCtx.setDoingREST(inMessageContext.isDoingREST());
        newmsgCtx.setDoingMTOM(inMessageContext.isDoingMTOM());
        newmsgCtx.setServerSide(inMessageContext.isServerSide());
        newmsgCtx.setServiceGroupContextId(inMessageContext.getServiceGroupContextId());

        // write the Message to the Wire
        TransportOutDescription transportOut = newmsgCtx.getTransportOut();

        //there may be instance where you want to send the response to replyTo
        //and this default behaviour should happen if somebody (e.g. a module) has not already provided
        //a Sender.
        try {
            EndpointReference responseEPR = newmsgCtx.getTo();
            if (newmsgCtx.isServerSide() && responseEPR != null) {
                if (!responseEPR.hasAnonymousAddress() && !responseEPR.hasNoneAddress()) {
                    URI uri = new URI(responseEPR.getAddress());
                    String scheme = uri.getScheme();
                    if (!transportOut.getName().getLocalPart().equals(scheme)) {
                        ConfigurationContext configurationContext = newmsgCtx.getConfigurationContext();
                        transportOut = configurationContext.getAxisConfiguration()
                                .getTransportOut(new QName(scheme));
                        if (transportOut == null) {
                            throw new AxisFault("Can not find the transport sender : " + scheme);
                        }
                        newmsgCtx.setTransportOut(transportOut);
                    }
                    inMessageContext.getOperationContext().setProperty(
                            Constants.DIFFERENT_EPR, Constants.VALUE_TRUE);
                }
            }
        } catch (URISyntaxException e) {
            throw new AxisFault(e);
        }
        return newmsgCtx;
    }

    public static AxisService createSimpleService(QName serviceName, String className, QName opName)
            throws AxisFault {
        return createSimpleService(serviceName, new RawXMLINOutMessageReceiver(), className,
                opName);
    }

    public static AxisService createSimpleServiceforClient(QName serviceName, String className, QName opName)
            throws AxisFault {
        return createSimpleServiceforClient(serviceName, new RawXMLINOutMessageReceiver(), className,
                opName);
    }

    public static AxisService createSimpleService(QName serviceName,
                                                  MessageReceiver messageReceiver, String className, QName opName)
            throws AxisFault {
        AxisService service = new AxisService(serviceName.getLocalPart());

        service.setClassLoader(Thread.currentThread().getContextClassLoader());
        service.addParameter(new Parameter(Constants.SERVICE_CLASS, className));

        AxisOperation axisOp = new InOutAxisOperation(opName);

        axisOp.setMessageReceiver(messageReceiver);
        axisOp.setStyle(WSDLConstants.STYLE_RPC);
        service.addOperation(axisOp);
        service.mapActionToOperation(Constants.AXIS2_NAMESPACE_URI + "/" + opName.getLocalPart(), axisOp);

        return service;
    }

    public static AxisService createSimpleServiceforClient(QName serviceName,
                                                           MessageReceiver messageReceiver,
                                                           String className,
                                                           QName opName)
            throws AxisFault {
        AxisService service = new AxisService(serviceName.getLocalPart());

        service.setClassLoader(Thread.currentThread().getContextClassLoader());
        service.addParameter(new Parameter(Constants.SERVICE_CLASS, className));

        AxisOperation axisOp = new OutInAxisOperation(opName);

        axisOp.setMessageReceiver(messageReceiver);
        axisOp.setStyle(WSDLConstants.STYLE_RPC);
        service.addOperation(axisOp);

        return service;
    }

    public static ServiceContext fillContextInformation(AxisService axisService,
                                                        ConfigurationContext configurationContext) {

        // 2. if null, create new opCtxt
        // fill the service group context and service context info
        return fillServiceContextAndServiceGroupContext(axisService, configurationContext);
    }

    private static ServiceContext fillServiceContextAndServiceGroupContext(AxisService axisService,
                                                                           ConfigurationContext configurationContext) {
        String serviceGroupContextId = UUIDGenerator.getUUID();
        ServiceGroupContext serviceGroupContext = new ServiceGroupContext(configurationContext,
                (AxisServiceGroup) axisService.getParent());

        serviceGroupContext.setId(serviceGroupContextId);
        configurationContext.registerServiceGroupContext(serviceGroupContext);

        return new ServiceContext(axisService, serviceGroupContext);
    }

    /**
     * Break a full path into pieces
     *
     * @param path
     * @return an array where element [0] always contains the service, and element 1, if not null, contains
     *         the path after the first element. all ? parameters are discarded.
     */
    public static String[] parseRequestURLForServiceAndOperation(String path, String servicePath) {
        if (path == null) {
            return null;
        }
        String[] values = new String[2];

        // TODO. This is kind of brittle. Any service with the name /services would cause fun.
        int index = path.lastIndexOf(servicePath);
        String service;

        if (-1 != index) {
            int serviceStart = index + servicePath.length();

            if (path.length() > serviceStart + 1) {
                service = path.substring(serviceStart + 1);

                int queryIndex = service.indexOf('?');

                if (queryIndex > 0) {
                    service = service.substring(0, queryIndex);
                }

                int operationIndex = service.indexOf('/');

                if (operationIndex > 0) {
                    values[0] = service.substring(0, operationIndex);
                    values[1] = service.substring(operationIndex + 1);
                } else {
                    values[0] = service;
                }
            }
        }

        return values;
    }

    public static ConfigurationContext getNewConfigurationContext(String repositry)
            throws Exception {
        File file = new File(repositry);
        if (!file.exists()) {
            throw new Exception("repository directory " + file.getAbsolutePath()
                    + " does not exists");
        }
        File axis2xml = new File(file, "axis.xml");
        String axis2xmlString = null;
        if (axis2xml.exists()) {
            axis2xmlString = axis2xml.getName();
        }
        return ConfigurationContextFactory.createConfigurationContextFromFileSystem(file.getAbsolutePath(), axis2xmlString);
    }

    public static String getParameterValue(Parameter param) {
        if (param == null) {
            return null;
        } else {
            return (String) param.getValue();
        }
    }

    /**
     * To get the name of the module , where archive name is combination of module name + its version
     * The format of the module version will be like follow
     * moduleName-00.0000 as an exmple addressing-01.0001.aar
     */

    public static String getModuleName(String moduleName) {
        char version_seperator = '-';
        int version_index = moduleName.lastIndexOf(version_seperator);
        if (version_index > 0) {
            return moduleName.substring(0, version_index);
        } else {
            return moduleName;
        }
    }

    public static String getModuleVersion(String moduleName) {
        char version_seperator = '-';
        int version_index = moduleName.lastIndexOf(version_seperator);
        if (version_index > 0) {
            return moduleName.substring(version_index + 1, moduleName.length());
        } else {
            return null;
        }
    }


    public static QName getModuleName(String name, String versionID) {
        String moduleName;
        if (versionID != null && versionID.length() != 0) {
            moduleName = name + "-" + versionID;
        } else {
            moduleName = name;
        }
        return new QName(moduleName);
    }

    /**
     * Will check whether a given module can be engage or not
     * if the version mismamathc then thow en exception
     * - if he trying to engage the same module then method will returen false
     * - else it will return true
     *
     * @param deployingModuleName
     * @param deployedModulename
     * @throws AxisFault
     */
    public static boolean checkVersion(QName deployingModuleName,
                                       QName deployedModulename) throws AxisFault {
        String module1name = getModuleName(deployingModuleName.getLocalPart());
        String module2name = getModuleName(deployedModulename.getLocalPart());
        String module1version = getModuleVersion(deployingModuleName.getLocalPart());
        String module2version = getModuleVersion(deployedModulename.getLocalPart());
        if (module1name.equals(module2name)) {
            if (module1version != null) {
                if (!module1version.equals(module2version)) {
                    throw new AxisFault("trying to engage two different module versions " +
                            module1version + " : " + module2version);
                } else {
                    return false;
                }
            } else if (module2version == null) {
                return false;
            }
        }
        return true;
    }

    public static void calculateDefaultModuleVersion(HashMap modules, AxisConfiguration axisConfig) {
        Iterator allModules = modules.values().iterator();
        HashMap defaultModules = new HashMap();
        while (allModules.hasNext()) {
            AxisModule axisModule = (AxisModule) allModules.next();
            QName moduleName = axisModule.getName();
            String moduleNameString = getModuleName(moduleName.getLocalPart());
            String moduleVersionString = getModuleVersion(moduleName.getLocalPart());
            String currentDefaultVerison = (String) defaultModules.get(moduleNameString);
            if (currentDefaultVerison != null) {
                // if the module version is null then , that will be ignore in this case
                if (moduleVersionString != null && isLatest(moduleVersionString, currentDefaultVerison)) {
                    defaultModules.put(moduleNameString, moduleVersionString);
                }
            } else {
                defaultModules.put(moduleNameString, moduleVersionString);
            }

        }
        Iterator def_mod_itr = defaultModules.keySet().iterator();
        while (def_mod_itr.hasNext()) {
            String moduleName = (String) def_mod_itr.next();
            axisConfig.addDefaultModuleVersion(moduleName, (String) defaultModules.get(moduleName));
        }
    }

    public static boolean isLatest(String moduleVersion, String currentDefaultVersion) {
        if ("SNAPSHOT".equals(moduleVersion)) {
            return true;
        } else {
            float m_version = Float.parseFloat(moduleVersion);
            float m_c_vresion = Float.parseFloat(currentDefaultVersion);
            return m_version > m_c_vresion;
        }
    }

    public static boolean isExplicitlyTrue(MessageContext messageContext, String propertyName) {
        Object flag = messageContext.getProperty(propertyName);
        return JavaUtils.isTrueExplicitly(flag);
    }

    /**
     * Maps the String URI of the Message exchange pattern to a integer.
     * Further, in the first lookup, it will cache the looked
     * up value so that the subsequent method calls are extremely efficient.
     */
    public static int getAxisSpecifMEPConstant(String messageExchangePattern) {


        int mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_INVALID;

        if (WSDLConstants.WSDL20_2004Constants.MEP_URI_IN_OUT.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_IN_OUT.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_IN_OUT;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_IN_ONLY.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_IN_ONLY.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_IN_ONLY;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_IN_OPTIONAL_OUT.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_IN_OPTIONAL_OUT.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_IN_OPTIONAL_OUT;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_OUT_IN.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_OUT_IN.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_OUT_IN;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_OUT_ONLY.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_OUT_ONLY.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_OUT_ONLY;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_OUT_OPTIONAL_IN.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_OUT_OPTIONAL_IN.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_OUT_OPTIONAL_IN;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_ROBUST_IN_ONLY.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_ROBUST_IN_ONLY.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_ROBUST_IN_ONLY;
        } else if (WSDLConstants.WSDL20_2004Constants.MEP_URI_ROBUST_OUT_ONLY.equals(messageExchangePattern) || WSDLConstants.WSDL20_2006Constants.MEP_URI_ROBUST_OUT_ONLY.equals(messageExchangePattern)) {
            mepConstant = WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_ROBUST_OUT_ONLY;
        }

        if (mepConstant == WSDLConstants.WSDL20_2004Constants.MEP_CONSTANT_INVALID) {
            throw new AxisError(Messages.getMessage("mepmappingerror"));
        }


        return mepConstant;
    }

}
