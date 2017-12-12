<?xml version="1.0" encoding="UTF-8"?> 
<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:kbids="http://id.kb.dk/schemas/kbids/v1"
        
    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <xsl:template match="record">
    <xsl:call-template name="kb_ids_generator" />
  </xsl:template>
  
  <xsl:template name="kb_ids_generator">
    <kbids:kbids xsi:schemaLocation="http://id.kb.dk/schemas/kbids/v1 http://id.kb.dk/schemas/kbids/v1/kbids.xsd">
      <xsl:attribute name="version">
        <xsl:value-of select="'1.0'" />
      </xsl:attribute>
      <xsl:element name="kbids:intellectualEntityId">
        <xsl:value-of select="ie_uuid" />
      </xsl:element>
      <xsl:element name="kbids:objectId">
        <xsl:value-of select="object_uuid" />
      </xsl:element>
      <xsl:if test="file_uuid">
        <xsl:element name="kbids:fileId">
          <xsl:value-of select="file_uuid" />
        </xsl:element>
      </xsl:if>
    </kbids:kbids>
  </xsl:template>
</xsl:transform> 
