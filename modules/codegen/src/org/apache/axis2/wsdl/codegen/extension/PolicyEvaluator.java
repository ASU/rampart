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

package org.apache.axis2.wsdl.codegen.extension;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.PolicyInclude;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.modules.Module;
import org.apache.axis2.modules.ModulePolicyExtension;
import org.apache.axis2.modules.PolicyExtension;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.util.Constants;
import org.apache.neethi.All;
import org.apache.neethi.Assertion;
import org.apache.neethi.ExactlyOne;
import org.apache.neethi.Policy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PolicyEvaluator implements CodeGenExtension {

    /**
     * Init method to initialization
     * @param configuration
     * @param namespace2ExtsMap
     */
    private void init(CodeGenConfiguration configuration, Map namespace2ExtsMap) {

       // adding default PolicyExtensions
       namespace2ExtsMap.put("http://schemas.xmlsoap.org/ws/2004/09/policy/optimizedmimeserialization",
               new MTOMPolicyExtension(configuration));
       namespace2ExtsMap.put("http://schemas.xmlsoap.org/ws/2004/09/policy/encoding",
               new EncodePolicyExtension());

       //set the policy handling template 
       configuration.putProperty("policyExtensionTemplate", "/org/apache/axis2/wsdl/template/java/PolicyExtensionTemplate.xsl");


        String repository = configuration.getRepositoryPath();

        if (repository == null) {    return;    }


        try {

            ConfigurationContext configurationCtx = ConfigurationContextFactory
                    .createConfigurationContextFromFileSystem(repository, null);
            AxisConfiguration axisConfiguration = configurationCtx
                    .getAxisConfiguration();

            for (Iterator iterator = axisConfiguration.getModules().values()
                    .iterator(); iterator.hasNext();) {

                AxisModule axisModule = (AxisModule) iterator.next();
                String[] namespaces = axisModule.getSupportedPolicyNamespaces();

                if (namespaces == null) {
                    continue;
                }

                Module module = axisModule.getModule();
                if (!(module instanceof ModulePolicyExtension)) {
                    continue;
                }

                PolicyExtension ext = ((ModulePolicyExtension) module).getPolicyExtension();

                for (int i = 0; i < namespaces.length; i++) {
                    namespace2ExtsMap.put(namespaces[i], ext);
                }
            }

        } catch (Exception e) {
            System.err
                    .println("cannot create repository : policy will not be supported");
        }
    }

    public void engage(CodeGenConfiguration configuration) {

        Map namespace2ExtMap = new HashMap();
         //initialize
        init(configuration,namespace2ExtMap);

        Document document = getEmptyDocument();
        Element rootElement = document.createElement("module-codegen-policy-extensions");

        AxisOperation axisOperation;
        QName opName;
        PolicyInclude policyInclude;
        Policy policy;


        for (Iterator iterator = configuration.getAxisService().getOperations(); iterator.hasNext(); ) {
            axisOperation = (AxisOperation) iterator.next();
            opName = axisOperation.getName();

            policyInclude = axisOperation.getPolicyInclude();
            policy = policyInclude.getEffectivePolicy();

            if (policy != null) {
                processPolicies(document, rootElement, policy, opName,namespace2ExtMap);
            }
        }

        configuration.putProperty("module-codegen-policy-extensions", rootElement);
    }

    /**
     * Process policies
     * @param document
     * @param rootElement
     * @param policy
     * @param opName
     */
    private void processPolicies(Document document, Element rootElement,
                                 Policy policy, QName opName,Map ns2Exts) {

        HashMap map = new HashMap();
        
        for (Iterator iterator = policy.getAlternatives(); iterator.hasNext();) {
            
            String targetNamesapce = null;
            Assertion assertion;
            List assertionList;
            
            for (Iterator assertions = ((List) iterator.next()).iterator(); assertions.hasNext();) {
              
                assertion = (Assertion) assertions.next();
                targetNamesapce = assertion.getName().getNamespaceURI();
                
                if ((assertionList = (List) map.get(targetNamesapce)) == null)  {
                    assertionList = new ArrayList();
                    map.put(targetNamesapce,assertionList);
                }
                
                assertionList.add(assertions);
            }
            
            // here we pick the first policy alternative and ignor the rest
            break;
        }
                
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            String targetNamespace = (String) iterator.next();
            PolicyExtension policyExtension = (PolicyExtension) ns2Exts.get(targetNamespace);

            if (policyExtension == null) {
                System.err.println("cannot find a PolicyExtension to process "
                        + targetNamespace + "type assertions");
                continue;
            }
            
            policyExtension.addMethodsToStub(document, rootElement, opName, (List) map.get(targetNamespace));
        }
    }

    private Document getEmptyDocument() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();

            return documentBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    class MTOMPolicyExtension implements PolicyExtension {

        private boolean setOnce = false;
        private CodeGenConfiguration configuration;

        public MTOMPolicyExtension(CodeGenConfiguration configuration) {
            this.configuration = configuration;
        }

        public void addMethodsToStub(Document document, Element element, QName operationName, List assertions) {
            
            // FIXME

//            if (!setOnce) {
//                 Object plainBase64PropertyMap = configuration.getProperty(Constants.PLAIN_BASE_64_PROPERTY_KEY);
//                 configuration.putProperty(Constants.BASE_64_PROPERTY_KEY, plainBase64PropertyMap);
//
//                 setOnce = true;
//            }
//
//            Element optimizeContent = document.createElement("optimizeContent");
//            Element opNameElement = document.createElement("opName");
//
//            opNameElement.setAttribute("ns-url", operationName.getNamespaceURI());
//            opNameElement.setAttribute("localName", operationName.getLocalPart());
//
//            optimizeContent.appendChild(opNameElement);
//
//            element.appendChild(optimizeContent);
        }
    }

    class EncodePolicyExtension implements PolicyExtension {
        public void addMethodsToStub(Document document, Element element, QName operationName, List assertions) {
            // TODO implement encoding
        }
    }
}
