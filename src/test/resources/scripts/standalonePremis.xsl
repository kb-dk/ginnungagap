<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:java="http://xml.apache.org/xalan/java"
	xmlns:mets="http://www.loc.gov/METS/" xmlns:mix="http://www.loc.gov/mix/v20"
	xmlns:mods="http://www.loc.gov/mods/v3" xmlns:premis="info:lc/xmlns/premis-v3"

	extension-element-prefixes="java">

	<xsl:output encoding="UTF-8" method="xml" indent="yes" />

	<xsl:include href="transformToPremis.xsl" />

	<xsl:template match="record">
<!-- 		<xsl:call-template name="premis_preservation" /> -->
<!-- 		<xsl:call-template name="premis_event" /> -->
		<xsl:call-template name="premis_rights" />
<!-- 		<xsl:call-template name="premis_object" /> -->
	</xsl:template>

</xsl:transform> 
  