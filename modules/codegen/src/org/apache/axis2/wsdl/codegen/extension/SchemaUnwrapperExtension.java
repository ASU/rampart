package org.apache.axis2.wsdl.codegen.extension;

import org.apache.axis2.AxisFault;
import org.apache.axis2.util.URLProcessor;
import org.apache.axis2.description.AxisMessage;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.util.URLProcessor;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.wsdl.WSDLUtil;
import org.apache.axis2.wsdl.codegen.CodeGenConfiguration;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.axis2.wsdl.util.ConfigPropertyFileLoader;
import org.apache.axis2.wsdl.util.Constants;
import org.apache.axis2.wsdl.util.MessagePartInformationHolder;
import org.apache.ws.commons.schema.*;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
 * This extension invokes the schema unwrapper depending on the users setting.
 * it is desirable to put this extension before other extensions since extnsions
 * such as the databinding extension may well depend on the schema being unwrapped
 * previously.
 * For a complete unwrap the following format of the schema is expected
 * &lt; element &gt;
 * &lt; complexType &gt;
 * &lt; sequence &gt;
 * &lt; element /&gt;
 * &lt; /sequence &gt;
 * &lt; /complexType &gt;
 * &lt; /element &gt;
 * <p/>
 * When an unwrapped WSDL is encountered Axis2 generates a wrapper schema
 * and that wrapper schema has the above mentioned format. This unwrapping algorithm
 * will work on a pure doc/lit WSDL if it has the above mentioned format
 * only
 */
public class SchemaUnwrapperExtension extends AbstractCodeGenerationExtension {

    /**
     * @param configuration
     * @throws CodeGenerationException
     */
    public void engage(CodeGenConfiguration configuration) throws CodeGenerationException {
        if (!configuration.isParametersWrapped()) {

            // A check to avoid nasty surprises - Since unwrapping is not
            // supported by all frameworks, we check the framework name to be
            // compatible
            if (!ConfigPropertyFileLoader.getUnwrapSupportedFrameworkNames().
                    contains(configuration.getDatabindingType())) {
                throw new CodeGenerationException(
                        CodegenMessages.getMessage("extension.unsupportedforunwrapping"));
            } else if (!ConfigPropertyFileLoader.getUnwrapDirectFrameworkNames().
                    contains(configuration.getDatabindingType())) {

                //walk the schema and find the top level elements
                AxisService axisService = configuration.getAxisService();

                for (Iterator operations = axisService.getOperations();
                     operations.hasNext();) {
                    AxisOperation op = (AxisOperation) operations.next();

                    if (WSDLUtil.isInputPresentForMEP(op.getMessageExchangePattern())) {
                        walkSchema(op.getMessage(
                                WSDLConstants.MESSAGE_LABEL_IN_VALUE));
                    }

                }
            }
        }
    }

    /**
     * walk the given schema element
     * For a successful unwrapping the element should have the
     * following structure
     * &lt; element &gt;
     * &lt; complexType &gt;
     * &lt; sequence &gt;
     * &lt; element /&gt;
     * &lt; /sequence &gt;
     * &lt; /complexType &gt;
     * &lt; /element &gt;
     */

    public void walkSchema(AxisMessage message)
            throws CodeGenerationException {
        //nothing to unwrap
        if (message.getSchemaElement() == null) {
            return;
        }


        List partNameList = new LinkedList();

        XmlSchemaElement schemaElement = message.getSchemaElement();
        XmlSchemaType schemaType = schemaElement.getSchemaType();

        String complexType = handleAllCasesOfComplexTypes(schemaType, message, partNameList);

        try {
            //set in the axis message that the unwrapping was success
            message.addParameter(getParameter(
                    Constants.UNWRAPPED_KEY,
                    Boolean.TRUE));

            // attach the opName and the parts name list into the
            // axis message by using the holder
            MessagePartInformationHolder infoHolder = new MessagePartInformationHolder();
            infoHolder.setOperationName(((AxisOperation) message.getParent()).getName());
            infoHolder.setPartsList(partNameList);

            //attach it to the parameters
            message.addParameter(
                    getParameter(Constants.UNWRAPPED_DETAILS,
                            infoHolder));
            // store the complex type name for this message
            message.addParameter(getParameter(Constants.COMPLEX_TYPE, complexType));

        } catch (AxisFault axisFault) {
            throw new CodeGenerationException(axisFault);
        }

    }

    private String handleAllCasesOfComplexTypes(XmlSchemaType schemaType, AxisMessage message, List partNameList) throws CodeGenerationException {

        // if a complex type name exits for a element then
        // we keep that complex type to support unwrapping
        String complexType = "";
        if (schemaType instanceof XmlSchemaComplexType) {
            XmlSchemaComplexType cmplxType = (XmlSchemaComplexType) schemaType;
            if ((cmplxType.getName() != null) && (cmplxType.getName().length() != 0)) {
                if ((cmplxType.getQName() != null) && (cmplxType.getQName().getNamespaceURI() != null) && (cmplxType.getQName().getNamespaceURI().length() != 0))
                {
                    complexType = URLProcessor.makePackageName(cmplxType.getQName().getNamespaceURI()) + ".";
                }
                complexType += cmplxType.getName();
            }
            if (cmplxType.getContentModel() == null) {
                if (cmplxType.getParticle() != null){
                    processXMLSchemaSequence(cmplxType.getParticle(), message, partNameList);
                }
            } else {
                // now lets handle case with extensions
                processComplexContentModel(cmplxType, message, partNameList);
            }


        } else {
            //we've no idea how to unwrap a non complexType!!!!!!
            throw new CodeGenerationException(CodegenMessages.getMessage("extension.unsupportedSchemaFormat",
                    "unknown", "complexType"));
        }
        return complexType;
    }

