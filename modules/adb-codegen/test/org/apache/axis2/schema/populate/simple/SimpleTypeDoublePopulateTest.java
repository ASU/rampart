package org.apache.axis2.schema.populate.simple;

import org.apache.axis2.databinding.utils.ConverterUtil;
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

public class SimpleTypeDoublePopulateTest extends AbstractSimplePopulater{
    private String values[]= {
            "20300.00554433422",
            "203045534234",
            "66733.0060604343"
    };
    private String xmlString[] = {
            "<doubleParam xmlns=\"http://soapinterop.org/xsd\">"+values[0]+"</doubleParam>",
            "<doubleParam xmlns=\"http://soapinterop.org/xsd\">"+values[1]+"</doubleParam>",
            "<doubleParam xmlns=\"http://soapinterop.org/xsd\">"+values[2]+"</doubleParam>"
    };

    protected void setUp() throws Exception {
       className = "org.soapinterop.xsd.DoubleParam";
       propertyClass = float.class;
    }

    // force others to implement this method
    public void testPopulate() throws Exception {
         for (int i = 0; i < values.length; i++) {
            checkValue(xmlString[i],values[i]);
        }
    }

    protected String convertToString(Object o) {
        return  ConverterUtil.convertToString((Double)o) ;
    }
}
