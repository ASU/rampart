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

import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.ws.commons.schema.XmlSchema;

import java.util.List;

public class WSDLValidatorExtension extends AbstractCodeGenerationExtension {

    public void engage(CodeGenConfiguration configuration) throws CodeGenerationException {
        //WSDLDescription wom = this.configuration.getWom();
        List schemaList = configuration.getAxisService().getSchema();
        if (schemaList == null || schemaList.isEmpty()) {
            //there are no types to be considered
            return;
        }

        for (int i = 0; i < schemaList.size(); i++) {
            XmlSchema s = (XmlSchema) schemaList.get(i);
            if (s.getIncludes().getCount() != 0) {
                //there are some included - now see whether there are any
                //elements or types declared!
                if (s.getElements().getCount() == 0 &&
                        s.getSchemaTypes().getCount() == 0 &&
                        s.getGroups().getCount() == 0 &&
                        s.getTargetNamespace() == null) {
                    // if there's no targetNamespace there's probably no name, but try it anyway
                    throw new CodeGenerationException(
                            CodegenMessages.getMessage("extension.invalidWSDL", s.toString()));
                }

            }
        }


    }
}