    private void processComplexContentModel(XmlSchemaComplexType cmplxType, AxisMessage message, List partNameList) throws CodeGenerationException {
        XmlSchemaContentModel contentModel = cmplxType.getContentModel();
        if (contentModel instanceof XmlSchemaComplexContent) {
            XmlSchemaComplexContent xmlSchemaComplexContent = (XmlSchemaComplexContent) contentModel;
            XmlSchemaContent content = xmlSchemaComplexContent.getContent();
            if (content instanceof XmlSchemaComplexContentExtension) {
                XmlSchemaComplexContentExtension schemaExtension = (XmlSchemaComplexContentExtension) content;

                // process particles inside this extension, if any
                if (schemaExtension.getParticle() != null){
                    processXMLSchemaSequence(schemaExtension.getParticle(), message, partNameList);
                }

                // now we need to get the schema of the extension type from the parent schema. For that let's first retrieve
                // the parent schema
                AxisService axisService = (AxisService) message.getParent().getParent();
                ArrayList schemasList = axisService.getSchema();

                XmlSchema parentSchema = null;

                for (int i = 0; i < schemasList.size() || parentSchema == null; i++) {
                    XmlSchema schema = (XmlSchema) schemasList.get(i);
                    if (schema.getTargetNamespace().equals(schemaExtension.getBaseTypeName().getNamespaceURI())) {
                        parentSchema = schema;
                    }
                }

                // ok now we got the parent schema. Now let's get the extension's schema type

                XmlSchemaType extensionSchemaType = parentSchema.getTypeByName(schemaExtension.getBaseTypeName());

                handleAllCasesOfComplexTypes(extensionSchemaType, message, partNameList);
            }
        }
    }

    private void processXMLSchemaSequence(XmlSchemaParticle schemaParticle, AxisMessage message, List partNameList) throws CodeGenerationException {
        if (schemaParticle instanceof XmlSchemaSequence) {
            // get the name of the operation name and namespace,
            // part name and hang them somewhere ? The ideal place
            // would be the property bag in the codegen config!
            QName opName = ((AxisOperation) message.getParent()).getName();

            XmlSchemaSequence sequence = (XmlSchemaSequence) schemaParticle;
            XmlSchemaObjectCollection items = sequence.getItems();

            // if this is an empty sequence, return
            if (items.getCount() == 0) {
                return;
            }
            for (Iterator i = items.getIterator(); i.hasNext();) {
                Object item = i.next();
                // get each and every element in the sequence and
                // traverse through them
                if (item instanceof XmlSchemaElement) {
                    //add the element name to the part name list
                    String partName = ((XmlSchemaElement) item).getName();

                    //  part names are not unique across messages. Hence
                    //  we need some way of making the part name a unique
                    //  one (due to the fact that the type mapper
                    //  is a global list of types).
                    //  The seemingly best way to do that is to
                    //  specify a namespace for the part QName reference which
                    //  is stored in the  list. This part qname is
                    //  temporary and should not be used with it's
                    //  namespace URI (which happened to be the operation name)
                    //  with _input attached to it


                    partNameList.add(
                            WSDLUtil.getPartQName(opName.getLocalPart(),
                                    WSDLConstants.INPUT_PART_QNAME_SUFFIX,
                                    partName));

                    // if the particle contains anything other than
                    // a XMLSchemaElement then we are not in a position
                    // to unwrap it
                } else if (item instanceof XmlSchemaAny) {

                    // if this is an instance of xs:any, then there is no part name for it. Using ANY_ELEMENT_FIELD_NAME
                    // for it for now

                    //we have to handle both maxoccurs 1 and maxoccurs > 1 situation
                    XmlSchemaAny xmlSchemaAny = (XmlSchemaAny) item;

                        partNameList.add(
                                WSDLUtil.getPartQName(opName.getLocalPart(),
                                        WSDLConstants.INPUT_PART_QNAME_SUFFIX,
                                        Constants.ANY_ELEMENT_FIELD_NAME));
                } else {
                    throw new CodeGenerationException(CodegenMessages.getMessage("extension.unsupportedSchemaFormat",
                            "unknown type", "Element"));
                }
            }

            //we do not know how to deal with other particles
            //such as xs:all or xs:choice. Usually occurs when
            //passed with the user built WSDL where the style
            //is document.
        } else if (schemaParticle instanceof XmlSchemaChoice) {
            throw new CodeGenerationException(CodegenMessages.getMessage("extension.unsupportedSchemaFormat",
                    "choice", "sequence"));

        } else if (schemaParticle instanceof XmlSchemaAll) {
            throw new CodeGenerationException(CodegenMessages.getMessage("extension.unsupportedSchemaFormat",
                    "all", "sequence"));
        } else {
            throw new CodeGenerationException(CodegenMessages.getMessage("extension.unsupportedSchemaFormat",
                    "unknown", "sequence"));
        }
    }

    /**
     * Generate a parametes object
     *
     * @param key
     * @param value
     */
    private Parameter getParameter(String key, Object value) {
        Parameter myParameter = new Parameter();
        myParameter.setName(key);
        myParameter.setValue(value);

        return myParameter;
    }


}
