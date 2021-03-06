<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <!-- Incldue the test object creation template  -->
    <xsl:include href="testObject"/>

    <xsl:template match="/class">
    <xsl:variable name="interfaceName"><xsl:value-of select="@interfaceName"/></xsl:variable>
    <xsl:variable name="package"><xsl:value-of select="@package"/></xsl:variable>
    <xsl:variable name="callbackname"><xsl:value-of select="@callbackname"/></xsl:variable>
    <xsl:variable name="stubname"><xsl:value-of select="@stubname"/></xsl:variable>
    <xsl:variable name="isSync"><xsl:value-of select="@isSync"/></xsl:variable>
    <xsl:variable name="isAsync"><xsl:value-of select="@isAsync"/></xsl:variable>

    /**
     * <xsl:value-of select="@name"/>.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: #axisVersion# #today#
     */
    package <xsl:value-of select="$package"/>;

    /*
     *  <xsl:value-of select="@name"/> Junit test case
    */

    public class <xsl:value-of select="@name"/> extends junit.framework.TestCase{

     <xsl:for-each select="method">
         <xsl:if test="@mep='12'">
          <xsl:variable name="outputtype"><xsl:value-of select="output/param/@type"></xsl:value-of></xsl:variable>
          <xsl:if test="$isSync='1'">
        /**
         * Auto generated test method
         */
        public  void test<xsl:value-of select="@name"/>() throws java.lang.Exception{

        <xsl:value-of select="$package"/>.<xsl:value-of select="$stubname"/> stub =
                    new <xsl:value-of select="$package"/>.<xsl:value-of select="$stubname"/>();//the default implementation should point to the right endpoint

           <xsl:choose>
             <xsl:when test="count(input/param)>0">
                <xsl:for-each select="input/param[@type!='']">
                    <xsl:value-of select="@type"/><xsl:text> </xsl:text><xsl:value-of select="@name"/><xsl:text>=
                                                        </xsl:text>(<xsl:value-of select="@type"/>)getTestObject(<xsl:value-of select="@type"/>.class);
                    // todo Fill in the <xsl:value-of select="@name"/> here
                </xsl:for-each>
                <xsl:choose>
                    <xsl:when test="$outputtype=''">
                    <!-- for now think there is only one input element -->
                    //There is no output to be tested!
                    stub.<xsl:value-of select="@name"/>(
                        <xsl:for-each select="input/param[@type!='']">
                             <xsl:variable name="opname" select="@opname"/>
                             <xsl:variable name="paramname" select="@name"/>
                             <xsl:variable name="paramcount" select="count(param[@type!='' and @opname=$opname])"/>

                             <xsl:choose>
                                 <xsl:when test="$paramcount > 0">
                                      <xsl:for-each select="param[@type!='' and @opname=$opname]">
                                            <xsl:if test="position()>1">,</xsl:if>get<xsl:value-of select="@partname"/>(<xsl:value-of select="$paramname"/>)
                                      </xsl:for-each>
                                 </xsl:when>
                                 <xsl:otherwise>
                                     <xsl:if test="@type!=''"><xsl:if test="position()>1">,</xsl:if><xsl:value-of select="@name"/></xsl:if>
                                 </xsl:otherwise>
                             </xsl:choose>
                        </xsl:for-each>);
                    </xsl:when>
                    <xsl:otherwise>
                        assertNotNull(stub.<xsl:value-of select="@name"/>(
                        <xsl:for-each select="input/param[@type!='']">

                             <xsl:variable name="opname" select="@opname"/>
                             <xsl:variable name="paramname" select="@name"/>
                             <xsl:variable name="paramcount" select="count(param[@type!='' and @opname=$opname])"/>

                             <xsl:choose>
                                 <xsl:when test="$paramcount > 0">
                                      <xsl:for-each select="param[@type!='' and @opname=$opname]">
                                            <xsl:if test="position()>1">,</xsl:if>get<xsl:value-of  select="@partname"/>(<xsl:value-of select="$paramname"/>)
                                      </xsl:for-each>
                                 </xsl:when>
                                 <xsl:otherwise>
                                     <xsl:if test="@type!=''"><xsl:if test="position()>1">,</xsl:if><xsl:value-of select="@name"/></xsl:if>
                                 </xsl:otherwise>
                             </xsl:choose>
                             <!-- if the input/param element contain any attributes then we have to unwrap them-->

                        </xsl:for-each>));
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:choose>
                    <xsl:when test="$outputtype=''">
                    //There is no output to be tested!
                    stub.<xsl:value-of select="@name"/>();
                    </xsl:when>
                    <xsl:otherwise>
                    assertNotNull(stub.<xsl:value-of select="@name"/>());
                    </xsl:otherwise>
                </xsl:choose>
             </xsl:otherwise>
            </xsl:choose>



        }
        </xsl:if>
        <xsl:if test="$isAsync='1'">
            <xsl:variable name="tempCallbackName">tempCallback<xsl:value-of select="generate-id()"/></xsl:variable>
         /**
         * Auto generated test method
         */
        public  void testStart<xsl:value-of select="@name"/>() throws java.lang.Exception{
            <xsl:value-of select="$package"/>.<xsl:value-of select="$stubname"/> stub = new <xsl:value-of select="$package"/>.<xsl:value-of select="$stubname"/>();
             <xsl:choose>
             <xsl:when test="count(input/param)>0">
                  <xsl:for-each select="input/param[@type!='']">
                    <xsl:value-of select="@type"/><xsl:text> </xsl:text><xsl:value-of select="@name"/><xsl:text>=
                                                        </xsl:text>(<xsl:value-of select="@type"/>)getTestObject(<xsl:value-of select="@type"/>.class);
                    // todo Fill in the <xsl:value-of select="@name"/> here
                </xsl:for-each>

                stub.start<xsl:value-of select="@name"/>(
                         <xsl:for-each select="input/param[@type!='']">
                             <xsl:variable name="opname" select="@opname"/>
                             <xsl:variable name="paramname" select="@name"/>
                             <xsl:variable name="paramcount" select="count(param[@type!='' and @opname=$opname])"/>

                             <xsl:choose>
                                 <xsl:when test="$paramcount > 0">
                                      <xsl:for-each select="param[@type!='' and @opname=$opname]">
                                           <xsl:if test="position()>1">,</xsl:if>get<xsl:value-of select="@partname"/>(<xsl:value-of select="$paramname"/>)
                                      </xsl:for-each>
                                 </xsl:when>
                                 <xsl:otherwise>
                                     <xsl:if test="@type!=''"><xsl:if test="position()>1">,</xsl:if><xsl:value-of select="@name"/></xsl:if>
                                 </xsl:otherwise>
                             </xsl:choose>
                        </xsl:for-each>,
                    new <xsl:value-of select="$tempCallbackName"/>()
                );
              </xsl:when>
              <xsl:otherwise>
                stub.start<xsl:value-of select="@name"/>(
                    new <xsl:value-of select="$tempCallbackName"/>()
                );
             </xsl:otherwise>
            </xsl:choose>


        }

        private class <xsl:value-of select="$tempCallbackName"/>  extends <xsl:value-of select="$package"/>.<xsl:value-of select="$callbackname"/>{
            public <xsl:value-of select="$tempCallbackName"/>(){ super(null);}

            public void receiveResult<xsl:value-of select="@name"/>(org.apache.axis2.client.async.AsyncResult result) {
                assertNotNull(result.getResponseEnvelope().getBody().getFirstElement());
            }

            public void receiveError<xsl:value-of select="@name"/>(java.lang.Exception e) {
                fail();
            }

        }
      </xsl:if>
      <!-- end of in-out mep -->
      </xsl:if>
      <!-- start of in-only mep-->
      <xsl:if test="@mep='10' or @mep='11'">
          /**
          * Auto generated test method
          */
          public  void test<xsl:value-of select="@name"/>() throws java.lang.Exception{

          <xsl:value-of select="$package"/>.<xsl:value-of select="$stubname"/> stub =
          new <xsl:value-of select="$package"/>.<xsl:value-of select="$stubname"/>();//the default implementation should point to the right endpoint
          <xsl:choose>
              <xsl:when test="count(input/param)>0">
                  <xsl:for-each select="input/param[@type!='']">
                      <xsl:value-of select="@type"/><xsl:text> </xsl:text><xsl:value-of select="@name"/><xsl:text>=
                  </xsl:text>(<xsl:value-of select="@type"/>)getTestObject(<xsl:value-of select="@type"/>.class);
                      // todo Fill in the <xsl:value-of select="@name"/> here
                  </xsl:for-each>

                  //There is no output to be tested!
                  stub.<xsl:value-of select="@name"/>(
                  <xsl:for-each select="input/param[@type!='']">
                      <xsl:variable name="opname" select="@opname"/>
                             <xsl:variable name="paramname" select="@name"/>
                             <xsl:variable name="paramcount" select="count(param[@type!='' and @opname=$opname])"/>
                             <xsl:variable name="shorttype" select="@shorttype"/>

                             <xsl:choose>
                                 <xsl:when test="$paramcount > 0">
                                      <xsl:for-each select="param[@type!='' and @opname=$opname]">
                                            <xsl:if test="position()>1">,</xsl:if>get<xsl:value-of select="@partname"/>(<xsl:value-of select="$paramname"/>)
                                      </xsl:for-each>
                                 </xsl:when>
                                 <xsl:otherwise>
                                     <xsl:if test="@type!=''"><xsl:if test="position()>1">,</xsl:if><xsl:value-of select="@name"/></xsl:if>
                                 </xsl:otherwise>
                             </xsl:choose>
                  </xsl:for-each>);

              </xsl:when>
              <xsl:otherwise>


                  //There is no output to be tested!
                  stub.<xsl:value-of select="@name"/>();


              </xsl:otherwise>
          </xsl:choose>
          }
      </xsl:if>

     </xsl:for-each>

       <!-- generate the test object -->
        <xsl:apply-templates/>

    }
    </xsl:template>

 </xsl:stylesheet>