/* Copyright 2004,2005 The Apache Software Foundation.
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

package org.apache.axis2.jaxws.description.builder;

import java.lang.annotation.Annotation;

public class ResponseWrapperAnnot implements javax.xml.ws.ResponseWrapper{

	private String 	localName;
	private String 	targetNamespace;
	private String 	className;			

	
	/**
     * A ResponseWrapperAnnot cannot be instantiated.
     */
	private  ResponseWrapperAnnot(){
		
	}
	
	private  ResponseWrapperAnnot(
			String localName,
			String targetNamespace,
			String className)
	{
		this.localName = localName;
		this.targetNamespace = targetNamespace;
		this.className = className;
	}

    public static ResponseWrapperAnnot createResponseWrapperAnnotImpl() {
        return new ResponseWrapperAnnot();
    }

    public static ResponseWrapperAnnot createResponseWrapperAnnotImpl( 
    			String localName,
    			String targetNamespace,
    			String className
    		) 
    {
        return new ResponseWrapperAnnot( localName, 
        								targetNamespace, 
        								className);
    }
	
	
	/**
	 * @return Returns the name.
	 */
	public String localName() {
		return this.localName;
	}
	
	/**
	 * @return Returns the targetNamespace.
	 */
	public String targetNamespace() {
		return this.targetNamespace;
	}
	
	/**
	 * @return Returns the wsdlLocation.
	 */
	public String className() {
		return this.className;
	}

	/**
	 * @param name The name to set.
	 */
	public void setLocalName(String localName) {
		this.localName = localName;
	}

	/**
	 * @param targetNamespace The targetNamespace to set.
	 */
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	/**
	 * @param wsdlLocation The wsdlLocation to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	//hmm, should we really do this
	public Class<Annotation> annotationType(){
		return Annotation.class;
	}


}
