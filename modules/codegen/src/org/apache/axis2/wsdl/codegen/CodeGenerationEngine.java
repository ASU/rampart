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

package org.apache.axis2.wsdl.codegen;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDL20ToAxisServiceBuilder;
import org.apache.axis2.util.CommandLineOption;
import org.apache.axis2.util.CommandLineOptionConstants;
import org.apache.axis2.util.CommandLineOptionParser;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.codegen.emitter.Emitter;
import org.apache.axis2.wsdl.codegen.extension.CodeGenExtension;
import org.apache.axis2.wsdl.databinding.TypeMapper;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.axis2.wsdl.util.ConfigPropertyFileLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeGenerationEngine {

    private static final Log log = LogFactory.getLog(CodeGenerationEngine.class);

    /** Array List for pre-extensions. Extensions that run before the emitter */
    private List preExtensions = new ArrayList();
    /** Array List for post-extensions. Extensions that run after the codegens*/
    private List postExtensions = new ArrayList();

    /** Codegen configuration  reference */
    private CodeGenConfiguration configuration;

    /**
     * 
     * @param configuration
     * @throws CodeGenerationException
     */
    public CodeGenerationEngine(CodeGenConfiguration configuration) throws CodeGenerationException {
        this.configuration = configuration;
        loadExtensions();
    }

    /**
     *
     * @param parser
     * @throws CodeGenerationException
     */
    public CodeGenerationEngine(CommandLineOptionParser parser) throws CodeGenerationException {
        Map allOptions = parser.getAllOptions();
        String wsdlUri;
        try {

            CommandLineOption option =
                    (CommandLineOption)allOptions.
                            get(CommandLineOptionConstants.WSDL2JavaConstants.WSDL_LOCATION_URI_OPTION);
            wsdlUri = option.getOptionValue();
            configuration = new CodeGenConfiguration(allOptions);
            

            if(CommandLineOptionConstants.WSDL2JavaConstants.WSDL_VERSION_2.
                    equals(configuration.getWSDLVersion())){
                configuration.setAxisService(
                        new WSDL20ToAxisServiceBuilder(wsdlUri,
                                configuration.getServiceName(),
                                configuration.getPortName()).
                                populateService());
            }else{
                //It'll be WSDL 1.1
                Definition wsdl4jDef = readInTheWSDLFile(wsdlUri);
                QName serviceQname = null;
                if (configuration.getServiceName()!=null){
                    serviceQname = new QName(wsdl4jDef.getTargetNamespace(), configuration.getServiceName());
                }

                configuration.setAxisService(new WSDL11ToAxisServiceBuilder(
                        wsdl4jDef,
                        serviceQname,
                        configuration.getPortName()).
                        populateService()
                );
            }
            
        } catch (AxisFault axisFault) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wsdlParsingException"), axisFault);
        } catch (WSDLException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wsdlParsingException"), e);
        } catch (Exception e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wsdlParsingException"), e);
        }

        configuration.setBaseURI(getBaseURI(wsdlUri));
        loadExtensions();
    }

    /**
     * Loads the relevant preExtensions
     *
     * @throws CodeGenerationException
     */
    private void loadExtensions() throws CodeGenerationException {
        //load pre extensions
        String[] extensions = ConfigPropertyFileLoader.getExtensionClassNames();
        if (extensions!=null){
            for (int i = 0; i < extensions.length; i++) {
                //load the Extension class
                addPreExtension((CodeGenExtension) getObjectFromClassName(extensions[i].trim()));
            }
        }
        
        //load post extensions
        String[] postExtensions = ConfigPropertyFileLoader.getPostExtensionClassNames();
        if (postExtensions!=null){
            for (int i = 0; i < postExtensions.length; i++) {
                //load the Extension class
                addPostExtension((CodeGenExtension) getObjectFromClassName(postExtensions[i].trim()));
            }
        }

    }

    /**
     * Adds a given extension to the list
     * @param ext
     */
    private void addPreExtension(CodeGenExtension ext) {
        if(ext != null) {
            preExtensions.add(ext);
        }
    }

    /**
     * Adds a given extension to the list
     * @param ext
     */
    private void addPostExtension(CodeGenExtension ext) {
        if(ext != null) {
            postExtensions.add(ext);
        }
    }

    /**
     * Generate the code!!
     * @throws CodeGenerationException
     */
    public void generate() throws CodeGenerationException {
        try {
            //engage the pre-extensions
            for (int i = 0; i < preExtensions.size(); i++) {
                ((CodeGenExtension) preExtensions.get(i)).engage(configuration);
            }

            Emitter emitter;


            TypeMapper mapper = configuration.getTypeMapper();
            if (mapper == null) {
                // this check is redundant here. The default databinding extension should
                // have already figured this out and thrown an error message. However in case the
                // users decides to mess with the config it is safe to keep this check in order to throw
                // a meaningful error message
                throw new CodeGenerationException(CodegenMessages.getMessage("engine.noProperDatabindingException"));
            }

            //Find and invoke the emitter by reflection
            Map emitterMap = ConfigPropertyFileLoader.getLanguageEmitterMap();
            String className = (String)emitterMap.get(configuration.getOutputLanguage());
            if (className != null) {
                emitter = (Emitter) getObjectFromClassName(className);
                emitter.setCodeGenConfiguration(configuration);
                emitter.setMapper(mapper);
            } else {
                throw new Exception(CodegenMessages.getMessage("engine.emitterMissing"));
            }


            //invoke the necessary methods in the emitter
            if (configuration.isServerSide()) {
                emitter.emitSkeleton();
                // if the users want both client and server, it would be in the
                // generate all option
                if (configuration.isGenerateAll()) {
                    emitter.emitStub();
                }
            }else{
                emitter.emitStub();
            }

            //engage the post-extensions
            for (int i = 0; i < postExtensions.size(); i++) {
                ((CodeGenExtension) postExtensions.get(i)).engage(configuration);
            }

        } catch (ClassCastException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.wrongEmitter"), e);
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }


    }


    /**
     * Read the WSDL file
     * @param uri
     * @throws WSDLException
     */
    private Definition readInTheWSDLFile(String uri) throws WSDLException {

        WSDLReader reader =
                WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);

        File file = new File(uri);
        String baseURI;

        if (uri.startsWith("http://")){
            baseURI = uri;
        } else{
            if(file.getParentFile() == null){
                try {
                    baseURI = new File(".").getCanonicalFile().toURI().toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                baseURI = file.getParentFile().toURI().toString();
            }
        }


        Document doc;
        try {
            doc = XMLUtils.newDocument(uri);
        } catch (ParserConfigurationException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser Configuration Error",
                    e);
        } catch (SAXException e) {
            throw new WSDLException(WSDLException.PARSER_ERROR,
                    "Parser SAX Error",
                    e);

        } catch (IOException e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        return reader.readWSDL(baseURI, doc);
    }




    /**
     * gets a object from the class
     *
     * @param className
     */
    private Object getObjectFromClassName(String className) throws CodeGenerationException {
        try {
            Class extensionClass = getClass().getClassLoader().loadClass(className);
            return extensionClass.newInstance();
        } catch (ClassNotFoundException e) {
            log.debug(CodegenMessages.getMessage("engine.extensionLoadProblem"), e);
            return null;
        } catch (InstantiationException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.extensionInstantiationProblem"), e);
        } catch (IllegalAccessException e) {
            throw new CodeGenerationException(CodegenMessages.getMessage("engine.illegalExtension"), e);
        } catch (NoClassDefFoundError e) {
            log.debug(CodegenMessages.getMessage("engine.extensionLoadProblem"), e);
            return null;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }

    }

    /**
     * calculates the base URI
     * Needs improvement but works fine for now ;)
     * @param currentURI
     */
    private String getBaseURI(String currentURI){
        String baseURI;
        if (!currentURI.startsWith("http://")) {
            // the uri should be a file
            try {
                currentURI = new File(currentURI).toURL().toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Cannot find baseuri for :" + currentURI);
            }
        }
        String uriFragment = currentURI.substring(0, currentURI.lastIndexOf("/"));
        baseURI = uriFragment + (uriFragment.endsWith("/") ? "" : "/");
        return baseURI;
    }
}
