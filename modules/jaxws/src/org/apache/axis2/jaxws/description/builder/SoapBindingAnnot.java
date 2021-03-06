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

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

public class SoapBindingAnnot implements javax.jws.soap.SOAPBinding{

    private Style style = Style.DOCUMENT;
    private Use use = Use.LITERAL;
    private ParameterStyle parameterStyle = ParameterStyle.WRAPPED;

	/**
     * A SoapBindingAnnot cannot be instantiated.
     */
	private  SoapBindingAnnot(){
		
	}
	
    public static SoapBindingAnnot createSoapBindingAnnotImpl() {
        return new SoapBindingAnnot();
    }

    public Style style(){
		return this.style;
	}
		
    public Use use(){
		return this.use;
	}
	
    public ParameterStyle parameterStyle(){
		return this.parameterStyle;
	}
			

	/**
	 * @param parameterStyle The parameterStyle to set.
	 */
	public void setParameterStyle(ParameterStyle parameterStyle) {
		this.parameterStyle = parameterStyle;
	}

	/**
	 * @param style The style to set.
	 */
	public void setStyle(Style style) {
		this.style = style;
	}

	/**
	 * @param use The use to set.
	 */
	public void setUse(Use use) {
		this.use = use;
	}

	public Class<Annotation> annotationType(){
		return Annotation.class;
	}

}
