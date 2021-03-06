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

public class WebFaultAnnot implements javax.xml.ws.WebFault{

	private String name = "return";
	private String targetNamespace = "";
	private String faultBean = "";
	
	/**
     * A WebFaultAnnot cannot be instantiated.
     */
	private  WebFaultAnnot(){
		
	}
	
    public static WebFaultAnnot createWebFaultAnnotImpl() {
        return new WebFaultAnnot();
    }
    
    /**
     * Get the 'name'
     * @return String 
     */
	public String name() {
		return this.name;
	}
	
	public String targetNamespace() {
		return this.targetNamespace;
	}
	
	public String faultBean() {
		return this.faultBean;
	}
				
	/**
	 * @param faultBean The faultBean to set.
	 */
	public void setFaultBean(String faultBean) {
		this.faultBean = faultBean;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param targetNamespace The targetNamespace to set.
	 */
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public Class<Annotation> annotationType(){
		return Annotation.class;
	}

}
