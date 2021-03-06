
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text"/>
<!-- #################################################################################  -->
    <!-- ############################   JiBX template   ##############################  -->
  <xsl:template match="databinders[@dbtype='jibx']">
    <xsl:param name="context">unknown</xsl:param>
    
    <xsl:apply-templates select="initialize-binding"/>

    <!-- wrapped='true' uses original code, wrapped='false' unwraps method calls -->
    <xsl:variable name="wrapped"><xsl:value-of select="@wrapped"/></xsl:variable>
    <xsl:if test="$wrapped='true'">
      
      <!-- MTOM not yet supported by JiBX, but array may be needed -->
      <xsl:variable name="base64"><xsl:value-of select="base64Elements/name"/></xsl:variable>
      <xsl:if test="$base64">
        private static javax.xml.namespace.QName[] qNameArray = {
        <xsl:for-each select="base64Elements/name">
          <xsl:if test="position()">1">,</xsl:if>new javax.xml.namespace.QName("<xsl:value-of select="@ns-url"/>","<xsl:value-of select="@localName"/>")
        </xsl:for-each>
        };
      </xsl:if>

        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
            return factory.getDefaultEnvelope();
        }
      
    </xsl:if>
    
    <xsl:if test="$wrapped='false'">
      <xsl:choose>
        <xsl:when test="$context='message-receiver'">
          <xsl:apply-templates mode="message-receiver" select="dbmethod"/>
        </xsl:when>
        <xsl:when test="$context='interface-implementation'">
          <xsl:variable name="isSync"><xsl:value-of select="/class/@isSync"/></xsl:variable>
          <xsl:if test="$isSync='1'">
            <xsl:apply-templates mode="interface-implementation" select="dbmethod"><xsl:with-param name="sync">true</xsl:with-param></xsl:apply-templates>
          </xsl:if>
          <xsl:variable name="isAsync"><xsl:value-of select="/class/@isAsync"/></xsl:variable>
          <xsl:if test="$isAsync='1'">
            <xsl:apply-templates mode="interface-implementation" select="dbmethod"><xsl:with-param name="sync">false</xsl:with-param></xsl:apply-templates>
          </xsl:if>
        </xsl:when>
      </xsl:choose>
    </xsl:if>
    
    <xsl:choose>
      <xsl:when test="$context='message-receiver'">
        <xsl:apply-templates select="object-output"/>
        <xsl:apply-templates select="object-fault"/>
      </xsl:when>
      <xsl:when test="$context='interface-implementation'">
        <xsl:apply-templates select="object-input"/>
        <xsl:call-template name="stub-utility-methods"/>
      </xsl:when>
    </xsl:choose>
    
  </xsl:template>
  
  
  <!--
  toOM AND toEnvelope METHOD GENERATION
  -->
  <xsl:template match="object-input|object-output">

        private org.apache.axiom.om.OMElement toOM(<xsl:value-of select="@type"/> param, org.apache.axiom.soap.SOAPFactory factory, boolean optimizeContent) {
            <xsl:call-template name="toOM-method-body"/>
        }
        <xsl:call-template name="toEnvelope-method"/>
  </xsl:template>
  
  <xsl:template match="object-fault">

        private org.apache.axiom.om.OMElement toOM(<xsl:value-of select="@type"/> param, boolean optimizeContent) {
            org.apache.axiom.om.OMFactory factory = org.apache.axiom.om.OMAbstractFactory.getOMFactory();
            <xsl:call-template name="toOM-method-body"/>
        }
  </xsl:template>
  
  <xsl:template name="toOM-method-body">
            if (param instanceof org.jibx.runtime.IMarshallable){
                if (bindingFactory == null) {
                    throw new RuntimeException(bindingErrorMessage);
                }
                org.jibx.runtime.IMarshallable marshallable =
                    (org.jibx.runtime.IMarshallable)param;
                int index = marshallable.JiBX_getIndex();
                org.apache.axis2.jibx.JiBXDataSource source =
                    new org.apache.axis2.jibx.JiBXDataSource(marshallable, bindingFactory);
                org.apache.axiom.om.OMNamespace namespace = factory.createOMNamespace(bindingFactory.getElementNamespaces()[index], null);
                return factory.createOMElement(source, bindingFactory.getElementNames()[index], namespace);
            } else {
                throw new RuntimeException("No JiBX &lt;mapping> defined for class <xsl:value-of select="@type"/>");
            }
  </xsl:template>
  
  <xsl:template name="toEnvelope-method">
        
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, <xsl:value-of select="@type"/> param, boolean optimizeContent) {
            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
            if (param != null){
                envelope.getBody().addChild(toOM(param, factory, optimizeContent));
            }
            return envelope;
        }

  </xsl:template>
  
  
  <!--
  MESSAGE RECEIVER METHOD GENERATION
  -->
  
  <!-- Invoked by main template to handle unwrapped method generation for message receiver -->
  <xsl:template match="dbmethod" mode="message-receiver">
    <xsl:variable name="method-name" select="@method-name"/>
      public org.apache.axiom.soap.SOAPEnvelope <xsl:value-of select="@receiver-name"/>(org.apache.axiom.om.OMElement element, <xsl:value-of select="/*/@skeletonInterfaceName"/> skel, org.apache.axiom.soap.SOAPFactory factory) throws org.apache.axis2.AxisFault
      <xsl:for-each select="/interface/method[@name=$method-name]/fault/param">, <xsl:value-of select="@name"/></xsl:for-each>
      {
          org.apache.axiom.soap.SOAPEnvelope envelope = null;
          try {
              org.jibx.runtime.impl.UnmarshallingContext uctx = getNewUnmarshalContext(element);
              uctx.next();
              int index;
    <xsl:apply-templates select="in-wrapper/parameter-element" mode="message-receiver"/>
    
    <!-- actual call handling depends on type of returned result -->
    <xsl:choose>
    
      <!-- returning an array of values -->
      <xsl:when test="out-wrapper/@empty='false' and out-wrapper/return-element/@array='true'">
              envelope = factory.getDefaultEnvelope();
              org.apache.axiom.om.OMElement wrapper = factory.createOMElement("<xsl:value-of select='out-wrapper/@name'/>", "<xsl:value-of select='out-wrapper/@ns'/>", "");
              envelope.getBody().addChild(wrapper);
              <xsl:value-of select="out-wrapper/return-element/@java-type"/>[] results = skel.<xsl:call-template name="call-arg-list"/>;
              if (results == null || results.length == 0) {
        <xsl:choose>
          <xsl:when test="out-wrapper/return-element/@optional='true'"/>
          <xsl:otherwise>
                  throw new org.apache.axis2.AxisFault("Missing required result");
          </xsl:otherwise>
        </xsl:choose>
              } else {
                  org.apache.axiom.om.OMNamespace appns = factory.createOMNamespace("<xsl:value-of select='out-wrapper/return-element/@ns'/>", "app");
                  wrapper.declareNamespace(appns);
        <xsl:choose>
          <xsl:when test="out-wrapper/return-element/@form='complex'">
                  for (int i = 0; i &lt; results.length; i++) {
                      <xsl:value-of select="out-wrapper/return-element/@java-type"/> result = results[i];
                      if (result == null) {
            <xsl:choose>
              <xsl:when test="out-wrapper/return-element/@nillable='true'">
                          org.apache.axiom.om.OMElement child = factory.createOMElement("<xsl:value-of select='out-wrapper/return-element/@name'/>", appns);
                          org.apache.axiom.om.OMNamespace xsins = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
                          child.declareNamespace(xsins);
                          child.addAttribute("nil", "true", xsins);
                          wrapper.addChild(child);
              </xsl:when>
              <xsl:otherwise>
                          throw new org.apache.axis2.AxisFault("Null value in result array not allowed unless element has nillable='true'");
              </xsl:otherwise>
            </xsl:choose>
                      } else {
                          org.apache.axiom.om.OMDataSource src = new org.apache.axis2.jibx.JiBXDataSource(result, _type_index<xsl:value-of select="out-wrapper/return-element/@type-index"/>, "<xsl:value-of select='out-wrapper/return-element/@name'/>", "<xsl:value-of select='out-wrapper/return-element/@ns'/>", bindingFactory);
                          org.apache.axiom.om.OMElement child = factory.createOMElement(src, "<xsl:value-of select='out-wrapper/return-element/@name'/>", appns);
                          wrapper.addChild(child);
                      }
                  }
          </xsl:when>
          <xsl:otherwise>
                  for (int i = 0; i &lt; results.length; i++) {
                      <xsl:value-of select="out-wrapper/return-element/@java-type"/> result = results[i];
                      org.apache.axiom.om.OMElement child = factory.createOMElement("<xsl:value-of select='out-wrapper/return-element/@name'/>", appns);
            <xsl:choose>
              <xsl:when test="out-wrapper/return-element/@serializer=''">
                      child.setText(result.toString());
              </xsl:when>
              <xsl:otherwise>
                      child.setText(<xsl:value-of select="out-wrapper/return-element/@serializer"/>(result));
              </xsl:otherwise>
            </xsl:choose>
                      wrapper.addChild(child);
                  }
          </xsl:otherwise>
        </xsl:choose>
              }
      </xsl:when>
    
      <!-- returning a single value -->
      <xsl:when test="out-wrapper/@empty='false'">
              envelope = factory.getDefaultEnvelope();
              org.apache.axiom.om.OMElement wrapper = factory.createOMElement("<xsl:value-of select='out-wrapper/@name'/>", "<xsl:value-of select='out-wrapper/@ns'/>", "");
              envelope.getBody().addChild(wrapper);
              <xsl:value-of select="out-wrapper/return-element/@java-type"/> result = skel.<xsl:call-template name="call-arg-list"/>;
              org.apache.axiom.om.OMNamespace appns = factory.createOMNamespace("<xsl:value-of select='out-wrapper/return-element/@ns'/>", "app");
              wrapper.declareNamespace(appns);
        <xsl:choose>
          <xsl:when test="out-wrapper/return-element/@form='complex'">
              if (result == null) {
            <xsl:choose>
              <xsl:when test="out-wrapper/return-element/@optional='true'"/>
              <xsl:when test="out-wrapper/return-element/@nillable='true'">
                  org.apache.axiom.om.OMElement child = factory.createOMElement("<xsl:value-of select='out-wrapper/return-element/@name'/>", appns);
                  org.apache.axiom.om.OMNamespace xsins = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
                  child.declareNamespace(xsins);
                  child.addAttribute("nil", "true", xsins);
                  wrapper.addChild(child);
              </xsl:when>
              <xsl:otherwise>
                  throw new org.apache.axis2.AxisFault("Missing required result");
              </xsl:otherwise>
            </xsl:choose>
              } else {
                  org.apache.axiom.om.OMDataSource src = new org.apache.axis2.jibx.JiBXDataSource(result, _type_index<xsl:value-of select="out-wrapper/return-element/@type-index"/>, "<xsl:value-of select='out-wrapper/return-element/@name'/>", "<xsl:value-of select='out-wrapper/return-element/@ns'/>", bindingFactory);
                  org.apache.axiom.om.OMElement child = factory.createOMElement(src, "<xsl:value-of select='out-wrapper/return-element/@name'/>", appns);
                  wrapper.addChild(child);
              }
          </xsl:when>
          <xsl:otherwise>
              org.apache.axiom.om.OMElement child = factory.createOMElement("<xsl:value-of select='out-wrapper/return-element/@name'/>", appns);
            <xsl:choose>
              <xsl:when test="out-wrapper/return-element/@object='true'">
              if (result == null) {
                  org.apache.axiom.om.OMNamespace xsins = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
                  child.declareNamespace(xsins);
                  child.addAttribute("nil", "true", xsins);
              } else {
                <xsl:call-template name="set-result-text"/>
              }
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="set-result-text"/>
              </xsl:otherwise>
            </xsl:choose>
              wrapper.addChild(child);
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
    
      <!-- not returning anything -->
      <xsl:otherwise>
              skel.<xsl:call-template name="call-arg-list"/>;
        <xsl:if test="count(out-wrapper)&gt;0">
              envelope = factory.getDefaultEnvelope();
              envelope.getBody().addChild(factory.createOMElement("<xsl:value-of select='out-wrapper/@name'/>", "<xsl:value-of select='out-wrapper/@ns'/>", ""));
        </xsl:if>
      </xsl:otherwise>
      
    </xsl:choose>
          } catch (org.jibx.runtime.JiBXException e) {
              throw new org.apache.axis2.AxisFault(e);
          }
          return envelope;
      }
  </xsl:template>
  
  <xsl:template name="set-result-text">
    <xsl:choose>
      <xsl:when test="out-wrapper/return-element/@serializer=''">
              child.setText(result.toString());
      </xsl:when>
      <xsl:otherwise>
              child.setText(<xsl:value-of select="out-wrapper/return-element/@serializer"/>(result<xsl:if test="out-wrapper/return-element/@wrapped-primitive='true'">.<xsl:value-of select="out-wrapper/return-element/@value-method"/>()</xsl:if>));
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Generate argument list for message receiver call to actual implementation method. -->
  <xsl:template name="call-arg-list">
    <xsl:value-of select="@method-name"/>(
    <xsl:for-each select="in-wrapper/parameter-element">
      <xsl:if test="position()&gt;1">, </xsl:if><xsl:value-of select="@java-name"/>
    </xsl:for-each>
    )
  </xsl:template>
  
  <!-- Generate code for a particular parameter element in a message receiver method -->
  <xsl:template match="parameter-element" mode="message-receiver">
    <xsl:choose>
      <xsl:when test="@array='true'">
        <xsl:call-template name="unmarshal-array"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="unmarshal-value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <!-- 
  CLIENT STUB UNWRAPPED METHOD GENERATION
  -->
  
  <!-- Invoked by main template to handle unwrapped method generation for synchronous client stub -->
  <xsl:template match="dbmethod" mode="interface-implementation">
    <xsl:param name="sync">error</xsl:param>
    <xsl:variable name="interfaceName"><xsl:value-of select="/class/@interfaceName"/></xsl:variable>
    <xsl:variable name="package"><xsl:value-of select="/class/@package"/></xsl:variable>
    <xsl:variable name="return-base-type"><xsl:value-of select="out-wrapper/return-element/@java-type"/></xsl:variable>
    <xsl:variable name="return-full-type"><xsl:value-of select="$return-base-type"/><xsl:if test="out-wrapper/return-element/@array='true'">[]</xsl:if></xsl:variable>
    <xsl:variable name="method-name"><xsl:value-of select="@method-name"/></xsl:variable>
    
        /**
    <xsl:if test="$sync='true'">
         * Auto generated synchronous call method
         * 
         * @see <xsl:value-of select="$package"/>.<xsl:value-of select="$interfaceName"/>#<xsl:value-of select="@method-name"/>
        <xsl:for-each select="in-wrapper/parameter-element">
         * @param <xsl:value-of select="@java-name"/></xsl:for-each>
         */
        public <xsl:choose><xsl:when test="string-length(normalize-space($return-full-type)) &gt; 0"><xsl:value-of select="$return-full-type"/></xsl:when><xsl:otherwise>void</xsl:otherwise></xsl:choose><xsl:text> </xsl:text><xsl:value-of select="@method-name"/>(
    </xsl:if>
    <xsl:if test="$sync='false'">
         * Auto generated asynchronous call method
         * 
         * @see <xsl:value-of select="$package"/>.<xsl:value-of select="$interfaceName"/>#start<xsl:value-of select="@method-name"/>
        <xsl:for-each select="in-wrapper/parameter-element">
         * @param <xsl:value-of select="@java-name"/></xsl:for-each>
    <xsl:if test="$sync='true'">
      <xsl:for-each select="/class/method[@name=$method-name]/fault/param">
         * @throws <xsl:value-of select="@name"/>
      </xsl:for-each>
    </xsl:if>
         */
        public void start<xsl:value-of select="@method-name"/>(
    </xsl:if>
        <xsl:for-each select="in-wrapper/parameter-element">
          <xsl:if test="position()&gt;1">, </xsl:if><xsl:value-of select="@java-type"/><xsl:if test="@array='true'">[]</xsl:if><xsl:text> </xsl:text><xsl:value-of select="@java-name"/>
        </xsl:for-each>
    <xsl:if test="$sync='false'">
        <xsl:if test="in-wrapper/@empty='false'">, </xsl:if>final <xsl:value-of select="/class/@callbackname"/> _callback
    </xsl:if>
            ) throws java.rmi.RemoteException
    <xsl:if test="$sync='true'">
      <!--add the faults-->
      <xsl:for-each select="/class/method[@name=$method-name]/fault/param">, <xsl:value-of select="@name"/></xsl:for-each>
    </xsl:if>
            {
    <!-- Simple parameter values (those with serializers) can be handled by
      direct conversion to elements. Complex parameter values need to use data
      sources. This code handles both types. -->
            try {
                int _opIndex = <xsl:apply-templates mode="get-index" select="/class/method[@name=$method-name]"></xsl:apply-templates>;
                javax.xml.namespace.QName opname = _operations[_opIndex].getName();
                org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(opname);
                _operationClient.getOptions().setAction("<xsl:apply-templates mode="get-action" select="/class/method[@name=$method-name]"></xsl:apply-templates>");
                _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
    
                // create SOAP envelope with the payload
                org.apache.axiom.soap.SOAPEnvelope env = createEnvelope(_operationClient.getOptions());
                org.apache.axiom.soap.SOAPFactory factory = getFactory(_operationClient.getOptions().getSoapVersionURI());
                org.apache.axiom.om.OMElement wrapper = factory.createOMElement("<xsl:value-of select='in-wrapper/@name'/>", "<xsl:value-of select='in-wrapper/@ns'/>", "");
                env.getBody().addChild(wrapper);
                org.apache.axiom.om.OMElement child;
    <xsl:apply-templates select="in-wrapper/parameter-element" mode="interface-implementation"/>
    
                // add SOAP headers
                _serviceClient.addHeadersToEnvelope(env);
                
                // create message context with that envelope
                org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
                _messageContext.setEnvelope(env);
    
                // add the message context to the operation client
                _operationClient.addMessageContext(_messageContext);
    
    <xsl:if test="$sync='true'">
               // execute the operation client
                _operationClient.execute(true);
                
      <xsl:if test="out-wrapper/@empty='false'">
                org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
                    .getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
                org.apache.axiom.om.OMElement _response = _returnMessageContext.getEnvelope().getBody().getFirstElement();
                if (_response != null &amp;&amp; "<xsl:value-of select='out-wrapper/@name'/>".equals(_response.getLocalName()) &amp;&amp;
                    "<xsl:value-of select='out-wrapper/@ns'/>".equals(_response.getNamespace().getNamespaceURI())) {
                    org.jibx.runtime.impl.UnmarshallingContext uctx = getNewUnmarshalContext(_response);
                    uctx.parsePastStartTag("<xsl:value-of select='out-wrapper/@ns'/>", "<xsl:value-of select='out-wrapper/@name'/>");
                    int index;
        <xsl:apply-templates select="out-wrapper/return-element" mode="interface-implementation"/>
                    return <xsl:value-of select="out-wrapper/return-element/@java-name"/>;
                } else {
                    throw new org.apache.axis2.AxisFault("Missing expected return wrapper element {<xsl:value-of select='out-wrapper/@ns'/>}<xsl:value-of select='out-wrapper/@name'/>");
                }
      </xsl:if>
            } catch (Exception e) {
                Exception outex = convertException(e);
      <xsl:for-each select="/class/method[@name=$method-name]/fault/param">
                if (outex instanceof <xsl:value-of select="@name"/>) {
                    throw (<xsl:value-of select="@name"/>)outex;
                }
      </xsl:for-each>
                // should never happen, but just in case
                throw new RuntimeException("Unexpected exception type: " +
                    outex.getClass().getName(), outex);
            }
        }
    </xsl:if>
    <xsl:if test="$sync='false'">
                _operationClient.setCallback(new org.apache.axis2.client.async.Callback() {
                    public void onComplete(org.apache.axis2.client.async.AsyncResult async) {
                        try {
                            org.apache.axiom.om.OMElement result = async.getResponseEnvelope().getBody().getFirstElement();
                            if (result != null &amp;&amp; "<xsl:value-of select='out-wrapper/@name'/>".equals(result.getLocalName()) &amp;&amp;
                                "<xsl:value-of select='out-wrapper/@ns'/>".equals(result.getNamespace().getNamespaceURI())) {
                                org.jibx.runtime.impl.UnmarshallingContext uctx = getNewUnmarshalContext(result);
                                uctx.parsePastStartTag("<xsl:value-of select='out-wrapper/@ns'/>", "<xsl:value-of select='out-wrapper/@name'/>");
                                int index;
      <xsl:apply-templates select="out-wrapper/return-element" mode="interface-implementation"/>
                                _callback.receiveResult<xsl:value-of select="@method-name"/>(<xsl:value-of select="out-wrapper/return-element/@java-name"/>);
                            } else {
                                throw new org.apache.axis2.AxisFault("Missing expected result wrapper element {<xsl:value-of select='out-wrapper/@ns'/>}<xsl:value-of select='out-wrapper/@name'/>");
                            }
                        } catch (Exception e) {
                            onError(e);
                        }
                    }

                    public void onError(Exception e) {
                        _callback.receiveError<xsl:value-of select="@method-name"/>(e);
                    }
                });
                        
                org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
                if ( _operations[_opIndex].getMessageReceiver() == null &amp;&amp; _operationClient.getOptions().isUseSeparateListener()) {
                    _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
                    _operations[_opIndex].setMessageReceiver(_callbackReceiver);
                }

                // execute the operation client
                _operationClient.execute(false);
                
            } catch (Exception e) {
                Exception outex = convertException(e);
                throw new RuntimeException("Unexpected exception type: " +
                    outex.getClass().getName(), outex);
            }
        }
    </xsl:if>
  </xsl:template>
  
  <!-- Invoked to get the operation index number for a method. -->
  <xsl:template match="method" mode="get-index"><xsl:value-of select="count(preceding-sibling::method)"/></xsl:template>
  
  <!-- Invoked to get the operation action for a method. -->
  <xsl:template match="method" mode="get-action"><xsl:value-of select="@soapaction"/></xsl:template>
  
  <!-- Generate code for a particular parameter element in a client stub method -->
  <xsl:template match="parameter-element" mode="interface-implementation">
    <xsl:choose>
      <xsl:when test="@array='true'">
        <xsl:call-template name="marshal-array"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="marshal-value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Marshal an array to a repeated element -->
  <xsl:template name="marshal-array">
    if (<xsl:value-of select="@java-name"/> == null || <xsl:value-of select="@java-name"/>.length == 0) {
    <xsl:choose>
      <xsl:when test="@optional='true'"></xsl:when>
      <xsl:when test="@nillable='true'">
        child = factory.createOMElement("<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", "");
        org.apache.axiom.om.OMNamespace xsins = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        child.declareNamespace(xsins);
        child.addAttribute("nil", "true", xsins);
        wrapper.addChild(child);
      </xsl:when>
      <xsl:otherwise>
        throw new org.apache.axis2.AxisFault("Missing required value <xsl:value-of select='@java-name'/>");
      </xsl:otherwise>
    </xsl:choose>
    } else {
        for (int i = 0; i &lt; <xsl:value-of select="@java-name"/>.length; i++) {
            <xsl:value-of select="@java-type"/> _item = <xsl:value-of select="@java-name"/>[i];
    <xsl:choose>
      <xsl:when test="@object='true' and @nillable='true'">
            if (_item == null) {
                child = factory.createOMElement("<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", "");
                org.apache.axiom.om.OMNamespace xsins = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
                child.declareNamespace(xsins);
                child.addAttribute("nil", "true", xsins);
                wrapper.addChild(child);
            } else {
        <xsl:call-template name="serialize-value-to-child"/>
            }
      </xsl:when>
      <xsl:when test="@object='true'">
            if (_item == null) {
                throw new org.apache.axis2.AxisFault("Null value in array <xsl:value-of select='@java-name'/>");
            } else {
        <xsl:call-template name="serialize-value-to-child"/>
            }
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="serialize-value-to-child"/>
      </xsl:otherwise>
    </xsl:choose>
        }
    }
  </xsl:template>
  
  <!-- Marshal a simple value to a non-repeated element -->
  <xsl:template name="marshal-value">
    <xsl:choose>
      <xsl:when test="@object='true' and @nillable='true'">
        if (<xsl:value-of select="@java-name"/> == null) {
            child = factory.createOMElement("<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", "");
            org.apache.axiom.om.OMNamespace xsins = factory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            child.declareNamespace(xsins);
            child.addAttribute("nil", "true", xsins);
            wrapper.addChild(child);
        } else {
        <xsl:call-template name="serialize-value-to-child"/>
        }
      </xsl:when>
      <xsl:when test="@object='true'">
        if (<xsl:value-of select="@java-name"/> == null) {
            throw new org.apache.axis2.AxisFault("Null value for <xsl:value-of select='@java-name'/>");
        } else {
        <xsl:call-template name="serialize-value-to-child"/>
        }
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="serialize-value-to-child"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Convert the current value to an element. -->
  <xsl:template name="serialize-value-to-child">
    <xsl:choose>
      <xsl:when test="@java-type='String' and @serializer=''">
        child = factory.createOMElement("<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", "");
        child.setText(<xsl:call-template name="parameter-or-array-item"/>);
      </xsl:when>
      <xsl:when test="@form='simple' and @serializer=''">
        child = factory.createOMElement("<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", "");
        child.setText(<xsl:call-template name="parameter-or-array-item"/>.toString());
      </xsl:when>
      <xsl:when test="@form='simple'">
        child = factory.createOMElement("<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", "");
        child.setText(<xsl:value-of select="@serializer"/>(<xsl:call-template name="parameter-or-array-item"/>));
      </xsl:when>
      <xsl:when test="@form='complex'">
        org.apache.axiom.om.OMDataSource src = new org.apache.axis2.jibx.JiBXDataSource(<xsl:call-template name="parameter-or-array-item"/>, _type_index<xsl:value-of select="@type-index"/>, "<xsl:value-of select='@name'/>", "<xsl:value-of select='@ns'/>", bindingFactory);
        org.apache.axiom.om.OMNamespace appns = factory.createOMNamespace("<xsl:value-of select='@ns'/>", "");
        child = factory.createOMElement(src, "<xsl:value-of select='@name'/>", appns);
      </xsl:when>
    </xsl:choose>
        wrapper.addChild(child);
  </xsl:template>
  
  <!-- Reference to parameter or array item value, as appropriate -->
  <xsl:template name="parameter-or-array-item"><xsl:choose><xsl:when test="@array='true'">_item</xsl:when><xsl:otherwise><xsl:value-of select='@java-name'/></xsl:otherwise></xsl:choose><xsl:if test="@wrapped-primitive='true'">.<xsl:value-of select="@value-method"/>()</xsl:if></xsl:template>
  
  <!-- Generate code for the result in a client stub method -->
  <xsl:template match="return-element" mode="interface-implementation">
    <xsl:choose>
      <xsl:when test="@array='true'">
        <xsl:call-template name="unmarshal-array"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="unmarshal-value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <!--
  CLIENT STUB SHARED METHOD GENERATION
  -->
  <!-- Called by main template to create utility methods -->
  <xsl:template name="stub-utility-methods">
        
        private Exception convertException(Exception ex) throws java.rmi.RemoteException {
            if (ex instanceof org.apache.axis2.AxisFault) {
                org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault)ex;
                org.apache.axiom.om.OMElement faultElt = f.getDetail();
                if (faultElt != null) {
                    if (faultExeptionNameMap.containsKey(faultElt.getQName())) {
                        try {
                            
                            // first create the actual exception
                            String exceptionClassName = (String)faultExeptionClassNameMap.get(faultElt.getQName());
                            Class exceptionClass = Class.forName(exceptionClassName);
                            Exception e = (Exception)exceptionClass.newInstance();
                            
                            // build the message object from the details
                            String messageClassName = (String)faultMessageMap.get(faultElt.getQName());
                            Class messageClass = Class.forName(messageClassName);
                            Object messageObject = fromOM(faultElt, messageClass, null);
                            java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                new Class[] { messageClass });
                            m.invoke(e, new Object[] { messageObject });
                            return e;
                            
                        } catch (ClassCastException e) {
                            // we cannot intantiate the class - throw the original
                            // Axis fault
                            throw f;
                        } catch (ClassNotFoundException e) {
                            // we cannot intantiate the class - throw the original
                            // Axis fault
                            throw f;
                        } catch (NoSuchMethodException e) {
                            // we cannot intantiate the class - throw the original
                            // Axis fault
                            throw f;
                        } catch (java.lang.reflect.InvocationTargetException e) {
                            // we cannot intantiate the class - throw the original
                            // Axis fault
                            throw f;
                        } catch (IllegalAccessException e) {
                            // we cannot intantiate the class - throw the original
                            // Axis fault
                            throw f;
                        } catch (InstantiationException e) {
                            // we cannot intantiate the class - throw the original
                            // Axis fault
                            throw f;
                        }
                    } else {
                        throw f;
                    }
                } else {
                    throw f;
                }
                
            } else if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            } else if (ex instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException)ex;
            } else {
                throw new org.apache.axis2.AxisFault(ex);
            }
        }
    
  </xsl:template>
  
  
  <!--
  STATIC CODE GENERATION
  -->
  
  <!-- Called by main template to handle static binding data and methods. -->
  <xsl:template match="initialize-binding">
      private static final org.jibx.runtime.IBindingFactory bindingFactory;
      private static final String bindingErrorMessage;
    <xsl:apply-templates mode="generate-index-fields" select="abstract-type"/>
        static {
            org.jibx.runtime.IBindingFactory factory = null;
            String message = null;
            try {
    <xsl:choose>
      <xsl:when test="@bound-class=''">
                factory = new org.apache.axis2.jibx.NullBindingFactory();
      </xsl:when>
      <xsl:otherwise>
                factory = org.jibx.runtime.BindingDirectory.getFactory(<xsl:value-of select="@bound-class"/>.class);
      </xsl:otherwise>
    </xsl:choose>
                message = null;
            } catch (Exception e) { message = e.getMessage(); }
            bindingFactory = factory;
            bindingErrorMessage = message;
    <xsl:apply-templates mode="set-index-fields" select="abstract-type"/>
        }
        
        private static org.jibx.runtime.impl.UnmarshallingContext getNewUnmarshalContext(org.apache.axiom.om.OMElement param)
            throws org.jibx.runtime.JiBXException {
            if (bindingFactory == null) {
                throw new RuntimeException(bindingErrorMessage);
            }
            org.jibx.runtime.impl.UnmarshallingContext ctx =
                (org.jibx.runtime.impl.UnmarshallingContext)bindingFactory.createUnmarshallingContext();
            org.jibx.runtime.IXMLReader reader = new org.jibx.runtime.impl.StAXReaderWrapper(param.getXMLStreamReaderWithoutCaching(), "SOAP-message", true);
            ctx.setDocument(reader);
            ctx.toTag();
            return ctx;
        }
        
    <!-- shouldn't be needed when no actual binding, but called by fault conversion code so must be left in for now -->
        private static Object fromOM(org.apache.axiom.om.OMElement param, Class type,
            java.util.Map extraNamespaces) {
            try {
                org.jibx.runtime.impl.UnmarshallingContext ctx = getNewUnmarshalContext(param);
                return ctx.unmarshalElement(type);
            } catch (Exception e) {
                 throw new RuntimeException(e);
            }
        }
  </xsl:template>
  
  <!-- Called by "initialize-binding" template to generate mapped class index fields. -->
  <xsl:template match="abstract-type" mode="generate-index-fields">
          private static final int _type_index<xsl:value-of select="@type-index"/>;
  </xsl:template>
    
  <!-- Called by "initialize-binding" template to initialize mapped class index fields. -->
  <xsl:template match="abstract-type" mode="set-index-fields">
         _type_index<xsl:value-of select="@type-index"/> = (bindingFactory == null) ?
            -1 : bindingFactory.getTypeIndex("{<xsl:value-of select="@ns"/>}:<xsl:value-of select="@name"/>");
  </xsl:template>
  
  
  <!--
  SHARED TEMPLATES
  -->
  
  <!-- Unmarshal a repeated element into an array -->
  <xsl:template name="unmarshal-array">
    <xsl:value-of select="@java-type"/>[] <xsl:value-of select="@java-name"/> = new <xsl:value-of select="@java-type"/>[4];
      index = 0;
      while (uctx.isAt("<xsl:value-of select="@ns"/>", "<xsl:value-of select="@name"/>")) {
          if (index >= <xsl:value-of select="@java-name"/>.length) {
              <xsl:value-of select="@java-name"/> = (<xsl:value-of select="@java-type"/>[])org.jibx.runtime.Utility.growArray(<xsl:value-of select="@java-name"/>);
          }
    <xsl:if test="@nillable='true'">
          if (uctx.attributeBoolean("http://www.w3.org/2001/XMLSchema-instance", "nil", false)) {
              uctx.skipElement();
          } else {
    </xsl:if>
    <xsl:value-of select="@java-name"/>[index++] = (<xsl:value-of select="@java-type"/>)<xsl:call-template name="deserialize-element-value"/>;
    <xsl:if test="@form='complex'">
              uctx.parsePastCurrentEndTag("<xsl:value-of select='@ns'/>", "<xsl:value-of select='@name'/>");
    </xsl:if>
    <xsl:if test="@nillable='true'">
          }
    </xsl:if>
      }
      <xsl:value-of select="@java-name"/> = (<xsl:value-of select="@java-type"/>[])org.jibx.runtime.Utility.resizeArray(index, <xsl:value-of select="@java-name"/>);
    <xsl:if test="@optional!='true'">
      if (index == 0) {
          throw new org.apache.axis2.AxisFault("Missing required element {<xsl:value-of select='@ns'/>}<xsl:value-of select='@name'/>");
      }
    </xsl:if>
  </xsl:template>
  
  <!-- Unmarshal a non-repeated element into an simple value -->
  <xsl:template name="unmarshal-value">
    <xsl:value-of select="@java-type"/><xsl:text> </xsl:text><xsl:value-of select="@java-name"/> = <xsl:choose><xsl:when test="boolean(@default)"><xsl:value-of select="@default"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose>;
          if (uctx.isAt("<xsl:value-of select="@ns"/>", "<xsl:value-of select="@name"/>")) {
    <xsl:if test="@nillable='true'">
              if (uctx.attributeBoolean("http://www.w3.org/2001/XMLSchema-instance", "nil", false)) {
                  uctx.skipElement();
              } else {
    </xsl:if>
    <xsl:value-of select="@java-name"/> = (<xsl:value-of select="@java-type"/>)<xsl:call-template name="deserialize-element-value"/>;
    <xsl:if test="@form='complex'">
              uctx.parsePastCurrentEndTag("<xsl:value-of select='@ns'/>", "<xsl:value-of select='@name'/>");
    </xsl:if>
    <xsl:if test="@nillable='true'">
              }
    </xsl:if>
    <xsl:choose>
      <xsl:when test="optional">
          }
      </xsl:when>
      <xsl:otherwise>
          } else {
              throw new org.apache.axis2.AxisFault("Missing required element {<xsl:value-of select='@ns'/>}<xsl:value-of select='@name'/>");
          }
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Convert the current element into a value. -->
  <xsl:template name="deserialize-element-value">
    <xsl:choose>
      <xsl:when test="@java-type='String' and @deserializer=''">
        uctx.parseElementText("<xsl:value-of select="@ns"/>", "<xsl:value-of select="@name"/>")
      </xsl:when>
      <xsl:when test="@form='simple' and @deserializer=''">
        new <xsl:value-of select="@java-type"/>(uctx.parseElementText("<xsl:value-of select="@ns"/>", "<xsl:value-of select="@name"/>"))
      </xsl:when>
      <xsl:when test="@form='simple' and @wrapped-primitive='true'">
        new <xsl:value-of select="@java-type"/>(<xsl:value-of select="@deserializer"/>(uctx.parseElementText("<xsl:value-of select="@ns"/>", "<xsl:value-of select="@name"/>")))
      </xsl:when>
      <xsl:when test="@form='simple'">
        <xsl:value-of select="@deserializer"/>(uctx.parseElementText("<xsl:value-of select="@ns"/>", "<xsl:value-of select="@name"/>"))
      </xsl:when>
      <xsl:when test="@form='complex'">
        uctx.getUnmarshaller(_type_index<xsl:value-of select="@type-index"/>).unmarshal(new <xsl:value-of select="@java-type"/>(), uctx)
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>