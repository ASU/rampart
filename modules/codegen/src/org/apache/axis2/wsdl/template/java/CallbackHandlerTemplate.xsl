<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <xsl:template match="/callback">
    /**
     * <xsl:value-of select="@name"/>.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: #axisVersion# #today#
     */
    package <xsl:value-of select="@package"/>;

    /**
     *  <xsl:value-of select="@name"/> Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class <xsl:value-of select="@name"/>{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public <xsl:value-of select="@name"/>(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public <xsl:value-of select="@name"/>(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        <xsl:for-each select="method">
            <xsl:variable name="outParamType"><xsl:value-of select="output/param/@type"/></xsl:variable>
            <xsl:variable name="outParamName"><xsl:value-of select="output/param/@name"/></xsl:variable>
            <xsl:variable name="mep"><xsl:value-of select="@mep"/></xsl:variable>
            <xsl:choose>
                <!-- Code generation for in-out only. Need to consider the other meps also
                    They should be parts of this xsl:choose loop -->
                <xsl:when test="$mep='12'">
           /**
            * auto generated Axis2 call back method for <xsl:value-of select="@name"/> method
            *
            */
           public void receiveResult<xsl:value-of select="@name"/>(
                    <xsl:if test="$outParamType!=''"><xsl:value-of select="$outParamType"/><xsl:text> </xsl:text><xsl:value-of select="$outParamName"/></xsl:if>) {
           }

          /**
           * auto generated Axis2 Error handler
           *
           */
            public void receiveError<xsl:value-of select="@name"/>(java.lang.Exception e) {
            }
                </xsl:when>
                <xsl:otherwise>
               // No methods generated for meps other than in-out
                </xsl:otherwise>
            </xsl:choose>


        </xsl:for-each>


    }
    </xsl:template>
</xsl:stylesheet>