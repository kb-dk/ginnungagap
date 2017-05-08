<?xml version="1.0" encoding="UTF-8"?> 
<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:mets="http://www.loc.gov/METS/"
    
    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <xsl:template match="catalog">
    <xsl:call-template name="structmap_generator" />
  </xsl:template>
  
  <xsl:template name="structmap_generator">
    <mets:mets xsi:schemaLocation="http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/version111/mets.xsd">
      <xsl:attribute name="TYPE">
        <xsl:value-of select="'Representation'" />
      </xsl:attribute>
      <xsl:attribute name="OBJID">
        <xsl:value-of select="uuid" />
      </xsl:attribute>
      <xsl:attribute name="PROFILE">
        <xsl:value-of select="java:dk.kb.metadata.Constants.getProfileURL()" />
      </xsl:attribute>
            
      <!-- START metsHdr -->
      <xsl:element name="mets:metsHdr">
        <xsl:attribute name="CREATEDATE">
          <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
        </xsl:attribute>

        <xsl:element name="mets:agent">
          <xsl:attribute name="ID">
            <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getKbAgent()" />
          </xsl:attribute>
          <xsl:attribute name="ROLE"> 
            <xsl:value-of select="'CREATOR'" />
          </xsl:attribute>
          <xsl:attribute name="TYPE">
            <xsl:value-of select="'ORGANIZATION'" />
          </xsl:attribute>
          <xsl:element name="mets:name">
            <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getKbAgentValue()" />
          </xsl:element>
          <xsl:element name="mets:note">
            <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getKbAgentType()" />
          </xsl:element>
        </xsl:element>
        
        <xsl:element name="mets:agent">
          <xsl:attribute name="ID">
            <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgent()" />
          </xsl:attribute>
          <xsl:attribute name="ROLE"> 
            <xsl:value-of select="'CREATOR'" />
          </xsl:attribute>
          <xsl:attribute name="TYPE">
            <xsl:value-of select="'OTHER'" />
          </xsl:attribute>
          <xsl:attribute name="OTHERTYPE">
            <xsl:value-of select="'API'" />
          </xsl:attribute>
          <xsl:element name="mets:name">
            <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgentValue()" />
          </xsl:element>
          <xsl:element name="mets:note">
            <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgentType()" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END metsHdr -->
      
      <!-- START structMap -->
      <xsl:element name="mets:structMap">
        <xsl:attribute name="TYPE">
          <xsl:value-of select="'catalog'" />
        </xsl:attribute>
        
        <xsl:element name="mets:div">
          <xsl:attribute name="xlink:label">
            <xsl:value-of select="catalogName" />
          </xsl:attribute>
          <xsl:for-each select="record">
            <xsl:element name="mets:mptr">
              <xsl:attribute name="ID">
                <xsl:value-of select="name" />
              </xsl:attribute>
              <xsl:attribute name="LOCTYPE">
                <xsl:value-of select="'URN'" />
              </xsl:attribute>
              <xsl:attribute name="xlink:href">
                <xsl:value-of select="java:dk.kb.metadata.utils.GuidExtrationUtils.extractGuid(guid)" />
              </xsl:attribute>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:element>
      <!-- END structMap -->
      
    </mets:mets>
  </xsl:template>
</xsl:transform> 
