<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="/">
        <xsl:comment> This file was auto-generated from WSDL </xsl:comment>
        <xsl:comment> by the Apache Axis2 version: #axisVersion# #today# </xsl:comment>
        <serviceGroup>
            <xsl:apply-templates/>
        </serviceGroup>
    </xsl:template>

    <xsl:template match="interface">
        <xsl:variable name="package"><xsl:value-of select="@classpackage"/></xsl:variable>

        <service>
            <xsl:attribute name="name"><xsl:value-of select="@servicename"/></xsl:attribute>
            <messageReceivers>
                <xsl:for-each select="messagereceiver">
                    <xsl:if test=".">
                        <messageReceiver>
                            <xsl:attribute name="mep"><xsl:value-of select="@mepURI"/></xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="$package=''">
                                    <xsl:attribute name="class"><xsl:value-of select="."/></xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="class"><xsl:value-of select="$package"/>.<xsl:value-of select="."/></xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                        </messageReceiver>
                    </xsl:if>
                </xsl:for-each>
             </messageReceivers>

            <parameter name="ServiceClass" locked="false">
                <xsl:choose>
                    <xsl:when test="$package=''">
                        <xsl:value-of select="@name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$package"/>.<xsl:value-of select="@name"/>
                    </xsl:otherwise>
                </xsl:choose>
            </parameter>
			<xsl:for-each select="method">
				<operation>
					<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
					<xsl:attribute name="mep"><xsl:value-of select="@mepURI"/></xsl:attribute>
					<actionMapping>
						<xsl:value-of select="@soapaction"/>
					</actionMapping>
					<xsl:for-each select="outputActionMapping">
					<outputActionMapping>
						<xsl:value-of select="@Action"/>
					</outputActionMapping>
					</xsl:for-each>
					<xsl:for-each select="faultActionMapping">
					<faultActionMapping>
						<xsl:attribute name="faultName"><xsl:value-of select="@faultName"/></xsl:attribute>
						<xsl:value-of select="@Action"/>
					</faultActionMapping>
					</xsl:for-each>
				</operation>
			</xsl:for-each>
        </service>
    </xsl:template>
</xsl:stylesheet>