<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org.1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:pbcore="http://www.pbcore.org/PBCore/PBCoreNamespace.html"
    
    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <xsl:template name="pbcore_description">
    <pbcore:pbcoreDescriptionDocument xsi:schemaLocation="http://www.pbcore.org/PBCore/PBCoreNamespace.html https://raw.githubusercontent.com/WGBH/PBCore_2.1/master/pbcore-2.1.xsd" version="2.1">
      <!-- pbcore asset type -->
      <xsl:if test="field[@name='pbcoreAssetType']">
        <xsl:element name="pbcore:pbcoreAssetType">
          <xsl:value-of select="field[@name='pbcoreAssetType']/value" />
        </xsl:element>
      </xsl:if>
      <!-- pbcore assert date -->
      <xsl:if test="field[@name='pbcoreAssetDate']">
        <xsl:element name="pbcore:pbcoreAssetDate">
          <xsl:value-of select="field[@name='pbcoreAssetDate']/value" />
        </xsl:element>
      </xsl:if>
      <!-- pbcore identifier (required) -->
      <xsl:element name="pbcore:pbcoreIdentifier">
        <xsl:attribute name="source">
          <xsl:value-of select="'Cumulus'" />
        </xsl:attribute>
        <xsl:value-of select="field[@name='pbcoreIdentifier']/value" />
      </xsl:element>
      <!-- pbcore title (required) -->
      <xsl:element name="pbcore:pbcoreTitle">
        <xsl:value-of select="field[@name='pbcoreTitle']/value" />
      </xsl:element>
      <!-- pbcore subject -->
      <xsl:for-each select="field[@name='pbcoreSubject']/value">
        <xsl:element name="pbcore:pbcoreSubject">
          <xsl:value-of select="." />
        </xsl:element>
      </xsl:for-each>
      <!-- pbcore description (required) -->
      <xsl:element name="pbcore:pbcoreDescription">
        <xsl:value-of select="field[@name='pbcoreDescription']/value" />
      </xsl:element>
      <!-- pbcore genre -->
      <xsl:choose>
        <xsl:when test="field[@name='pbcoreGenre']">
          <xsl:element name="pbcore:pbcoreGenre">
            <xsl:value-of select="field[@name='pbcoreGenre']/value" />
          </xsl:element>
        </xsl:when>
        <xsl:when test="field[@name='Genre']">
          <xsl:element name="pbcore:pbcoreGenre">
            <xsl:value-of select="field[@name='Genre']/value" />
          </xsl:element>
        </xsl:when>
      </xsl:choose>
      <!-- pbcore audience level -->
      <xsl:if test="field[@name='pbcoreAudienceLevel']">
        <xsl:element name="pbcore:pbcoreAudienceLevel">
          <xsl:value-of select="field[@name='pbcoreAudienceLevel']/value" />
        </xsl:element>
      </xsl:if>
      <!-- pbcore audience rating -->
      <xsl:if test="field[@name='pbcoreAudienceRating']">
        <xsl:element name="pbcore:pbcoreAudienceRating">
          <xsl:value-of select="field[@name='pbcoreAudienceRating']/value" />
        </xsl:element>
      </xsl:if>
      <!-- pbcore creator element -->
      <xsl:if test="field[@name='pbcoreCreator']">
        <xsl:element name="pbcore:pbcoreCreator">
          <xsl:element name="pbcore:creator">
            <xsl:value-of select="field[@name='pbcoreCreator']/value" />
          </xsl:element>
          <xsl:if test="field[@name='pbcorecreatorRole']">
            <xsl:element name="pbcore:creatorRole">
              <xsl:value-of select="field[@name='pbcorecreatorRole']/value" />
            </xsl:element>
          </xsl:if>
        </xsl:element>
      </xsl:if>
      <!-- pbcore contributor element -->
      <xsl:if test="field[@name='pbcoreContributor']">
        <xsl:element name="pbcore:pbcoreContributor">
          <xsl:element name="pbcore:contributor">
            <xsl:value-of select="field[@name='pbcoreContributor']/value" />
          </xsl:element>
          <xsl:if test="field[@name='pbcorecontributorRole']">
            <xsl:element name="pbcore:contributorRole">
              <xsl:value-of select="field[@name='pbcorecontributorRole']/value" />
            </xsl:element>
          </xsl:if>
        </xsl:element>
      </xsl:if>
      <!-- pbcore publisher element -->
      <xsl:if test="field[@name='pbcorePublisher']">
        <xsl:element name="pbcore:pbcorePublisher">
          <xsl:element name="pbcore:publisher">
            <xsl:value-of select="field[@name='pbcorePublisher']/value" />
          </xsl:element>
          <xsl:if test="field[@name='pbcorepublisherRole']">
            <xsl:element name="pbcore:publisherRole">
              <xsl:value-of select="field[@name='pbcorepublisherRole']/value" />
            </xsl:element>
          </xsl:if>
        </xsl:element>
      </xsl:if>
      <!-- pbcore rights element -->
      <xsl:if test="field[@name='pbcorerightsSummary'] or field[@name='pbcorerightsLink'] or field[@name='pbcorerightsEmbedded']">
        <xsl:if test="field[@name='pbcorerightsSummary']">
          <xsl:element name="pbcore:pbcoreRightsSummary">
            <xsl:element name="pbcore:rightsSummary">
              <xsl:value-of select="field[@name='pbcorerightsSummary']/value" />
            </xsl:element>
            <xsl:if test="field[@name='pbcorerightsLink']">
              <xsl:element name="pbcore:rightsLink">
                <xsl:value-of select="field[@name='pbcorerightsLink']/value" />
              </xsl:element>
            </xsl:if>
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='pbcorerightsEmbedded']">
          <xsl:element name="pbcore:pbcoreRightsSummary">
            <xsl:element name="pbcore:rightsSummary">
              <xsl:value-of select="field[@name='pbcorerightsEmbedded']/value" />
            </xsl:element>
          </xsl:element>
        </xsl:if>
      </xsl:if>
      <!-- pbcore annotation -->
      <xsl:if test="field[@name='pbcoreAnnotation']">
        <xsl:element name="pbcore:pbcoreAnnotation">
          <xsl:value-of select="field[@name='pbcoreAnnotation']/value" />
        </xsl:element>
      </xsl:if>
    </pbcore:pbcoreDescriptionDocument>
  </xsl:template>

  <xsl:template name="pbcore_instantiation">
    <pbcore:pbcoreInstiationDocument xsi:schemaLocation="http://www.pbcore.org/PBCore/PBCoreNamespace.html https://raw.githubusercontent.com/WGBH/PBCore_2.1/master/pbcore-2.1.xsd" version="2.1">
      <!-- instatiation identifier = pbcore identifier (required) -->
      <xsl:element name="pbcore:instantiationIdentifier">
        <xsl:value-of select="field[@name='pbcoreIdentifier']/value" />
      </xsl:element>
      <!-- instantiation date -->
      <xsl:if test="field[@name='instantiationDate']">
        <xsl:element name="pbcore:instantiationDate">
          <xsl:value-of select="field[@name='instantiationDate']/value" />
        </xsl:element>
      </xsl:if>
      <!-- instantiation physical -->
      <xsl:if test="field[@name='instantiationPhysical']">
        <xsl:element name="pbcore:instantiationPhysical">
          <xsl:value-of select="field[@name='instantiationPhysical']/value" />
        </xsl:element>
      </xsl:if>
      <!-- instantiation digital -->
      <xsl:if test="field[@name='instantiationDigital']">
        <xsl:element name="pbcore:instantiationDigital">
          <xsl:value-of select="field[@name='instantiationDigital']/value" />
        </xsl:element>
      </xsl:if>
      <!-- instantiation file size -->
      <xsl:if test="field[@name='instantiationFileSize']">
        <xsl:element name="pbcore:instantiationFileSize">
          <xsl:value-of select="field[@name='instantiationFileSize']/value" />
        </xsl:element>
      </xsl:if>
      <!-- instantiation channel configuration -->
      <xsl:if test="field[@name='instantiationChannelConfiguration']">
        <xsl:element name="pbcore:instantiationChannelConfiguration">
          <xsl:value-of select="field[@name='instantiationChannelConfiguration']/value" />
        </xsl:element>
      </xsl:if>
      <!-- instantiation language -->
      <xsl:if test="field[@name='instantiationLanguage']">
        <xsl:element name="pbcore:instantiationLanguage">
          <xsl:value-of select="field[@name='instantiationLanguage']/value" />
        </xsl:element>
      </xsl:if>
      <!-- instantiation essence track -->
      <xsl:element name="pbcore:instantiationEssenceTrack">
        <xsl:if test="field[@name='essenceTrackStandard']">
          <xsl:element name="pbcore:essenceTrackStandard">
            <xsl:value-of select="field[@name='essenceTrackStandard']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='essenceTrackDataRate']">
          <xsl:element name="pbcore:essenceTrackDataRate">
            <xsl:value-of select="field[@name='essenceTrackDataRate']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='essenceTrackSamplingRate']">
          <xsl:element name="pbcore:essenceTrackSamplingRate">
            <xsl:value-of select="field[@name='essenceTrackSamplingRate']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='essenceTrackBitDepth']">
          <xsl:element name="pbcore:essenceTrackBitDepth">
            <xsl:value-of select="field[@name='essenceTrackBitDepth']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='essenceTrackDuration']">
          <xsl:element name="pbcore:essenceTrackDuration">
            <xsl:value-of select="field[@name='essenceTrackDuration']/value" />
          </xsl:element>
        </xsl:if>
      </xsl:element>
    </pbcore:pbcoreInstiationDocument>
  </xsl:template> 
</xsl:stylesheet> 
