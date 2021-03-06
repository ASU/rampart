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
package org.apache.axis2.schema.testsuite;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.StAXUtils;
import services.echo.types.*;

import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

public class XsdAnyElementsTest extends AbstractTest {

    public static final int MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST = 1;
    public static final int MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST = 2;
    public static final int MIN_EQUALS_ONE_NILLABLE_TRUE_TEST = 3;
    public static final int MIN_EQUALS_ONE_NILLABLE_FALSE_TEST = 4;

    public void testAnyTypeArray() {
        OMElement[] returnObject;
        System.out.println("minOccurs = 0 and nillable true");
        try {
            returnObject = testAnyTypeArray(null, MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{null}));
            returnObject = testAnyTypeArray(new OMElement[]{null}, MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{null}));
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement()}, MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement()}));
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement(), null}, MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement(), null}));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        System.out.println("minOccurs = 0 and nillable false");
        try {
            returnObject = testAnyTypeArray(null, MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, null));
            returnObject = testAnyTypeArray(new OMElement[]{null}, MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, null));
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement()}, MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement()}));
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement(), null}, MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement()}));
        } catch (Exception e) {
            fail();
        }

        System.out.println("minOccurs = 1 and nillable true");
        try {
            returnObject = testAnyTypeArray(null, MIN_EQUALS_ONE_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{null}));
            returnObject = testAnyTypeArray(new OMElement[]{null}, MIN_EQUALS_ONE_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{null}));
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement()}, MIN_EQUALS_ONE_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement()}));
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement(), null}, MIN_EQUALS_ONE_NILLABLE_TRUE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement(), null}));
        } catch (Exception e) {
            fail();
        }

        System.out.println("minOccurs = 1 and nillable false");
        try {
            returnObject = testAnyTypeArray(null, MIN_EQUALS_ONE_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, null));
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            returnObject = testAnyTypeArray(new OMElement[]{null}, MIN_EQUALS_ONE_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{null}));
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement(), null}, MIN_EQUALS_ONE_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement(), null}));
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            returnObject = testAnyTypeArray(new OMElement[]{getOMElement()}, MIN_EQUALS_ONE_NILLABLE_FALSE_TEST);
            assertTrue(assertArrayEqual(returnObject, new OMElement[]{getOMElement()}));
        } catch (Exception e) {
            fail();
        }
    }

    public OMElement[] testAnyTypeArray(OMElement[] innerElement, int type) throws Exception {
        OMElement omElement;
        OMElement[] returnObject = null;
        String omElementString;
        switch (type) {
            case MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST : {
                TestAnyType1 testAnyType = new TestAnyType1();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType1.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType1.Factory.parse(xmlReader).getTestValue();
                break;
            }
            case MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST : {
                TestAnyType3 testAnyType = new TestAnyType3();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType3.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType3.Factory.parse(xmlReader).getTestValue();
                break;
            }
            case MIN_EQUALS_ONE_NILLABLE_TRUE_TEST : {
                TestAnyType5 testAnyType = new TestAnyType5();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType5.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType5.Factory.parse(xmlReader).getTestValue();
                break;
            }
            case MIN_EQUALS_ONE_NILLABLE_FALSE_TEST : {
                TestAnyType7 testAnyType = new TestAnyType7();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType7.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType7.Factory.parse(xmlReader).getTestValue();
                break;
            }
        }
        return returnObject;
    }

    public void testAnyType() {

        OMElement returnObject;
        System.out.println("minOccurs = 0 nillable true");
        try {
            returnObject = testAnyType(null, MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST);
            assertTrue(isOMElementsEqual(returnObject, null));
            returnObject = testAnyType(getOMElement(), MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST);
            assertTrue(isOMElementsEqual(returnObject, getOMElement()));
        } catch (Exception e) {
            fail();
        }
        System.out.println("minOccurs = 0 nillable false");
        try {
            returnObject = testAnyType(null, MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST);
            assertTrue(isOMElementsEqual(returnObject, null));
            returnObject = testAnyType(getOMElement(), MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST);
            assertTrue(isOMElementsEqual(returnObject, getOMElement()));
        } catch (Exception e) {
            fail();
        }
        System.out.println("minOccurs = 1 nillable true");
        try {
            returnObject = testAnyType(null, MIN_EQUALS_ONE_NILLABLE_TRUE_TEST);
            assertTrue(isOMElementsEqual(returnObject, null));
            returnObject = testAnyType(getOMElement(), MIN_EQUALS_ONE_NILLABLE_TRUE_TEST);
            assertTrue(isOMElementsEqual(returnObject, getOMElement()));
        } catch (Exception e) {
            fail();
        }
        System.out.println("minOccurs = 1 nillable false");
        try {
            returnObject = testAnyType(null, MIN_EQUALS_ONE_NILLABLE_FALSE_TEST);
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            returnObject = testAnyType(getOMElement(), MIN_EQUALS_ONE_NILLABLE_FALSE_TEST);
            assertTrue(isOMElementsEqual(returnObject, getOMElement()));
        } catch (Exception e) {
            fail();
        }

    }

    public OMElement testAnyType(OMElement innerElement, int type) throws Exception {
        OMElement omElement;
        OMElement returnObject = null;
        String omElementString;
        switch (type) {
            case MIN_EQUALS_ZERO_NILLABLE_TRUE_TEST : {
                TestAnyType2 testAnyType = new TestAnyType2();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType2.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType2.Factory.parse(xmlReader).getTestValue();
                break;
            }
            case MIN_EQUALS_ZERO_NILLABLE_FALSE_TEST : {
                TestAnyType4 testAnyType = new TestAnyType4();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType4.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType4.Factory.parse(xmlReader).getTestValue();
                break;
            }
            case MIN_EQUALS_ONE_NILLABLE_TRUE_TEST : {
                TestAnyType6 testAnyType = new TestAnyType6();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType6.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType6.Factory.parse(xmlReader).getTestValue();
                break;
            }
            case MIN_EQUALS_ONE_NILLABLE_FALSE_TEST : {
                TestAnyType8 testAnyType = new TestAnyType8();
                testAnyType.setTestValue(innerElement);
                omElement = testAnyType.getOMElement(TestAnyType8.MY_QNAME, OMAbstractFactory.getSOAP12Factory());
                omElementString = omElement.toStringWithConsume();
                System.out.println("OMElement ==> " + omElementString);
                XMLStreamReader xmlReader =
                        StAXUtils.createXMLStreamReader(new ByteArrayInputStream(omElementString.getBytes()));
                returnObject = TestAnyType8.Factory.parse(xmlReader).getTestValue();
                break;
            }
        }
        return returnObject;
    }


}


