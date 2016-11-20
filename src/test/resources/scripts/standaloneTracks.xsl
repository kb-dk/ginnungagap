<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:java="http://xml.apache.org/xalan/java"
	xmlns:tracks="http://id.kb.dk/tracks.html"

	extension-element-prefixes="java">

	<xsl:output encoding="UTF-8" method="xml" indent="yes" />

	<xsl:template match="record">
	  <tracks:tracks xsi:schemaLocation="http://id.kb.dk/tracks.html http://id.kb.dk/standards/tracks/version_beta/tracks.xsd">
	    <xsl:for-each select="field[@name='Tracks']/table/row">
          <xsl:element name="tracks:track">
            <xsl:for-each select="field[@name='Nr']/value">
              <xsl:element name="tracks:Nr">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='ID']/value">
              <xsl:element name="tracks:ID">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Type']/value">
              <xsl:element name="tracks:Type">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Begyndelsestid']/value">
              <xsl:element name="tracks:Begyndelsestid">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Varighed']/value">
              <xsl:element name="tracks:Varighed">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='1. linie']/value">
              <xsl:element name="tracks:Start_linie">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track medvirkende']/value">
              <xsl:element name="tracks:Track_medvirkende">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track ophav']/value">
              <xsl:element name="tracks:Track_ophav">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track titel']/value">
              <xsl:element name="tracks:Track_titel">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track undertitel']/value">
              <xsl:element name="tracks:Track_undertitel">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track alternativ titel']/value">
              <xsl:element name="tracks:Track_alternativ_titel">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track original titel']/value">
              <xsl:element name="tracks:Track_original_titel">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Track noter']/value">
              <xsl:element name="tracks:Track_noter">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Instrumenter']/value">
              <xsl:element name="tracks:Instrumenter">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="field[@name='Antal versioner']/value">
              <xsl:element name="tracks:Antal_versioner">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
	    </xsl:for-each>
	  </tracks:tracks>
	</xsl:template>
</xsl:transform> 
  