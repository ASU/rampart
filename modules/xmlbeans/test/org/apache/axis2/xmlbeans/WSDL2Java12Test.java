package org.apache.axis2.xmlbeans;

import org.apache.axis2.wsdl.codegen.CodeGenerationException;
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

/**
 * Test for the WSDL that's unreferenced type
 */
public class WSDL2Java12Test extends WSDL2JavaFailureTestBase {

    protected void setUp() throws Exception {
        this.wsdlFileName = "unreferenced.wsdl";
        super.setUp();
    }


}
