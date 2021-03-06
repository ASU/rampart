package org.apache.axis2.schema.populate.other;

import junit.framework.TestCase;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.io.ByteArrayInputStream;

import org.apache.axiom.om.util.StAXUtils;
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

public class PopulateRecursiveTest extends TestCase {

// all are present
    private String xmlString1 = "<E xmlns=\"http://recursion.org\">" +
            "<E>test</E>" +
            "</E>";




    public void testPopulate1() throws Exception {
        populateAndAssert(xmlString1);
    }


    private void populateAndAssert(String s
    ) throws XMLStreamException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, IntrospectionException {
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new ByteArrayInputStream(s.getBytes()));
        Class clazz = Class.forName("org.recursion.E");
        Class innerClazz = clazz.getDeclaredClasses()[0];
        Method parseMethod = innerClazz.getMethod("parse", new Class[]{XMLStreamReader.class});
        Object obj = parseMethod.invoke(null, new Object[]{reader});

        assertNotNull(obj);

        Object eObject = null;
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        Method readMethod;

        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if ("e".equals(propertyDescriptor.getDisplayName())) {
                readMethod = propertyDescriptor.getReadMethod();
                eObject = readMethod.invoke(obj, null);

                assertNotNull(eObject);

                assertTrue(eObject.getClass().equals(Class.forName("org.recursion.TypeE")) );
            }
        }
    }



}
