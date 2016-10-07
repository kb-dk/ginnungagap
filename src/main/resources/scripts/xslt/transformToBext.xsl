<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org.1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:bext="http://www.digitizationguidelines.gov/guidelines/digitize-embedding.html"
    
    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <xsl:template name="bext">
    <bext:conformation_point_document xsi:schemaLocation="http://www.digitizationguidelines.gov/guidelines/digitize-embedding.html http://id.kb.dk/standards/bext/version_2/bext.xsd">
      <xsl:comment>
        <xsl:value-of select="'No official page for the xsd standard. Using local page for location. NOTE: The schema is not managed by an international metadata authority. It is a copy from the BWF Metaedit tool v. 1.3.1. '" />
      </xsl:comment>
      <xsl:element name="bext:File">
        <!-- Mandatory attribute 'name' -->
        <xsl:attribute name="name">
          <xsl:value-of select="field[@name='Asset Name']/value" />
        </xsl:attribute>
        <!-- Optional BEXT field 'Description' -->
        <xsl:if test="field[@name='BEXT Description']">
          <xsl:element name="bext:Description">
            <xsl:value-of select="field[@name='BEXT Description']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional BEXT field 'Originator' -->
        <xsl:if test="field[@name='BEXT Originator']">
          <xsl:element name="bext:Originator">
            <xsl:value-of select="field[@name='BEXT Originator']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional BEXT field 'OriginatorReference' -->
        <xsl:if test="field[@name='BEXT OriginatorReference']">
          <xsl:element name="bext:OriginatorReference">
            <xsl:value-of select="field[@name='BEXT OriginatorReference']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional BEXT field 'OriginatorReference' -->
        <xsl:if test="field[@name='BEXT OriginatorReference']">
          <xsl:element name="bext:OriginatorReference">
            <xsl:value-of select="field[@name='BEXT OriginatorReference']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional BEXT field 'Version' -->
        <xsl:if test="field[@name='BEXT Version']">
          <xsl:element name="bext:Version">
            <xsl:value-of select="field[@name='BEXT Version']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional BEXT field 'CodingHistory' -->
        <xsl:if test="field[@name='BEXT CodingHistory']">
          <xsl:element name="bext:CodingHistory">
            <xsl:value-of select="field[@name='BEXT CodingHistory']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'IARL' -->
        <xsl:if test="field[@name='IARL Archival Location']">
          <xsl:element name="bext:IARL">
            <xsl:value-of select="field[@name='BEXT Originator']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'IART' -->
        <xsl:if test="field[@name='IART Artist']">
          <xsl:element name="bext:IART">
            <xsl:value-of select="field[@name='IART Artist']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'ICMS' -->
        <xsl:if test="field[@name='ICMS Commissioned']">
          <xsl:element name="bext:ICMS">
            <xsl:value-of select="field[@name='ICMS Commissioned']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'ICMT' -->
        <xsl:if test="field[@name='ICMT Comments']">
          <xsl:element name="bext:ICMT">
            <xsl:value-of select="field[@name='ICMT Comments']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'ICRD' -->
        <xsl:if test="field[@name='ICRD Creation date']">
          <xsl:element name="bext:ICRD">
            <xsl:value-of select="field[@name='ICRD Creation date']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'IENG' -->
        <xsl:if test="field[@name='IENG Engineer']">
          <xsl:element name="bext:IENG">
            <xsl:value-of select="field[@name='IENG Engineer']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'IKEY' -->
        <xsl:if test="field[@name='IKEY Keywords']">
          <xsl:element name="bext:IKEY">
            <xsl:value-of select="field[@name='IKEY Keywords']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'IMED' -->
        <xsl:if test="field[@name='IMED Medium']">
          <xsl:element name="bext:IMED">
            <xsl:value-of select="field[@name='IMED Medium']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'INAM' -->
        <xsl:if test="field[@name='INAM Name (Title)']">
          <xsl:element name="bext:INAM">
            <xsl:value-of select="field[@name='INAM Name (Title)']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'IPRD' -->
        <xsl:if test="field[@name='IPRD Product (Album)']">
          <xsl:element name="bext:IPRD">
            <xsl:value-of select="field[@name='IPRD Product (Album)']/value" />
          </xsl:element>
        </xsl:if>
        <!-- Optional LIST INFO field 'ISRC' -->
        <xsl:if test="field[@name='ISRC Source']">
          <xsl:element name="bext:ISRC">
            <xsl:value-of select="field[@name='ISRC Source']/value" />
          </xsl:element>
        </xsl:if>
      </xsl:element>
     </bext:conformation_point_document>
  </xsl:template>
 
</xsl:stylesheet> 
