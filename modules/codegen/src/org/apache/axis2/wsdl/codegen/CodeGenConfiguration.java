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

import org.apache.axis2.description.AxisService;
import org.apache.axis2.util.CommandLineOptionConstants;
import org.apache.axis2.util.URLProcessor;
import org.apache.axis2.wsdl.databinding.TypeMapper;
import org.apache.axis2.wsdl.util.ConfigPropertyFileLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CodeGenConfiguration implements CommandLineOptionConstants {

    /**  Axis Service reference*/
    private AxisService axisService;
    /**  Base URI */
    private String baseURI;
    /** path to the repository - used for evaluating policy */
    private String repositoryPath;
    /** Mapping file including the qname to type map */
    private File typeMappingFile;

    /** keeps the WSDL version  - default is 1.1*/
    private String WSDLVersion = WSDL2JavaConstants.WSDL_VERSION_1;

    public String getWSDLVersion() {
        return WSDLVersion;
    }

    public void setWSDLVersion(String WSDLVersion) {
        this.WSDLVersion = WSDLVersion;
    }

    public File getTypeMappingFile() {
        return typeMappingFile;
    }

    public void setTypeMappingFile(File typeMappingFile) {
        this.typeMappingFile = typeMappingFile;
    }

    /**
     * A map to keep the custom namespace and package name mappings
     */
    private Map uri2PackageNameMap;


    public Map getUri2PackageNameMap() {
        return uri2PackageNameMap;
    }

    public void setUri2PackageNameMap(Map uri2PackageNameMap) {
        if (this.uri2PackageNameMap==null){
            this.uri2PackageNameMap = uri2PackageNameMap;
        }else{
            this.uri2PackageNameMap.putAll(uri2PackageNameMap);
        }
    }

    /**
     * Says whether to flatten the files or put them in specific folders
     */
    private boolean flattenFiles = false;

    public boolean isFlattenFiles() {
        return flattenFiles;
    }

    public void setFlattenFiles(boolean flattenFiles) {
        this.flattenFiles = flattenFiles;
    }

    /**
     * Folder name for the resource files
     */
    private  String resourceLocation = ConfigPropertyFileLoader.getResourceFolderName();

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    /**
     * Folder name for the source files
     */
    private String sourceLocation  = ConfigPropertyFileLoader.getSrcFolderName();

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    /**
     * Determines whether the parameters are wrapped or unwrapped
     * true by default
     */
    private boolean parametersWrapped = true;


    public boolean isParametersWrapped() {
        return parametersWrapped;
    }

    public void setParametersWrapped(boolean parametersWrapped) {
        this.parametersWrapped = parametersWrapped;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public Map getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Map configurationProperties) {
        this.configurationProperties = configurationProperties;
    }


    public void setOutputLanguage(String outputLanguage) {
        this.outputLanguage = outputLanguage;
    }

    public void setAdvancedCodeGenEnabled(boolean advancedCodeGenEnabled) {
        this.advancedCodeGenEnabled = advancedCodeGenEnabled;
    }

    public void setAsyncOn(boolean asyncOn) {
        this.asyncOn = asyncOn;
    }

    public void setSyncOn(boolean syncOn) {
        this.syncOn = syncOn;
    }

    public void setServerSide(boolean serverSide) {
        this.serverSide = serverSide;
    }

    public void setGenerateDeployementDescriptor(boolean generateDeployementDescriptor) {
        this.generateDeployementDescriptor = generateDeployementDescriptor;
    }

    public void setWriteTestCase(boolean writeTestCase) {
        this.writeTestCase = writeTestCase;
    }

    public void setOutputLocation(File outputLocation) {
        this.outputLocation = outputLocation;
    }

    private File outputLocation;

    //get the defaults for these from the property file
    private String outputLanguage = ConfigPropertyFileLoader.getDefaultLanguage();
    private String databindingType = ConfigPropertyFileLoader.getDefaultDBFrameworkName();
    private boolean advancedCodeGenEnabled = false;


    private boolean asyncOn = true;
    private boolean syncOn = true;
    private boolean serverSide = false;
    private boolean generateDeployementDescriptor = true;
    private boolean writeTestCase = false;
    private boolean writeMessageReceiver = true;
    private String packageName = URLProcessor.DEFAULT_PACKAGE;

    // Default packClasses is true, which means the classes generated
    // by default are wrapped. The effect of this setting will be controlled
    // to some extent, by the other settings as well.
    private boolean packClasses = true;

    private boolean generateAll = false;

    //user selected portname
    private String portName;
    //user selected servicename
    private String serviceName;

    //option to generate server side interface or not
    private boolean serverSideInterface = false;


    public boolean isServerSideInterface() {
        return serverSideInterface;
    }

    public void setServerSideInterface(boolean serverSideInterface) {
        this.serverSideInterface = serverSideInterface;
    }


    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * A hashmap to hang the property objects
     */
    private Map policyMap = new HashMap();

    /*
    * A hashmap of properties that may be populated on the way. extensions can populate it
    * This can be used to keep non specific information
    */
    private Map configurationProperties = new HashMap();


    public boolean isGenerateAll() {
        return generateAll;
    }

    public void setGenerateAll(boolean generateAll) {
        this.generateAll = generateAll;
    }

    /**
     * Gets the wrap classes flag.
     *
     * @return Returns true if it is a wrap class, else returns false.
     */
    public boolean isPackClasses() {
        return packClasses;
    }

    /**
     * Sets the wrap classes flag.
     *
     * @param packClasses
     */
    public void setPackClasses(boolean packClasses) {
        this.packClasses = packClasses;
    }

    /**
     * Gets the policy map.
     *
     * @return Returns Map.
     */
    public Map getPolicyMap() {
        return policyMap;
    }

    /**
     * Sets the policy map.
     *
     * @param policyMap
     */
    public void setPolicyMap(Map policyMap) {
        this.policyMap = policyMap;
    }


    /**
     * Puts a property into the configuration.
     *
     * @param key
     * @param value
     */
    public void putProperty(Object key, Object value) {
        configurationProperties.put(key, value);
    }

    /**
     * Gets the property from the configuration.
     *
     * @param key
     * @return Returns the property as Object.
     */
    public Object getProperty(Object key) {
        return configurationProperties.get(key);
    }

    /**
     * Gets all property objects.
     *
     * @return Returns Map of all properties.
     */
    public Map getProperties() {
        return configurationProperties;
    }

    private TypeMapper typeMapper;


    /**
     * @return Returns TypeMapper.
     */
    public TypeMapper getTypeMapper() {
        return typeMapper;
    }

    /**
     * @param typeMapper
     */
    public void setTypeMapper(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    /**
     * @return Returns String.
     */
    public String getDatabindingType() {
        return databindingType;
    }

    /**
     * @param databindingType
     */
    public void setDatabindingType(String databindingType) {
        this.databindingType = databindingType;
    }


    /**
     * Constructor for the configuration. It populates the values using the options map.
     *
     * @param optionMap
     */
    public CodeGenConfiguration(AxisService service, Map optionMap) {
        this.axisService = service;
        CodegenConfigLoader.loadConfig(this,optionMap);
    }

    /**
     * Constructor for the configuration. It populates the values using the options map.
     *
     * @param optionMap
     */
    public CodeGenConfiguration(Map optionMap) {
        CodegenConfigLoader.loadConfig(this,optionMap);
    }



    /**
     * @return Returns the outputLocation.
     */
    public File getOutputLocation() {
        return outputLocation;
    }

    public String getOutputLanguage() {
        return outputLanguage;
    }

    public boolean isAdvancedCodeGenEnabled() {
        return advancedCodeGenEnabled;
    }


    /**
     * @return Returns the packageName.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName The packageName to set.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public boolean isAsyncOn() {
        return asyncOn;
    }


    public boolean isSyncOn() {
        return syncOn;
    }

    public boolean isServerSide() {
        return serverSide;
    }

    public boolean isGenerateDeployementDescriptor() {
        return generateDeployementDescriptor;
    }

    public boolean isWriteTestCase() {
        return writeTestCase;
    }


    public boolean isWriteMessageReceiver() {
        return writeMessageReceiver;
    }

    public void setWriteMessageReceiver(boolean writeMessageReceiver) {
        this.writeMessageReceiver = writeMessageReceiver;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public AxisService getAxisService() {
        return axisService;
    }

    public void setAxisService(AxisService axisService) {
        this.axisService = axisService;
        if (packageName == null || packageName.length() == 0 || URLProcessor.DEFAULT_PACKAGE.equals(packageName)) {
            //use the target namespace from the axis service to form a package name
            packageName = URLProcessor.makePackageName(
                    axisService.getTargetNamespace()
            );
        }
    }
        
    	/**
	 * This flag determines whether the generated classes are expected to be
	 * backword compatible with Axis 1.x
	 */
	private boolean backwordCompatibilityMode = false;

	public boolean isBackwordCompatibilityMode() {
		return backwordCompatibilityMode;
	}

	public void setBackwordCompatibilityMode(boolean backwordCompatibilityMode) {
		this.backwordCompatibilityMode = backwordCompatibilityMode;
	}

    /**
     * Should we suppress namespace prefixes
     */
    private boolean suppressPrefixesMode = false;

    public boolean isSuppressPrefixesMode() {
        return suppressPrefixesMode;
    }

    public void setSuppressPrefixesMode(boolean suppressPrefixesMode) {
        this.suppressPrefixesMode = suppressPrefixesMode;
    }
}
