<?xml version="1.0" encoding="UTF-8"?>
<!-- FOR 'BILLEDER' -->
<xsl:transform version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:cdl="http://www.cdlib.org/inside/diglib/copyrightMD"
    xmlns:dk="http://id.kb.dk/dk-corporate-creator"

    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <!-- START defining variables -->
  <xsl:variable name="image_uri_base" select="'http://www.kb.dk/imageService'"/>

  <xsl:variable name="type_of_resource" select="''" />
  <xsl:variable name="type_of_event" select="'references'" />

  <xsl:variable name="ID" select="java:dk.kb.metadata.utils.GuidExtractionUtils.extractGuid(record/field[@name='GUID']/value)" />

  <xsl:variable name="MODS_LOCATION" select="'http://www.loc.gov/mods/v3 http://id.kb.dk/standards/mods/version_3_7/mods-3-7.xsd'" />
  <xsl:variable name="MODS_VERSION" select="'3.7'" />

  <xsl:variable name="CDL_LOCATION" select="'http://www.cdlib.org/inside/diglib/copyrightMD http://id.kb.dk/schemas/dk-corporate-creator/copyright-md.xsd'" />
  <xsl:variable name="CDL_COPYRIGHT_STATUS" select="'copyrighted'"/>
  <xsl:variable name="CDL_PUBLICATION_STATUS" select="'unknown'"/>
  <!-- <xsl:variable name="CDL_VERSION" select="'1.0'" /> -->

  <xsl:variable name="mods_default_lang">
   <xsl:choose>
     <xsl:when test="record/field[@name='Cataloging language']">
       <xsl:value-of select="record/field[@name='Cataloging language']/value" />
     </xsl:when>
     <xsl:otherwise>da</xsl:otherwise>
   </xsl:choose>
  </xsl:variable>
  <!-- END defining variables -->

  <!-- RETRIEVAL FUNCTIONS -->
  <!-- Retrieve the language of a 'Cumulus' field and apply it as attribute. -->
  <xsl:template name="cumulus_get_lang_attribute">
    <xsl:if test="contains(., '|')">
      <xsl:attribute name="lang">
        <xsl:value-of select="java:dk.kb.metadata.utils.TransformUtils.getCumulusLang(.,$mods_default_lang)" />
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  <!-- Retrieve the value of a 'Cumulus' field and return it. -->
  <xsl:template name="cumulus_get_value">
    <xsl:value-of select="java:dk.kb.metadata.utils.TransformUtils.getCumulusVal(.)" />
  </xsl:template>

  <!-- MODS TEMPLATE BASE (whole mods) -->
  <xsl:template name="mods">

    <!-- Begin the MODS document -->
    <mods:mods xsi:schemaLocation="{$MODS_LOCATION}" version="{$MODS_VERSION}">
      <!-- mods:abstract -->
      <xsl:call-template name="mods_abstract" />
      <!-- mods:classification -->
      <xsl:call-template name="mods_classification" />
      <!-- mods:extension-->
      <xsl:call-template name="mods_extension" />
      <!-- mods:genre -->
      <xsl:call-template name="mods_genre" />
      <!-- mods:identifiers -->
      <xsl:call-template name="mods_identifier_for_records" />
      <xsl:call-template name="mods_identifier" />
      <!-- mods:language -->
      <xsl:call-template name="mods_language" />
      <!-- mods:location -->
      <xsl:call-template name="mods_location" />
      <!-- mods:name -->
      <xsl:call-template name="mods_name" />
      <!-- mods:ophav -->
      <xsl:call-template name="mods_name_ophav" />
      <!-- mods:note -->
      <xsl:call-template name="mods_note" />
      <!-- mods:originInfo -->
      <xsl:call-template name="mods_originInfo" />
      <!-- mods:part -->
      <xsl:call-template name="mods_part" />
      <!-- mods:physicalDescription -->
      <xsl:call-template name="mods_physicalDescription" />
      <!-- mods:recordInfo -->
      <xsl:call-template name="mods_recordInfo" />
      <!-- mods:relatedItem -->
      <xsl:call-template name="mods_relatedItem_File" />
      <!-- mods:relatedItem -->
      <xsl:call-template name="mods_relatedItem" />
      <!-- mods:subject -->
      <xsl:call-template name="mods_subject" />
      <!-- mods:tableOfContents -->
      <xsl:call-template name="mods_tableOfContents" />
      <!-- mods:targetAudience -->
      <xsl:call-template name="mods_targetAudience" />
      <!-- mods:titleInfo -->
      <xsl:call-template name="mods_titleInfo" />
      <!-- mods:typeOfResource -->
      <xsl:call-template name="mods_typeOfResource" />
    </mods:mods>
  </xsl:template>

  <!-- MODS MASTER ASSET TEMPLATE BASE -->
  <xsl:template name="mods_for_representation_mets">
    <!-- Begin the MODS document -->
    <mods:mods xsi:schemaLocation="{$MODS_LOCATION}" version="{$MODS_VERSION}">
      <!-- mods:abstract -->
      <xsl:call-template name="mods_abstract" />
      <!-- mods:classification -->
      <xsl:call-template name="mods_classification" />
      <!-- mods:extension-->
      <xsl:call-template name="mods_extension" />
      <!-- mods:genre -->
      <xsl:call-template name="mods_genre" />
      <!-- mods:identifiers -->
      <xsl:call-template name="mods_identifier" />
      <!-- mods:language -->
      <xsl:call-template name="mods_language" />
      <!-- mods:location -->
      <xsl:call-template name="mods_location" />
      <!-- mods:name -->
      <xsl:call-template name="mods_name" />
      <!-- mods:ophav -->
      <xsl:call-template name="mods_name_ophav" />
      <!-- mods:note -->
      <xsl:call-template name="mods_note" />
      <!-- mods:originInfo -->
      <xsl:call-template name="mods_originInfo" />
      <!-- mods:part -->
      <xsl:call-template name="mods_part" />
      <!-- mods:physicalDescription -->
      <xsl:call-template name="mods_physicalDescription" />
      <!-- mods:relatedItem -->
      <xsl:call-template name="mods_relatedItem" />
      <!-- mods:subject -->
      <xsl:call-template name="mods_subject" />
      <!-- mods:tableOfContents -->
      <xsl:call-template name="mods_tableOfContents" />
      <!-- mods:targetAudience -->
      <xsl:call-template name="mods_targetAudience" />
      <!-- mods:titleInfo -->
      <xsl:call-template name="mods_titleInfo" />
    </mods:mods>
  </xsl:template>

  <!-- MODS RIGHTS TEMPLATE BASE -->
  <xsl:template name="mods_rights">
    <!-- Begin the MODS document -->
    <mods:mods xsi:schemaLocation="{$MODS_LOCATION}" version="{$MODS_VERSION}">
      <!-- mods:accessCondition -->
      <xsl:call-template name="mods_accessCondition" />
    </mods:mods>
  </xsl:template>

  <!-- START abstract -->
  <xsl:template name="mods_abstract">
    <!-- NOTHING YET! -->
  </xsl:template>
  <!-- End abstract -->

  <!-- START accessCondition (only for MODS:RIGHTS)-->
  <xsl:template name="mods_accessCondition">
<!--TODO: To be removed -->
<!--    -->
<!--    <xsl:for-each select="field[@name='Copyright']/value">-->
<!--      <xsl:element name="mods:accessCondition">-->
<!--        <xsl:attribute name="displayLabel">-->
<!--          <xsl:value-of select="'Copyright'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:call-template name="cumulus_get_value" />-->
<!--      </xsl:element>-->
<!--    </xsl:for-each>-->
<!--TODO: To be removed-->
<!--    -->
<!--    <xsl:for-each select="field[@name='Copyright Notice']/value">-->
<!--      <xsl:element name="mods:accessCondition">-->
<!--        <xsl:attribute name="displayLabel">-->
<!--          <xsl:value-of select="'Copyright Notice'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:call-template name="cumulus_get_value" />-->
<!--      </xsl:element>-->
<!--    </xsl:for-each>-->

    <xsl:if test="field[@name='Blokeret']">
      <xsl:variable name="BLOK">
        <xsl:copy-of select="field[@name='Blokeret']/value"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$BLOK = 'true'">
          <xsl:element name="mods:accessCondition">
            <xsl:attribute name="type">
              <xsl:value-of select="'restriction on access'" />
            </xsl:attribute>
            <xsl:attribute name="displayLabel">Access Status</xsl:attribute>
            <xsl:value-of select="'Blokeret'" />
          </xsl:element>
        </xsl:when>
      </xsl:choose>
    </xsl:if>

    <xsl:if test="field[@name='Blokeringsnote']">
      <xsl:element name="mods:accessCondition">
        <xsl:attribute name="type">
          <xsl:value-of select="'restriction on access note'" />
        </xsl:attribute>
        <xsl:for-each select="field[@name='Blokeringsnote']/value">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:call-template name="cumulus_get_value" />
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <xsl:if test="field[@name='Særlige visningsvilkår']">
      <xsl:variable name="SVV">
        <xsl:copy-of select="field[@name='Særlige visningsvilkår']/value"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$SVV = 'Ejermærke' or $SVV = 'CC-BY' or $SVV = 'Vandmærke'">
          <xsl:element name="mods:accessCondition">
            <xsl:attribute name="type">
              <xsl:value-of select="'use and reproduction'" />
            </xsl:attribute>
            <xsl:attribute name="displayLabel">Restricted</xsl:attribute>
            <xsl:for-each select="field[@name='Særlige visningsvilkår']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:element>
        </xsl:when>
        <xsl:when test="$SVV = 'Visning kun på stedet' or $SVV = 'Kun visning af metadata'">
          <xsl:element name="mods:accessCondition">
            <xsl:attribute name="type">
              <xsl:value-of select="'restriction on access'" />
            </xsl:attribute>
            <xsl:attribute name="displayLabel">Access Status</xsl:attribute>
            <xsl:for-each select="field[@name='Særlige visningsvilkår']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:element>
        </xsl:when>
        <xsl:otherwise>
          <xsl:element name="mods:accessCondition">
            <xsl:for-each select="field[@name='Særlige visningsvilkår']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>

    <xsl:if test="field[@name='Visningsnote']">
      <xsl:element name="mods:accessCondition">
        <xsl:attribute name="type">
          <xsl:value-of select="'use and reproduction note'" />
        </xsl:attribute>
        <xsl:attribute name="displayLabel">Restricted</xsl:attribute>
        <xsl:for-each select="field[@name='Visningsnote']/value">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:call-template name="cumulus_get_value" />
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <xsl:if test="field[@name='Pligtafleveret']">
      <xsl:variable name="PLIGT">
        <xsl:copy-of select="field[@name='Pligtafleveret']/value"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$PLIGT = 'true'">
          <xsl:element name="mods:accessCondition">
            <xsl:attribute name="type">
              <xsl:value-of select="'pligtaflevering'" />
            </xsl:attribute>
            <xsl:value-of select="'Pligtafleveret'" />
          </xsl:element>
        </xsl:when>
      </xsl:choose>
    </xsl:if>

    <!-- Start the CDL accessCondition -->
    <xsl:element name="mods:accessCondition">
      <cdl:copyright xsi:schemaLocation="{$CDL_LOCATION}" copyright.status="{$CDL_COPYRIGHT_STATUS}" publication.status="{$CDL_PUBLICATION_STATUS}">
        <xsl:call-template name="cdl_accessCondition" />
      </cdl:copyright>
    </xsl:element>

  </xsl:template>
  <!-- END accessCondition -->

  <!-- START classification -->
  <xsl:template name="mods_classification">
    <xsl:for-each select="field[@name='DK5']/value">
      <xsl:element name="mods:classification">
        <xsl:attribute name="authority">
          <xsl:value-of select="'DK5'" />
        </xsl:attribute>
        <xsl:value-of select="." />
      </xsl:element>
    </xsl:for-each>
  </xsl:template>
  <!-- END classification -->

  <!-- START extension -->
  <xsl:template name="mods_extension">
    <!-- NOTHING YET! -->
  </xsl:template>
  <!-- END extension -->

  <!-- START genre -->
  <xsl:template name="mods_genre">
    <!-- Genre -->
    <xsl:for-each select="field[@name='Genre']/value">
      <xsl:element name="mods:genre">
        <xsl:value-of select="." />
      </xsl:element>
    </xsl:for-each>

    <!-- Categories -->
    <xsl:for-each select="field[@name='Categories']/value">
      <xsl:element name="mods:genre">
        <xsl:attribute name="type">
          <xsl:value-of select="'Categories'" />
        </xsl:attribute>
        <xsl:value-of select="." />
      </xsl:element>
    </xsl:for-each>

  </xsl:template>
  <!-- END genre -->

  <!-- START identifiers for records -->
  <xsl:template name="mods_identifier_for_records">
    <!-- GUID -->
    <xsl:if test="field[@name='GUID']/value">
      <xsl:element name="mods:identifier">
        <xsl:attribute name="type">
          <xsl:value-of select="'uri'" />
        </xsl:attribute>
        <xsl:value-of select="concat('urn:uuid:', $ID)" />
      </xsl:element>
    </xsl:if>

    <!-- Record Name -->
    <xsl:for-each select="field[@name='Record Name']/value">
      <xsl:element name="mods:identifier">
        <xsl:attribute name="type">
          <xsl:value-of select="'local'" />
        </xsl:attribute>
        <xsl:value-of select="." />
      </xsl:element>
    </xsl:for-each>

    <!-- Asset Reference -->
    <xsl:if test="field[@name='Asset Reference']">
        <xsl:element name="mods:identifier">
          <xsl:attribute name="type">
            <xsl:value-of select="'Asset Reference'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Asset Reference']/value" />
        </xsl:element>
    </xsl:if>

    <!-- Original Asset Reference -->
    <xsl:if test="field[@name='Original Asset Reference']">
        <xsl:element name="mods:identifier">
          <xsl:attribute name="type">
            <xsl:value-of select="'Original Asset Reference'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Original Asset Reference']/value" />
        </xsl:element>
    </xsl:if>
  </xsl:template>
  <!-- END identifiers for records -->

  <!-- START identifiers for both records and representations -->
  <xsl:template name="mods_identifier">

    <!-- Accessionsnr eller Accessionsnummer -->
    <xsl:choose>
      <xsl:when test="field[@name='Accessionsnr']">
        <xsl:element name="mods:identifier">
          <xsl:attribute name="displayLabel">
            <xsl:value-of select="'Accession number'" />
          </xsl:attribute>
          <xsl:attribute name="type">
            <xsl:value-of select="'accession number'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Accessionsnr']/value" />
        </xsl:element>
      </xsl:when>
      <xsl:when test="field[@name='Accessionsnummer']">
        <xsl:element name="mods:identifier">
          <xsl:attribute name="displayLabel">
            <xsl:value-of select="'Accession number'" />
          </xsl:attribute>
          <xsl:attribute name="type">
            <xsl:value-of select="'accession number'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Accessionsnummer']/value" />
        </xsl:element>
      </xsl:when>
    </xsl:choose>

    <!-- Scannenummer || Scan Number -->
    <xsl:choose>
      <xsl:when test="field[@name='Scannenummer']">
        <xsl:element name="mods:identifier">
          <xsl:attribute name="type">
            <xsl:value-of select="'Scannenummer'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Scannenummer']/value" />
        </xsl:element>
      </xsl:when>
      <xsl:when test="field[@name='Scan Number']">
        <xsl:element name="mods:identifier">
          <xsl:attribute name="type">
            <xsl:value-of select="'Scan number'" />
          </xsl:attribute>
          <xsl:attribute name="displayLabel">Scan Number</xsl:attribute>
          <xsl:value-of select="field[@name='Scan Number']/value" />
        </xsl:element>
      </xsl:when>
    </xsl:choose>

    <!-- DSM Nummer-->
    <xsl:if test="field[@name='DSM Nummer']">
      <xsl:element name="mods:identifier">
        <xsl:attribute name="type">
          <xsl:value-of select="'DSM Nummer'" />
        </xsl:attribute>
        <xsl:attribute name="displayLabel">DSM Number</xsl:attribute>
        <xsl:value-of select="field[@name='DSM Nummer']/value" />
      </xsl:element>
    </xsl:if>

  </xsl:template>

  <!-- END identifiers -->

  <!-- START language -->
  <xsl:template name="mods_language">
    <xsl:if test="field[@name='Udgivelsessprog'] or field[@name='Language'] or field[@name='Languages']">
      <xsl:element name="mods:language">
        <!-- Udgivelsessprog eller Language -->
        <xsl:choose>
          <xsl:when test="field[@name='Udgivelsessprog']">
            <xsl:for-each select="field[@name='Udgivelsessprog']/value">
              <xsl:element name="mods:languageTerm">
                <xsl:attribute name="authority">
                  <xsl:value-of select="'rfc4646'" />
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:value-of select="'code'" />
                </xsl:attribute>
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Language']">
            <xsl:for-each select="field[@name='Language']/value">
              <xsl:element name="mods:languageTerm">
                <xsl:attribute name="authority">
                  <xsl:value-of select="'rfc4646'" />
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:value-of select="'code'" />
                </xsl:attribute>
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>

        <!-- Language, additional -->
        <xsl:if test="field[@name='Language, additional']">
          <xsl:element name="mods:languageTerm">
            <xsl:attribute name="authority">
              <xsl:value-of select="'rfc4646'" />
            </xsl:attribute>
            <xsl:attribute name="type">
              <xsl:value-of select="'code'" />
            </xsl:attribute>
            <xsl:attribute name="transliteration">
              <xsl:value-of select="'Language, additional'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Language, additional']/value" />
          </xsl:element>
        </xsl:if>

        <!-- Languages -->
        <xsl:if test="field[@name='Languages']">
          <xsl:element name="mods:languageTerm">
            <xsl:attribute name="authority">
              <xsl:value-of select="'rfc4646'" />
            </xsl:attribute>
            <xsl:attribute name="type">
              <xsl:value-of select="'code'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Languages']/value" />
          </xsl:element>
        </xsl:if>
      </xsl:element>
    </xsl:if>

  </xsl:template>
  <!-- END language -->

  <!-- START location -->
  <xsl:template name="mods_location">
    <xsl:if test="field[@name='Opstilling'] or field[@name='Shelfmark']">
      <xsl:element name="mods:location">
        <!-- Opstilling || Shelfmark -->
        <xsl:choose>
          <xsl:when test="field[@name='Opstilling']">
            <xsl:for-each select="field[@name='Opstilling']/value">
              <xsl:element name="mods:shelfLocator">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Shelfmark']">
            <xsl:for-each select="field[@name='Shelfmark']/value">
              <xsl:element name="mods:shelfLocator">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>
  </xsl:template>
  <!-- END location -->

  <!-- START name -->
  <xsl:template name="mods_name">
    <!-- Ophav || Creator
         (both with either "@Ophav rolle" or 'Ophav' & 'Creator' as role)-->
    <!--Only included if Catalog is Luftfoto -->
    <xsl:if test="contains(field[@name='Catalog Name']/value, 'Luftfoto OM')">
      <xsl:if test="field[@name='Ophav'] or field[@name='Creator']">
        <xsl:element name="mods:name">
          <xsl:attribute name="type">
            <xsl:value-of select="'personal'" />
          </xsl:attribute>
          <xsl:choose>
            <xsl:when test="field[@name='Ophav']">
              <xsl:for-each select="field[@name='Ophav']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:element name="mods:namePart">
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:for-each>
            </xsl:when>
            <xsl:when test="field[@name='Creator']">
              <xsl:for-each select="field[@name='Creator']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:element name="mods:namePart">
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:for-each>
            </xsl:when>
          </xsl:choose>
          <xsl:element name="mods:role">
            <xsl:choose>
              <xsl:when test="field[@name='Ophav rolle']">
                <xsl:element name="mods:roleTerm">
                  <xsl:attribute name="type">
                    <xsl:value-of select="'text'" />
                  </xsl:attribute>
                  <xsl:value-of select="field[@name='Ophav rolle']/value" />
                </xsl:element>
              </xsl:when>
              <xsl:otherwise>
                <xsl:element name="mods:roleTerm">
                  <xsl:attribute name="type">
                    <xsl:value-of select="'text'" />
                  </xsl:attribute>
                  <xsl:attribute name="lang">
                    <xsl:value-of select="'da'" />
                  </xsl:attribute>
                  <xsl:value-of select="'Ophav'" />
                </xsl:element>
                <xsl:element name="mods:roleTerm">
                  <xsl:attribute name="type">
                    <xsl:value-of select="'text'" />
                  </xsl:attribute>
                  <xsl:attribute name="lang">
                    <xsl:value-of select="'en'" />
                  </xsl:attribute>
                  <xsl:value-of select="'Creator'" />
                </xsl:element>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:element>
        </xsl:element>
      </xsl:if>
    </xsl:if>

    <!--todo: to be removed: Country (location) of recipient || Modtager, land
         (with both 'Country (location) of recipient' and 'Modtager, land' as role)-->
<!--    <xsl:if test="field[@name='Country (location) of recipient'] or field[@name='Modtager, land']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Country (location) of recipient']">-->
<!--            <xsl:for-each select="field[@name='Country (location) of recipient']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Modtager, land']">-->
<!--            <xsl:for-each select="field[@name='Modtager, land']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Country (location) of recipient'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Modtager, land'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- todo: to be removed: Location of recipient || Modtager, sted
         (with both 'Location of recipient' and 'Modtager, sted' as role)-->
<!--    <xsl:if test="field[@name='Location of recipient'] or field[@name='Modtager, sted']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Location of recipient']">-->
<!--            <xsl:for-each select="field[@name='Location of recipient']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Modtager, sted']">-->
<!--            <xsl:for-each select="field[@name='Modtager, sted']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Location of recipient'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Modtager, sted'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!--todo: to be removed:  Additional sender (personal) || Medafsender
         (with both 'Additional sender (personal)' and 'Medafsender' as role)-->
<!--    <xsl:if test="field[@name='Additional sender (personal)'] or field[@name='Medafsender']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Additional sender (personal)']">-->
<!--            <xsl:for-each select="field[@name='Additional sender (personal)']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Medafsender']">-->
<!--            <xsl:for-each select="field[@name='Medafsender']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Additional sender (personal)'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Medafsender'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Author || Forfatter todo: remove
         (with both 'Author' and 'Forfatter' as role)-->
<!--    <xsl:if test="field[@name='Author'] or field[@name='Forfatter']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Author']">-->
<!--            <xsl:for-each select="field[@name='Author']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Forfatter']">-->
<!--            <xsl:for-each select="field[@name='Forfatter']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Author'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Forfatter'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Contributor || Bidragsyder todo: to be removed
         (with both 'Contributor' and 'Bidragsyder' as role)-->
<!--    <xsl:if test="field[@name='Contributor'] or field[@name='Bidragsyder']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Contributor']">-->
<!--            <xsl:for-each select="field[@name='Contributor']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Bidragsyder']">-->
<!--            <xsl:for-each select="field[@name='Bidragsyder']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Contributor'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Bidragsyder'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Organisation todo: to be removed-->
<!--    <xsl:for-each select="field[@name='Organisation']/value">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'corporate'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:element name="mods:namePart">-->
<!--          <xsl:call-template name="cumulus_get_value" />-->
<!--        </xsl:element>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Organisation'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:for-each>-->

    <!-- Patron || Mæcen todo: to be removed
         (with both 'Patron' and 'Mæcen' as role)-->
<!--    <xsl:if test="field[@name='Patron'] or field[@name='Mæcen']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Patron']">-->
<!--            <xsl:for-each select="field[@name='Patron']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Mæcen']">-->
<!--            <xsl:for-each select="field[@name='Mæcen']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Patron'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Mæcen'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Printer || Trykker todo: to be removed
         (with both 'Printer' and 'Trykker' as role)-->
<!--    <xsl:if test="field[@name='Printer'] or field[@name='Trykker']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Printer']">-->
<!--            <xsl:for-each select="field[@name='Printer']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Trykker']">-->
<!--            <xsl:for-each select="field[@name='Trykker']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Printer'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Trykker'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Publisher || Udgiver todo: to be removed
         (both with either "@Udgiver rolle" or 'Publisher' & 'Udgiver' as role)-->
<!--    <xsl:if test="field[@name='Publisher'] or field[@name='Udgiver']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Publisher']">-->
<!--            <xsl:for-each select="field[@name='Publisher']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Udgiver']">-->
<!--            <xsl:for-each select="field[@name='Udgiver']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:choose>-->
<!--            <xsl:when test="field[@name='Udgiver rolle']">-->
<!--              <xsl:element name="mods:roleTerm">-->
<!--                <xsl:attribute name="type">-->
<!--                  <xsl:value-of select="'text'" />-->
<!--                </xsl:attribute>-->
<!--                <xsl:value-of select="field[@name='Udgiver rolle']/value" />-->
<!--              </xsl:element>-->
<!--            </xsl:when>-->
<!--            <xsl:otherwise>-->
<!--              <xsl:element name="mods:roleTerm">-->
<!--                <xsl:attribute name="type">-->
<!--                  <xsl:value-of select="'text'" />-->
<!--                </xsl:attribute>-->
<!--                <xsl:attribute name="lang">-->
<!--                  <xsl:value-of select="'en'" />-->
<!--                </xsl:attribute>-->
<!--                <xsl:value-of select="'Publisher'" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:roleTerm">-->
<!--                <xsl:attribute name="type">-->
<!--                  <xsl:value-of select="'text'" />-->
<!--                </xsl:attribute>-->
<!--                <xsl:attribute name="lang">-->
<!--                  <xsl:value-of select="'da'" />-->
<!--                </xsl:attribute>-->
<!--                <xsl:value-of select="'Udgiver'" />-->
<!--              </xsl:element>-->
<!--            </xsl:otherwise>-->
<!--          </xsl:choose>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Scribe || Skriver todo: to be removed
         (with both 'Scribe' and 'Skriver' as role)-->
<!--    <xsl:if test="field[@name='Scribe'] or field[@name='Skriver']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Scribe']">-->
<!--            <xsl:for-each select="field[@name='Scribe']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Skriver']">-->
<!--            <xsl:for-each select="field[@name='Skriver']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Scribe'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Skriver'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->


    <!--todo: to be removed (altRepGroup mm. skal i ophav-tabel): Sender (organization) || Afsender, Organisation -->
<!--    <xsl:if test="field[@name='Sender (organization)'] or field[@name='Afsender, Organisation']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="altRepGroup">aut2</xsl:attribute>-->
<!--        <xsl:attribute name="displayLabel">Sender</xsl:attribute>-->
<!--        <xsl:attribute name="type">corporate</xsl:attribute>-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Sender (organization)']">-->
<!--            <xsl:for-each select="field[@name='Sender (organization)']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'aut'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Afsender, Organisation']">-->
<!--            <xsl:for-each select="field[@name='Afsender, Organisation']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'aut'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--      </xsl:element> &lt;!&ndash; mods:name &ndash;&gt;-->
<!--      <xsl:element name="mods:subject">-->
<!--        <xsl:attribute name="altRepGroup">aut2</xsl:attribute>-->
<!--        <xsl:element name="mods:hierarchicalGeographic">-->
<!--          <xsl:element name="mods:city">-->
<!--            <xsl:value-of select="field[@name='Location of sender']/value" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:country">-->
<!--            <xsl:value-of select="field[@name='Country (location) of sender']/value" />-->
<!--          </xsl:element>-->
<!--        </xsl:element> &lt;!&ndash;mods:hierarchicalGeographic &ndash;&gt;-->
<!--      </xsl:element> &lt;!&ndash; mods:subject &ndash;&gt;-->
<!--    </xsl:if>-->

    <!-- todo: to be removed (altRepGroup mm. skal i ophav-tabel) Sender (person) || Afsender -->
<!--    <xsl:if test="field[@name='Sender (person)'] or field[@name='Afsender']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="altRepGroup">aut1</xsl:attribute>-->
<!--        <xsl:attribute name="displayLabel">Sender</xsl:attribute>-->
<!--        <xsl:attribute name="type">personal</xsl:attribute>-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Sender (person)']">-->
<!--            <xsl:for-each select="field[@name='Sender (person)']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'aut'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Afsender']">-->
<!--            <xsl:for-each select="field[@name='Afsender']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'aut'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--      </xsl:element> &lt;!&ndash; mods:name &ndash;&gt;-->
<!--      <xsl:element name="mods:subject">-->
<!--        <xsl:attribute name="altRepGroup">aut1</xsl:attribute>-->
<!--        <xsl:element name="mods:hierarchicalGeographic">-->
<!--          <xsl:element name="mods:city">-->
<!--            <xsl:value-of select="field[@name='Location of sender']/value" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:country">-->
<!--            <xsl:value-of select="field[@name='Country (location) of sender']/value" />-->
<!--          </xsl:element>-->
<!--        </xsl:element> &lt;!&ndash;mods:hierarchicalGeographic &ndash;&gt;-->
<!--      </xsl:element> &lt;!&ndash; mods:subject &ndash;&gt;-->
<!--    </xsl:if>-->

    <!-- Translator || Oversætter todo: to be removed
         (with both 'Translator' and 'Oversætter' as role)-->
<!--    <xsl:if test="field[@name='Translator'] or field[@name='Oversætter']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Translator']">-->
<!--            <xsl:for-each select="field[@name='Translator']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Oversætter']">-->
<!--            <xsl:for-each select="field[@name='Oversætter']/value">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'en'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Translator'" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:attribute name="lang">-->
<!--              <xsl:value-of select="'da'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:value-of select="'Oversætter'" />-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!--todo: to be removed (altRepGroup mm. skal i person-tabel) Recipient || Recipient (personal) || Modtager -->
<!--    <xsl:if test="field[@name='Recipient'] or field[@name='Recipient (personal)'] or field[@name='Modtager']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="altRepGroup">rcp1</xsl:attribute>-->
<!--        <xsl:attribute name="displayLabel">Recipient</xsl:attribute>-->
<!--        <xsl:attribute name="type">personal</xsl:attribute>-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Recipient']">-->
<!--            <xsl:for-each select="field[@name='Recipient']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'rcp'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Recipient (personal)']">-->
<!--            <xsl:for-each select="field[@name='Recipient (personal)']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'rcp'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Modtager']">-->
<!--            <xsl:for-each select="field[@name='Modtager']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'rcp'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--      </xsl:element> &lt;!&ndash; mods:name &ndash;&gt;-->
<!--      <xsl:element name="mods:subject">-->
<!--        <xsl:attribute name="altRepGroup">rcp1</xsl:attribute>-->
<!--        <xsl:element name="mods:hierarchicalGeographic">-->
<!--          <xsl:element name="mods:city">-->
<!--            <xsl:value-of select="field[@name='Location of recipient']/value" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:country">-->
<!--            <xsl:value-of select="field[@name='Country (location) of recipient']/value" />-->
<!--          </xsl:element>-->
<!--        </xsl:element> &lt;!&ndash;mods:hierarchicalGeographic &ndash;&gt;-->
<!--      </xsl:element> &lt;!&ndash; mods:subject &ndash;&gt;-->
<!--    </xsl:if>-->

    <!--todo: to be removed (altRepGroup mm. skal i person-tabel) Recipient (organization) || Modtager, Organisation -->
<!--    <xsl:if test="field[@name='Recipient (organization)'] or field[@name='Modtager, Organisation']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="altRepGroup">rcp2</xsl:attribute>-->
<!--        <xsl:attribute name="displayLabel">Recipient</xsl:attribute>-->
<!--        <xsl:attribute name="type">corporate</xsl:attribute>-->
<!--        <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Recipient (organization)']">-->
<!--            <xsl:for-each select="field[@name='Recipient (organization)']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'rcp'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Modtager, Organisation']">-->
<!--            <xsl:for-each select="field[@name='Modtager, Organisation']/value">-->
<!--              <xsl:element name="mods:namePart">-->
<!--                <xsl:call-template name="cumulus_get_value" />-->
<!--              </xsl:element>-->
<!--              <xsl:element name="mods:role">-->
<!--                <xsl:element name="mods:roleTerm">-->
<!--                  <xsl:attribute name="type">code</xsl:attribute>-->
<!--                  <xsl:value-of select="'rcp'"/>-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--      </xsl:element> &lt;!&ndash; mods:name &ndash;&gt;-->
<!--      <xsl:element name="mods:subject">-->
<!--        <xsl:attribute name="altRepGroup">rcp2</xsl:attribute>-->
<!--        <xsl:element name="mods:hierarchicalGeographic">-->
<!--          <xsl:element name="mods:city">-->
<!--            <xsl:value-of select="field[@name='Location of recipient']/value" />-->
<!--          </xsl:element>-->
<!--          <xsl:element name="mods:country">-->
<!--            <xsl:value-of select="field[@name='Country (location) of recipient']/value" />-->
<!--          </xsl:element>-->
<!--        </xsl:element> &lt;!&ndash;mods:hierarchicalGeographic &ndash;&gt;-->
<!--      </xsl:element> &lt;!&ndash; mods:subject &ndash;&gt;-->
<!--    </xsl:if>-->

    <!-- Plademærke -->
    <xsl:for-each select="field[@name='Plademærke']/value">
      <xsl:element name="mods:name">
        <xsl:attribute name="type">
          <xsl:value-of select="'corporate'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:element name="mods:namePart">
          <xsl:call-template name="cumulus_get_value" />
        </xsl:element>
        <xsl:element name="mods:role">
          <xsl:element name="mods:roleTerm">
            <xsl:value-of select="'Plademaerke'" />
            <xsl:attribute name="type">
              <xsl:value-of select="'text'" />
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <!-- Medvirkende todo: to be removed-->
<!--    <xsl:if test="field[@name='Medvirkende']">-->
<!--      <xsl:element name="mods:name">-->
<!--        <xsl:attribute name="type">-->
<!--          <xsl:value-of select="'personal'" />-->
<!--        </xsl:attribute>-->
<!--        <xsl:for-each select="field[@name='Medvirkende']/value">-->
<!--          <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--          <xsl:element name="mods:namePart">-->
<!--            <xsl:call-template name="cumulus_get_value" />-->
<!--          </xsl:element>-->
<!--        </xsl:for-each>-->
<!--        <xsl:element name="mods:role">-->
<!--          <xsl:element name="mods:roleTerm">-->
<!--            <xsl:attribute name="type">-->
<!--              <xsl:value-of select="'text'" />-->
<!--            </xsl:attribute>-->
<!--            <xsl:choose>-->
<!--              <xsl:when test="field[@name='Medvirkende rolle']">-->
<!--                <xsl:value-of select="field[@name='Medvirkende rolle']/value" />-->
<!--              </xsl:when>-->
<!--              <xsl:otherwise>-->
<!--                <xsl:value-of select="'Medvirkende'" />-->
<!--              </xsl:otherwise>-->
<!--            </xsl:choose>-->
<!--          </xsl:element>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- Crowd_Person moved to subject-->

  </xsl:template>
  <!-- END name -->

  <!-- START name ophav -->
  <xsl:template name="mods_name_ophav">
    <!-- Ophav Tabel -->
    <xsl:for-each select="field[@name='Ophav-tabel']/table/row">
      <xsl:call-template name="mods_name_ophav_person">
        <xsl:with-param name="tabel" select = "'ophav'" />
      </xsl:call-template>
    </xsl:for-each>

  </xsl:template>
  <!-- END name ophav -->

  <!-- START name ophav or person-->
  <xsl:template name="mods_name_ophav_person">
  <!-- Ophav or Person Tabel -->
    <xsl:param name = "tabel" />
    <!-- <xsl:value-of select = "$tabel" /> -->

    <!--START specific roles handling-->
      <!--START personal-->
    <xsl:if test="(contains(field[@name='Rolle']/value, 'afsender') or
    contains(field[@name='Rolle']/value, 'supplerende afsender')) and
    not(field[@name='Kooperation']/value)">
      <xsl:element name="mods:name">
        <xsl:attribute name="altRepGroup">aut1</xsl:attribute>
        <xsl:attribute name="displayLabel">Sender</xsl:attribute>
        <xsl:attribute name="type">personal</xsl:attribute>
        <xsl:element name="mods:role">
          <xsl:element name="mods:roleTerm">
            <xsl:attribute name="type">
              <xsl:value-of select="'code'" />
            </xsl:attribute>
            <xsl:value-of select="'aut'" />
          </xsl:element>
        </xsl:element>
        <xsl:if test="field[@name='Efternavn']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'family'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Efternavn']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Fornavn']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'given'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Fornavn']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Andet navn']/value">
          <xsl:element name="mods:alternativeName">
            <xsl:attribute name="altType">
              <xsl:value-of select="'nickname'" />
            </xsl:attribute>
            <xsl:element name="mods:namePart">
              <xsl:for-each select="field[@name='Andet navn']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:for-each>
            </xsl:element>
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'date'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Født']/value" />
            <xsl:value-of select="'/'" />
            <xsl:value-of select="field[@name='Død']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Nationalitet']/value">
          <xsl:element name="mods:description">
            <xsl:value-of select="field[@name='Nationalitet']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Titel']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'termsOfAddress'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Titel']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="contains($tabel, 'person')">
          <xsl:if test="field[@name='Kooperation']/value">
            <xsl:element name="mods:affiliation">
              <xsl:value-of select="field[@name='Kooperation']/value" />
            </xsl:element>
          </xsl:if>
        </xsl:if>
      </xsl:element> <!--mods:name-->
      <xsl:if test="../../../field[@name='Location of sender'] or ../../../field[@name='Country (location) of sender']">
        <xsl:element name="mods:subject">
          <xsl:attribute name="altRepGroup">aut1</xsl:attribute>
          <xsl:element name="mods:hierarchicalGeographic">
            <xsl:if test="../../../field[@name='Location of sender']">
              <xsl:element name="mods:city">
                <xsl:value-of select="../../../field[@name='Location of sender']/value" />
              </xsl:element>
            </xsl:if>
            <xsl:if test="../../../field[@name='Country (location) of sender']">
              <xsl:element name="mods:country">
                <xsl:value-of select="../../../field[@name='Country (location) of sender']/value" />
              </xsl:element>
            </xsl:if>
          </xsl:element> <!--mods:hierarchicalGeographic -->
        </xsl:element> <!-- mods:subject -->
      </xsl:if>
    </xsl:if>
      <!--END personal-->

      <!--START Kooperation -->
    <xsl:if test="(contains(field[@name='Rolle']/value, 'afsender') or
    contains(field[@name='Rolle']/value, 'supplerende afsender')) and
    field[@name='Kooperation']/value">
      <xsl:element name="mods:name">
        <xsl:attribute name="altRepGroup">aut2</xsl:attribute>
        <xsl:attribute name="displayLabel">Sender</xsl:attribute>
        <xsl:attribute name="type">personal</xsl:attribute>
        <xsl:element name="mods:role">
          <xsl:element name="mods:roleTerm">
            <xsl:attribute name="type">
              <xsl:value-of select="'code'" />
            </xsl:attribute>
            <xsl:value-of select="'aut'" />
          </xsl:element>
        </xsl:element>
        <xsl:if test="field[@name='Efternavn']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'family'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Efternavn']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Fornavn']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'given'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Fornavn']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Andet navn']/value">
          <xsl:element name="mods:alternativeName">
            <xsl:attribute name="altType">
              <xsl:value-of select="'nickname'" />
            </xsl:attribute>
            <xsl:element name="mods:namePart">
              <xsl:for-each select="field[@name='Andet navn']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:for-each>
            </xsl:element>
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'date'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Født']/value" />
            <xsl:value-of select="'/'" />
            <xsl:value-of select="field[@name='Død']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Nationalitet']/value">
          <xsl:element name="mods:description">
            <xsl:value-of select="field[@name='Nationalitet']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Titel']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'termsOfAddress'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Titel']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="contains($tabel, 'person')">
          <xsl:if test="field[@name='Kooperation']/value">
            <xsl:element name="mods:affiliation">
              <xsl:value-of select="field[@name='Kooperation']/value" />
            </xsl:element>
          </xsl:if>
        </xsl:if>
      </xsl:element>
      <xsl:if test="../../../field[@name='Location of sender'] or ../../../field[@name='Country (location) of sender']">
        <xsl:element name="mods:subject">
          <xsl:attribute name="altRepGroup">aut2</xsl:attribute>
          <xsl:element name="mods:hierarchicalGeographic">
            <xsl:if test="../../../field[@name='Location of sender']">
              <xsl:element name="mods:city">
                <xsl:value-of select="../../../field[@name='Location of sender']/value" />
              </xsl:element>
            </xsl:if>
            <xsl:if test="../../../field[@name='Country (location) of sender']">
              <xsl:element name="mods:country">
                <xsl:value-of select="../../../field[@name='Country (location) of sender']/value" />
              </xsl:element>
            </xsl:if>
            </xsl:element> <!-- mods:hierarchicalGeographic -->
          </xsl:element> <!-- mods:subject -->
      </xsl:if>
    </xsl:if>
      <!--END kooperation-->
    <!--END specific roles handling -->

    <xsl:if test="not(contains(field[@name='Rolle']/value, 'afsender')) and
    not(contains(field[@name='Rolle']/value, 'supplerende afsender')) ">
    <xsl:element name="mods:name">
      <xsl:attribute name="type">
        <xsl:value-of select="'personal'" />
      </xsl:attribute>
      <xsl:if test="contains($tabel, 'ophav')">
        <xsl:element name="mods:role">
          <xsl:element name="mods:roleTerm">
            <xsl:attribute name="type">
              <xsl:value-of select="'code'" />
            </xsl:attribute>
            <xsl:value-of select="'cre'" />
          </xsl:element>
        </xsl:element>
      </xsl:if>
      <xsl:if test="field[@name='Efternavn']/value">
        <xsl:element name="mods:namePart">
          <xsl:attribute name="type">
            <xsl:value-of select="'family'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Efternavn']/value" />
        </xsl:element>
      </xsl:if>
      <xsl:if test="field[@name='Fornavn']/value">
        <xsl:element name="mods:namePart">
          <xsl:attribute name="type">
            <xsl:value-of select="'given'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Fornavn']/value" />
        </xsl:element>
      </xsl:if>
      <xsl:if test="field[@name='Andet navn']/value">
        <xsl:element name="mods:alternativeName">
          <xsl:attribute name="altType">
            <xsl:value-of select="'nickname'" />
          </xsl:attribute>
          <xsl:element name="mods:namePart">
            <xsl:for-each select="field[@name='Andet navn']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:element>
        </xsl:element>
      </xsl:if>
      <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
        <xsl:element name="mods:namePart">
          <xsl:attribute name="type">
            <xsl:value-of select="'date'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Født']/value" />
          <xsl:value-of select="'/'" />
          <xsl:value-of select="field[@name='Død']/value" />
        </xsl:element>
      </xsl:if>
      <xsl:if test="field[@name='Nationalitet']/value">
        <xsl:element name="mods:description">
          <xsl:value-of select="field[@name='Nationalitet']/value" />
        </xsl:element>
      </xsl:if>
      <xsl:if test="field[@name='Titel']/value">
        <xsl:element name="mods:namePart">
          <xsl:attribute name="type">
            <xsl:value-of select="'termsOfAddress'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='Titel']/value" />
        </xsl:element>
      </xsl:if>
      <xsl:if test="contains($tabel, 'person')">
        <xsl:if test="field[@name='Kooperation']/value">
          <xsl:element name="mods:affiliation">
            <xsl:value-of select="field[@name='Kooperation']/value" />
          </xsl:element>
        </xsl:if>
      </xsl:if>
    </xsl:element>
    </xsl:if>

  </xsl:template>
  <!-- END name ophav or person -->
  <!-- END name person -->

  <!-- START accessCondition (only for MODS:RIGHTS)-->
  <xsl:template name="cdl_accessCondition">
      <!-- cdl:ophav -->
      <xsl:call-template name="cdl_ophav" />

  </xsl:template>

  <!-- START name ophav -->
  <xsl:template name="cdl_ophav">
    <!-- Ophav Tabel -->
    <xsl:for-each select="field[@name='Ophav-tabel']/table/row">
      <xsl:call-template name="cdl_ophav_person">
        <xsl:with-param name="tabel" select = "'ophav'" />
      </xsl:call-template>
    </xsl:for-each>

  </xsl:template>
  <!-- END name ophav -->


  <!-- START name ophav or person-->
  <xsl:template name="cdl_ophav_person">
  <!-- Ophav or Person Tabel -->
    <xsl:param name = "tabel" />
    <!-- <xsl:value-of select = "$tabel" /> -->
    <xsl:if test="field[@name='Efternavn']/value or field[@name='Fornavn']/value">
      <xsl:element name="cdl:creator">
        <xsl:element name="cdl:creator.person">
            <xsl:element name="cdl:name">
              <xsl:value-of select="field[@name='Efternavn']/value" />
              <xsl:value-of select="', '" />
              <xsl:value-of select="field[@name='Fornavn']/value" />
            </xsl:element>
          <xsl:if test="field[@name='Født']/value">
            <xsl:element name="cdl:year.birth">
              <xsl:value-of select="field[@name='Født']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Død']/value">
            <xsl:element name="cdl:year.death">
              <xsl:value-of select="field[@name='Død']/value" />
            </xsl:element>
          </xsl:if>
        </xsl:element>
      </xsl:element>
    </xsl:if>
    <xsl:if test="field[@name='Kooperation']/value">
      <xsl:element name="cdl:creator">
        <xsl:element name="dk:creator.corporate">
          <xsl:if test="contains($tabel, 'ophav')">
            <xsl:element name="cdl:name">
              <xsl:value-of select="field[@name='Kooperation']/value" />
            </xsl:element>
          </xsl:if>
        <xsl:if test="field[@name='Start år']/value">
          <xsl:element name="dk:year.started">
            <xsl:value-of select="field[@name='Start år']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Slut år']/value">
          <xsl:element name="dk:year.ended">
            <xsl:value-of select="field[@name='Slut år']/value" />
          </xsl:element>
        </xsl:if>
        </xsl:element>
      </xsl:element>
    </xsl:if>
  </xsl:template>
  <!-- END name ophav or person -->
  <!-- END accessCondition -->

  <!-- START note -->
  <xsl:template name="mods_note">
    <!-- Note -->
    <xsl:for-each select="field[@name='Note']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'content'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Addiontional resources || Henvisninger -->
    <xsl:choose>
      <xsl:when test="field[@name='Additional resources']">
        <xsl:for-each select="field[@name='Additional resources']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'Additional resources'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="field[@name='Henvisninger']">
        <xsl:for-each select="field[@name='Henvisninger']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'Henvisninger'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Dialog eller Dialogue -->
    <xsl:choose>
      <xsl:when test="field[@name='Dialog']/value">
        <xsl:for-each select="field[@name='Dialog']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'Dialog'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
         </xsl:element>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="field[@name='Dialogue']/value">
        <xsl:for-each select="field[@name='Dialogue']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'Dialogue'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Extent || Omfang -->
    <xsl:choose>
      <xsl:when test="field[@name='Extent']">
        <xsl:for-each select="field[@name='Extent']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'Extent'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="field[@name='Omfang']">
        <xsl:for-each select="field[@name='Omfang']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'Omfang'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Kommentar til indhold -->
    <xsl:for-each select="field[@name='Kommentar til indhold']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'Kommentar til indhold'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Selected references -->
    <xsl:for-each select="field[@name='Selected references']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'citation/reference'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Undertekst -->
    <xsl:for-each select="field[@name='Undertekst']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'Undertekst'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Situation -->
    <xsl:for-each select="field[@name='Situation']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'Situation'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Pladenummer -->
    <xsl:for-each select="field[@name='Pladenummer']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'Pladenummer'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Intern note -->
    <xsl:for-each select="field[@name='Intern note']/value">
      <xsl:element name="mods:note">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:attribute name="type">
          <xsl:value-of select="'Intern note'" />
        </xsl:attribute>
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>

    <!-- Crowd_Note -->
    <xsl:if test="field[@name='Crowd_Note']">
      <xsl:element name="mods:note">
        <xsl:attribute name="altRepGroup">
          <xsl:value-of select="'crowd'" />
        </xsl:attribute>
        <xsl:attribute name="type">
          <xsl:value-of select="'note'" />
        </xsl:attribute>
        <xsl:value-of select="field[@name='Crowd_Note']/value" />
      </xsl:element>
    </xsl:if>

    <!-- Crowd_Kommentar -->
    <xsl:if test="field[@name='Crowd_Kommentar']">
      <xsl:element name="mods:note">
        <xsl:attribute name="altRepGroup">
          <xsl:value-of select="'crowd'" />
        </xsl:attribute>
        <xsl:attribute name="type">
          <xsl:value-of select="'kommentar'" />
        </xsl:attribute>
        <xsl:value-of select="field[@name='Crowd_Kommentar']/value" />
      </xsl:element>
    </xsl:if>

    <!-- Beskrivelse -->
    <xsl:if test="field[@name='Beskrivelse']  or field[@name='Description']">
      <xsl:element name="mods:note">
        <xsl:attribute name="displayLabel">Description</xsl:attribute>
        <xsl:choose>
          <xsl:when test="field[@name='Beskrivelse']">
            <xsl:for-each select="field[@name='Beskrivelse']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Description']">
            <xsl:for-each select="field[@name='Description']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- IPTC Descripiton -->
    <xsl:if test="field[@name='IPTC Description']">
      <xsl:element name="mods:note">
        <xsl:attribute name="displayLabel">Description</xsl:attribute>
        <xsl:for-each select="field[@name='IPTC Description']/value">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:call-template name="cumulus_get_value" />
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Hjemtaget som-->
    <xsl:if test="field[@name='Hjemtaget som:']">
      <xsl:element name="mods:note">
        <xsl:attribute name="type">
          <xsl:value-of select="'provenance'" />
        </xsl:attribute>
        <xsl:attribute name="displayLabel">Incorporated as</xsl:attribute>
        <xsl:value-of select="field[@name='Hjemtaget som:']/value" />
      </xsl:element>
    </xsl:if>

    <!-- Hjemtaget af-->
    <xsl:if test="field[@name='Hjemtaget af:']">
      <xsl:element name="mods:note">
        <xsl:attribute name="type">
          <xsl:value-of select="'provenance'" />
        </xsl:attribute>
        <xsl:attribute name="displayLabel">Incorporated by</xsl:attribute>
        <xsl:value-of select="field[@name='Hjemtaget af:']/value" />
      </xsl:element>
    </xsl:if>

    <!-- Contents -->
    <xsl:if test="field[@name='Contents']">
      <xsl:for-each select="field[@name='Contents']/value">
        <xsl:element name="mods:note">
          <xsl:attribute name="type">
            <xsl:value-of select="'content'"/>
          </xsl:attribute>
          <xsl:call-template name="cumulus_get_lang_attribute"/>
          <xsl:call-template name="cumulus_get_value"/>
        </xsl:element>
      </xsl:for-each>
    </xsl:if>

    <!-- Catalog Name -->
    <xsl:element name="mods:note">
      <xsl:attribute name="displayLabel">Catalog Name</xsl:attribute>
      <xsl:value-of select="field[@name='Catalog Name']/value" />
    </xsl:element>

  </xsl:template>
  <!-- END note -->

  <!-- START originInfo -->
  <xsl:template name="mods_originInfo">
    <xsl:if test="field[@name='Udgivelsesland'] or field[@name='Country'] or
          field[@name='Country (location) of sender'] or
          field[@name='Udgivelsessted'] or field[@name='Location of origin'] or
          field[@name='Local Date'] or
          field[@name='Manual Date not after'] or
          field[@name='Manual Date not before'] or
          field[@name='Origin not after'] or field[@name='Origin not before'] or
          field[@name='Presentation Date'] or
          field[@name='Country of origin'] or
          field[@name='Place of origin']">

      <xsl:element name="mods:originInfo">
        <xsl:if test="field[@name='Udgivelsesland'] or field[@name='Country'] or
          field[@name='Udgivelsessted'] or
          field[@name='Country (location) of sender'] or
          field[@name='Location of origin'] or
          field[@name='Country of origin'] or
          field[@name='Place of origin']">
          <xsl:element name="mods:place">
            <!-- Udgivelsesland || Country || Country (location) of sender -->
            <xsl:choose>
              <xsl:when test="field[@name='Udgivelsesland']">
                <xsl:for-each select="field[@name='Udgivelsesland']/value">
                  <xsl:element name="mods:placeTerm">
                    <xsl:attribute name="type">
                      <xsl:value-of select="'text'" />
                    </xsl:attribute>
                    <xsl:call-template name="cumulus_get_lang_attribute" />
                    <xsl:call-template name="cumulus_get_value" />
                  </xsl:element>
                </xsl:for-each>
              </xsl:when>
              <xsl:when test="field[@name='Country']">
                <xsl:for-each select="field[@name='Country']/value">
                  <xsl:element name="mods:placeTerm">
                    <xsl:attribute name="type">
                      <xsl:value-of select="'text'" />
                    </xsl:attribute>
                    <xsl:call-template name="cumulus_get_lang_attribute" />
                    <xsl:call-template name="cumulus_get_value" />
                  </xsl:element>
                </xsl:for-each>
              </xsl:when>
<!--              <xsl:when test="field[@name='Country (location) of sender']"> todo: to be removed. Moved to ophav-tabel-->
<!--                <xsl:for-each select="field[@name='Country (location) of sender']/value">-->
<!--                  <xsl:element name="mods:placeTerm">-->
<!--                    <xsl:attribute name="type">-->
<!--                      <xsl:value-of select="'code'" />-->
<!--                    </xsl:attribute>-->
<!--                    <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--                    <xsl:call-template name="cumulus_get_value" />-->
<!--                  </xsl:element>-->
<!--                </xsl:for-each>-->
<!--              </xsl:when>-->
              <xsl:when test="field[@name='Country of origin']">
                <xsl:for-each select="field[@name='Country of origin']/value">
                  <xsl:element name="mods:placeTerm">
                    <xsl:attribute name="type">
                      <xsl:value-of select="'text'" />
                    </xsl:attribute>
                    <xsl:call-template name="cumulus_get_lang_attribute" />
                    <xsl:call-template name="cumulus_get_value" />
                  </xsl:element>
                </xsl:for-each>
              </xsl:when>
            </xsl:choose>

            <!-- Udgivelsessted || Location of origin || Location of sender -->
            <xsl:choose>
              <xsl:when test="field[@name='Udgivelsessted']">
                <xsl:for-each select="field[@name='Udgivelsessted']/value">
                  <xsl:element name="mods:placeTerm">
                    <xsl:attribute name="type">
                      <xsl:value-of select="'text'" />
                    </xsl:attribute>
                    <xsl:call-template name="cumulus_get_lang_attribute" />
                    <xsl:call-template name="cumulus_get_value" />
                  </xsl:element>
                </xsl:for-each>
              </xsl:when>
              <xsl:when test="field[@name='Location of origin']">
                <xsl:for-each select="field[@name='Location of origin']/value">
                  <xsl:element name="mods:placeTerm">
                    <xsl:attribute name="type">
                      <xsl:value-of select="'text'" />
                    </xsl:attribute>
                    <xsl:call-template name="cumulus_get_lang_attribute" />
                    <xsl:call-template name="cumulus_get_value" />
                  </xsl:element>
                </xsl:for-each>
              </xsl:when>
<!--              <xsl:when test="field[@name='Location of sender']"> todo: to be removed. Moved to ophav-tabel-->
<!--                <xsl:for-each select="field[@name='Location of sender']/value">-->
<!--                  <xsl:element name="mods:placeTerm">-->
<!--                    <xsl:attribute name="type">-->
<!--                      <xsl:value-of select="'text'" />-->
<!--                    </xsl:attribute>-->
<!--                    <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--                    <xsl:call-template name="cumulus_get_value" />-->
<!--                  </xsl:element>-->
<!--                </xsl:for-each>-->
<!--              </xsl:when>-->
              <xsl:when test="field[@name='Place of origin']">
                <xsl:for-each select="field[@name='Place of origin']/value">
                  <xsl:element name="mods:placeTerm">
                    <xsl:attribute name="type">
                      <xsl:value-of select="'text'" />
                    </xsl:attribute>
                    <xsl:call-template name="cumulus_get_lang_attribute" />
                    <xsl:call-template name="cumulus_get_value" />
                  </xsl:element>
                </xsl:for-each>
              </xsl:when>
            </xsl:choose>

          </xsl:element>
        </xsl:if>

        <!-- Local Date -->
        <xsl:for-each select="field[@name='Local Date']/value">
          <xsl:element name="mods:dateOther">
            <xsl:attribute name="type">
              <xsl:value-of select="'Local Date'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <!-- Manual Date not after -->
        <xsl:for-each select="field[@name='Manual Date not after']/value">
          <xsl:element name="mods:dateOther">
            <xsl:attribute name="type">
              <xsl:value-of select="'Manual Date not after'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <!-- Manual Date not before -->
        <xsl:for-each select="field[@name='Manual Date not before']/value">
          <xsl:element name="mods:dateOther">
            <xsl:attribute name="type">
              <xsl:value-of select="'Manual Date not before'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <!-- Origin not after -->
        <xsl:for-each select="field[@name='Origin not after']/value">
          <xsl:element name="mods:dateOther">
            <xsl:attribute name="type">
              <xsl:value-of select="'Origin not after'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <!-- Origin not before -->
        <xsl:for-each select="field[@name='Origin not before']/value">
          <xsl:element name="mods:dateOther">
            <xsl:attribute name="type">
              <xsl:value-of select="'Origin not before'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <!-- Presentation Date -->
        <xsl:for-each select="field[@name='Presentation Date']/value">
          <xsl:element name="mods:dateOther">
            <xsl:attribute name="type">
              <xsl:value-of select="'Presentation Date'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

      </xsl:element>
    </xsl:if>

    <!-- Udgave -->
    <xsl:if test="field[@name='Udgave']">
      <xsl:element name="mods:originInfo">
        <xsl:element name="mods:edition">
          <xsl:value-of select="field[@name='Udgave']/value" />
        </xsl:element>
      </xsl:element>
    </xsl:if>

<!--    Start altRepGroup "original"-->
    <xsl:if test="field[@name='Date not after'] or field[@name='Dato ikke efter'] or
          field[@name='Date not before'] or field[@name='Dato ikke før'] or
          field[@name='År'] or field[@name='Date of Origin'] ">
      <xsl:element name="mods:originInfo">
      <xsl:attribute name="altRepGroup">original</xsl:attribute>
      <!-- År || Date of Origin -->
      <xsl:choose>
        <xsl:when test="field[@name='År']">
          <xsl:for-each select="field[@name='År']/value">
            <xsl:element name="mods:dateCreated">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:element>
          </xsl:for-each>
        </xsl:when>
        <xsl:when test="field[@name='Date of Origin']">
          <xsl:for-each select="field[@name='Date of Origin']/value">
            <xsl:element name="mods:dateCreated">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:element>
          </xsl:for-each>
        </xsl:when>
      </xsl:choose>

      <!-- Date not before || Dato ikke før -->
      <xsl:choose>
        <xsl:when test="field[@name='Date not before']">
          <xsl:for-each select="field[@name='Date not before']/value">
            <xsl:element name="mods:dateCreated">
              <xsl:attribute name="point">
                <xsl:value-of select="'start'" />
              </xsl:attribute>
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:element>
          </xsl:for-each>
        </xsl:when>
        <xsl:when test="field[@name='Dato ikke før']">
          <xsl:for-each select="field[@name='Dato ikke før']/value">
            <xsl:element name="mods:dateCreated">
              <xsl:attribute name="point">
                <xsl:value-of select="'start'" />
              </xsl:attribute>
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:element>
          </xsl:for-each>
        </xsl:when>
      </xsl:choose>

      <!-- Date not after || Dato ikke efter -->
      <xsl:choose>
        <xsl:when test="field[@name='Date not after']">
          <xsl:for-each select="field[@name='Date not after']/value">
            <xsl:element name="mods:dateCreated">
              <xsl:attribute name="point">
                <xsl:value-of select="'end'" />
              </xsl:attribute>
              <xsl:value-of select="." />
            </xsl:element>
          </xsl:for-each>
        </xsl:when>
        <xsl:when test="field[@name='Dato ikke efter']">
          <xsl:for-each select="field[@name='Dato ikke efter']/value">
            <xsl:element name="mods:dateCreated">
              <xsl:attribute name="point">
                <xsl:value-of select="'end'" />
              </xsl:attribute>
              <xsl:value-of select="." />
            </xsl:element>
          </xsl:for-each>
        </xsl:when>
      </xsl:choose>
    </xsl:element>
  </xsl:if>
  <!--        end altRepGroup "original"-->

<!--     Date Time Original || Captured Date, only one of them can be present?-->
    <xsl:if test="field[@name='Date Time Original'] and not(field[@name='Captured Date'])">
<!--    <xsl:if test="field[@name='Date Time Original'] ">-->
      <xsl:element name="mods:originInfo">
        <xsl:attribute name="altRepGroup">surrogate</xsl:attribute>
        <xsl:element name="mods:dateCaptured">
          <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
              'EEE MMM dd HH:mm:ss z yyy', field[@name='Date Time Original']/value)" />
        </xsl:element>
      </xsl:element>
    </xsl:if>
<!--    <xsl:if test="field[@name='Captured Date'] and not(field[@name='Date Time Original'])">-->
    <xsl:if test="field[@name='Captured Date'] ">
      <xsl:element name="mods:originInfo">
        <xsl:attribute name="altRepGroup">surrogate</xsl:attribute>
        <xsl:element name="mods:dateCaptured">
          <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
              'EEE MMM dd HH:mm:ss z yyy', field[@name='Captured Date']/value)" />
        </xsl:element>
      </xsl:element>
    </xsl:if>

  </xsl:template>
  <!-- END originInfo -->

  <!-- START part -->
  <xsl:template name="mods_part">
    <!-- NOTHING YET! -->
  </xsl:template>
  <!-- END part -->

  <!-- START physicalDescription -->
  <xsl:template name="mods_physicalDescription">
    <xsl:if test="field[@name='Script'] or field[@name='Skrifttype']
    or field[@name='Script: detail'] or field[@name='Skrifttype, detaljer']
    or field[@name='Størrelse'] or field[@name='Dimensions'] or field[@name='Size']
    or field[@name='Textarea'] or field[@name='Tekstområde'] or field[@name='State']
    or field[@name='Pageorientation'] or field[@name='Color Codes']
    or field[@name='Materialeteknik'] or field[@name='Publiceringsformat']
    or field[@name='Medium']">
      <xsl:element name="mods:physicalDescription">
        <!-- Script || Skrifttype -->
        <xsl:choose>
          <xsl:when test="field[@name='Script']">
            <xsl:for-each select="field[@name='Script']/value">
              <xsl:element name="mods:note">
                <xsl:attribute name="type">
                  <xsl:value-of select="'additional physical form'" />
                </xsl:attribute>
                <xsl:attribute name="displayLabel">
                  <xsl:value-of select="'Script'" />
                </xsl:attribute>
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Skrifttype']">
            <xsl:for-each select="field[@name='Skrifttype']/value">
              <xsl:element name="mods:note">
                <xsl:attribute name="type">
                  <xsl:value-of select="'additional physical form'" />
                </xsl:attribute>
                <xsl:attribute name="displayLabel">
                  <xsl:value-of select="'Skrifttype'" />
                </xsl:attribute>
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>

        <!-- Script: detail || Skrifttype, detaljer -->
        <xsl:choose>
          <xsl:when test="field[@name='Script: detail']">
            <xsl:for-each select="field[@name='Script: detail']/value">
              <xsl:element name="mods:note">
                <xsl:attribute name="type">
                  <xsl:value-of select="'additional physical form'" />
                </xsl:attribute>
                <xsl:attribute name="displayLabel">
                  <xsl:value-of select="'Script: detail'" />
                </xsl:attribute>
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Skrifttype, detaljer']">
            <xsl:for-each select="field[@name='Skrifttype, detaljer']/value">
              <xsl:element name="mods:note">
                <xsl:attribute name="type">
                  <xsl:value-of select="'additional physical form'" />
                </xsl:attribute>
                <xsl:attribute name="displayLabel">
                  <xsl:value-of select="'Skrifttype, detaljer'" />
                </xsl:attribute>
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>

        <!-- Størrelse || Dimensions -->
        <xsl:choose>
          <xsl:when test="field[@name='Størrelse']">
            <xsl:for-each select="field[@name='Størrelse']/value">
              <xsl:element name="mods:form">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:attribute name="type">Størrelse</xsl:attribute>
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Dimensions']">
            <xsl:for-each select="field[@name='Dimensions']/value">
              <xsl:element name="mods:form">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:attribute name="type">Dimensions</xsl:attribute>
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Size']">
            <xsl:for-each select="field[@name='Size']/value">
              <xsl:element name="mods:form">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:attribute name="type">Dimensions</xsl:attribute>
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>

        <!-- Textarea || Tekstområde -->
        <xsl:choose>
          <xsl:when test="field[@name='Textarea']">
            <xsl:for-each select="field[@name='Textarea']/value">
              <xsl:element name="mods:form">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:attribute name="type">
                  <xsl:value-of select="'Textarea'" />
                </xsl:attribute>
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Tekstområde']">
            <xsl:for-each select="field[@name='Tekstområde']/value">
              <xsl:element name="mods:form">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:attribute name="type">
                  <xsl:value-of select="'Tekstområde'" />
                </xsl:attribute>
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>

        <xsl:if test="field[@name='State']">
          <xsl:element name="mods:note">
            <xsl:attribute name="type">
              <xsl:value-of select="'State'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='State']/value" />
          </xsl:element>
        </xsl:if>

        <xsl:for-each select="field[@name='Pageorientation']/value">
          <xsl:element name="mods:note">
            <xsl:attribute name="displayLabel">Pageorientation</xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="field[@name='Color Codes']/value">
          <xsl:element name="mods:note">
            <xsl:attribute name="type" >AIM Color Codes</xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="field[@name='Materialeteknik']/value">
          <xsl:element name="mods:form">
            <xsl:attribute name="type">Materialeteknik</xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="field[@name='Publiceringsformat']/value">
          <xsl:element name="mods:extent">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>

        <xsl:for-each select="field[@name='Medium']/value">
          <xsl:element name="mods:form">
            <xsl:attribute name="type">
              <xsl:value-of select="'additional physical form'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:element> <!--mods:physicalDescription-->
    </xsl:if>

  </xsl:template>
  <!-- END physicalDescription -->

  <!-- START recordInfo -->
  <xsl:template name="mods_recordInfo">

    <xsl:element name="mods:recordInfo">
      <!-- record creation date -->
      <xsl:if test="field[@name='Record Creation Date']">
        <xsl:element name="mods:recordCreationDate">
          <xsl:attribute name="encoding">
            <xsl:value-of select="'w3cdtf'" />
          </xsl:attribute>
          <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
              'EEE MMM dd HH:mm:ss z yyy', field[@name='Record Creation Date']/value)" />
        </xsl:element>
      </xsl:if>

      <!-- record change date -->
      <xsl:if test="field[@name='Record Modification Date']">
        <xsl:element name="mods:recordChangeDate">
          <xsl:attribute name="encoding">
            <xsl:value-of select="'w3cdtf'" />
          </xsl:attribute>
          <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
              'EEE MMM dd HH:mm:ss z yyy', field[@name='Record Modification Date']/value)" />
        </xsl:element>
      </xsl:if>

      <!-- record identifier -->
      <xsl:if test="field[@name='record_id']">
        <xsl:element name="mods:recordIdentifier">
          <xsl:value-of select="field[@name='record_id']/value" />
        </xsl:element>
      </xsl:if>

      <!-- language of cataloging -->
      <xsl:element name="mods:languageOfCataloging">
        <xsl:element name="mods:languageTerm">
          <xsl:attribute name="authority">
            <xsl:value-of select="'rfc4646'" />
          </xsl:attribute>
          <xsl:attribute name="type">
            <xsl:value-of select="'code'" />
          </xsl:attribute>
          <xsl:value-of select="$mods_default_lang" />
        </xsl:element>
      </xsl:element>

      <!-- record origin -->
      <xsl:for-each select="field[@name='Aleph_ID']/value">
        <xsl:element name="mods:recordOrigin">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:value-of select="." />
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="field[@name='AlephID']/value">
        <xsl:element name="mods:recordOrigin">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:value-of select="." />
        </xsl:element>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>
  <!-- END recordInfo -->

  <!-- START relatedItem -->
  <xsl:template name="mods_relatedItem">
    <!-- Collection || Samling -->
    <xsl:choose>
      <xsl:when test="field[@name='Collection']">
        <xsl:for-each select="field[@name='Collection']/value">
          <xsl:element name="mods:relatedItem">
            <xsl:attribute name="type">host</xsl:attribute>
            <xsl:attribute name="displayLabel">Collection</xsl:attribute>
            <xsl:element name="mods:titleInfo">
              <xsl:call-template name="title_info_content" />
            </xsl:element>
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="field[@name='Samling']">
        <xsl:for-each select="field[@name='Samling']/value">
          <xsl:element name="mods:relatedItem">
            <xsl:attribute name="type">host</xsl:attribute>
            <xsl:attribute name="displayLabel">Samling</xsl:attribute>
            <xsl:element name="mods:titleInfo">
              <xsl:call-template name="title_info_content" />
            </xsl:element>
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Publikation -->
    <xsl:if test="field[@name='Publikation']">
      <xsl:element name="mods:relatedItem">
        <xsl:attribute name="type">
          <xsl:value-of select="'host'" />
        </xsl:attribute>
        <xsl:attribute name="displayLabel">
          <xsl:value-of select="'Publication'" />
        </xsl:attribute>
        <xsl:for-each select="field[@name='Publikation']/value">
          <xsl:element name="mods:titleInfo">
            <xsl:call-template name="title_info_content" />
          </xsl:element>
        </xsl:for-each>

        <!-- Publikationsformat -->
        <xsl:if test="field[@name='Publikationsformat']">
          <xsl:element name="mods:physicalDescription">
            <xsl:element name="mods:form">
              <xsl:attribute name="type">
                <xsl:value-of select="'Publikationsformat'" />
              </xsl:attribute>
              <xsl:value-of select="field[@name='Publikationsformat']/value" />
            </xsl:element>
          </xsl:element>
        </xsl:if>
      </xsl:element>
    </xsl:if>

    <!-- Event date, Event description, Event lable, Event participants, Event place, Event type -->
    <xsl:if test="field[@name='Event date'] or field[@name='Event description'] or field[@name='Event label'] or
            field[@name='Event participants'] or field[@name='Event place'] or field[@name='Event type']">
      <xsl:element name="mods:relatedItem">

        <!-- Event type -->
        <xsl:attribute name="type">
          <xsl:value-of select="java:dk.kb.metadata.selector.ModsEnumeratorSelector.relatedItemAttributeType(field[@name='Event type']/value, $type_of_event)" />
        </xsl:attribute>

        <!-- Event description -->
        <xsl:if test="field[@name='Event description']">
          <xsl:element name="mods:physicalDescription">
            <xsl:for-each select="field[@name='Event description']/value">
              <xsl:element name="mods:note">
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
        </xsl:if>

        <!-- Event date and Event place in originInfo -->
        <xsl:if test="field[@name='Event date'] or field[@name='Event participants'] or field[@name='Event place']">
          <xsl:element name="mods:originInfo">
            <!-- Event date -->
            <xsl:for-each select="field[@name='Event date']/value">
              <xsl:element name="mods:dateOther">
                <xsl:attribute name="type">
                  <xsl:value-of select="'event'" />
                </xsl:attribute>
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>

            <!-- Event place -->
            <xsl:for-each select="field[@name='Event place']/value">
              <xsl:element name="mods:place">
                <xsl:element name="mods:placeTerm">
                  <xsl:value-of select="." />
                </xsl:element>
              </xsl:element>
            </xsl:for-each>

            <!-- Event participants -->
            <xsl:for-each select="field[@name='Event participants']/value">
              <xsl:element name="mods:publisher" >
                <xsl:value-of select="." />
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
        </xsl:if>

        <!-- Event label  -->
        <xsl:if test="field[@name='Event label']">
          <xsl:element name="mods:titleInfo">
            <xsl:element name="mods:title">
              <xsl:value-of select="field[@name='Event label']/value" />
            </xsl:element>
          </xsl:element>
        </xsl:if>
      </xsl:element>
    </xsl:if>
    <!-- End Event-->

    <!-- URL (and URL-tekst) -->
    <xsl:if test="field[@name='URL']">
      <xsl:element name="mods:relatedItem">
        <xsl:attribute name="type">
          <xsl:value-of select="'host'" />
        </xsl:attribute>
        <xsl:element name="mods:identifier">
          <xsl:attribute name="type">
            <xsl:value-of select="'URL'" />
          </xsl:attribute>
          <xsl:value-of select="field[@name='URL']/value" />
        </xsl:element>

        <xsl:for-each select="field[@name='URL-tekst']/value">
          <xsl:element name="mods:note">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'URL-tekst'" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Serie -->
    <xsl:for-each select="field[@name='Serie']/value">
      <xsl:element name="mods:relatedItem">
        <xsl:attribute name="type">
          <xsl:value-of select="'series'" />
        </xsl:attribute>
        <xsl:attribute name="displayLabel">
          <xsl:value-of select="'Serie'" />
        </xsl:attribute>
        <xsl:element name="mods:titleInfo">
          <xsl:call-template name="title_info_content" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <!-- Seriebeskrivelse-->
    <xsl:if test="field[@name='Seriebeskrivelse']">
      <xsl:element name="mods:relatedItem">
        <xsl:attribute name="type">
          <xsl:value-of select="'series'" />
        </xsl:attribute>
        <xsl:element name="mods:note" >
          <xsl:attribute name="displayLabel">
            <xsl:value-of select="'Series Description'" />
          </xsl:attribute>
          <xsl:for-each select="field[@name='Seriebeskrivelse']/value">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:for-each>
        </xsl:element>
      </xsl:element>
    </xsl:if>

    <!-- Serietitel-->
    <xsl:if test="field[@name='Serietitel']">
      <xsl:element name="mods:relatedItem">
        <xsl:attribute name="type">
          <xsl:value-of select="'series'" />
        </xsl:attribute>
        <xsl:element name="mods:titleInfo">
          <xsl:for-each select="field[@name='Serietitel']/value">
            <xsl:call-template name="title_info_content" />
          </xsl:for-each>
        </xsl:element>
      </xsl:element>
    </xsl:if>

  </xsl:template>
  <!-- END relatedItem MASTER ASSET -->

  <!-- START relatedItem SUB ASSET -->
  <xsl:template name="mods_relatedItem_File">

    <!-- URI image if it is an image -->
    <xsl:if test="java:dk.kb.metadata.utils.FileFormatUtils.formatForMix(field[@name='formatName']/value)">
      <xsl:if test="field[@name='Asset Reference']">
        <xsl:element name="mods:relatedItem">
          <xsl:attribute name="type">
            <xsl:value-of select="'otherFormat'" />
          </xsl:attribute>
          <xsl:element name="mods:identifier">
            <xsl:attribute name="displayLabel">
              <xsl:value-of select="'image'" />
            </xsl:attribute>
            <xsl:attribute name="type">
              <xsl:value-of select="'uri'" />
            </xsl:attribute>
            <xsl:value-of select="concat($image_uri_base,
              substring-before(field[@name='Asset Reference']/value,'.tif'),
              '.jpg')" />
          </xsl:element>
          <!-- URI thumbnail -->
          <xsl:if test="field[@name='Thumbnail'] and field[@name='Asset Reference']">
            <xsl:element name="mods:identifier">
              <xsl:attribute name="displayLabel">
                <xsl:value-of select="'thumbnail'" />
              </xsl:attribute>
              <xsl:attribute name="type">
                <xsl:value-of select="'uri'" />
              </xsl:attribute>
              <xsl:value-of select="concat($image_uri_base, '/w150/h150',
                substring-before(field[@name='Asset Reference']/value,'.tif'),
                '.jpg')" />
            </xsl:element>
          </xsl:if>
        </xsl:element>
      </xsl:if>
    </xsl:if>

  </xsl:template>
  <!-- END relatedItem -->

  <!-- START subject -->
  <xsl:template name="mods_subject">

    <!-- Location || Lokalitet || Bygningsnavn-->
    <xsl:if test="field[@name='Location'] or field[@name='Lokalitet'] or field[@name='Bygningsnavn']">
      <xsl:element name="mods:subject">
        <xsl:call-template name="cumulus_get_lang_attribute"/>
        <xsl:element name="mods:hierarchicalGeographic">
          <xsl:choose>
            <xsl:when test="field[@name='Location']">
              <xsl:for-each select="field[@name='Location']/value">
                <xsl:element name="mods:area">
                  <xsl:attribute name="areaType">area</xsl:attribute>
                  <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:for-each>
            </xsl:when>
            <xsl:when test="field[@name='Lokalitet']">
              <xsl:for-each select="field[@name='Lokalitet']/value">
                <xsl:element name="mods:area">
                  <xsl:attribute name="areaType">area</xsl:attribute>
                  <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:for-each>
            </xsl:when>
          </xsl:choose>
          <xsl:for-each select="field[@name='Bygningsnavn']/value">
            <xsl:element name="mods:area">
              <xsl:attribute name="areaType">building</xsl:attribute>
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:element>
          </xsl:for-each>
        </xsl:element> <!--mods:hierarchicalGeographic-->
      </xsl:element> <!--mods:subject-->
    </xsl:if>

    <!-- Georeference -->
    <xsl:if test="field[@name='Georeference']/value">
      <xsl:element name="mods:subject">
        <xsl:element name="mods:cartographics">
          <xsl:element name="mods:coordinates">
            <xsl:value-of select="field[@name='Georeference']/value" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:if>

    <!-- Keywords
    <xsl:if test="field[@name='Keywords']">
      <xsl:element name="mods:subject">
        <xsl:for-each select="field[@name='Keywords']/value">
          <xsl:element name="mods:topic">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:if>
    -->

    <!-- Keywords -->
    <xsl:if test="field[@name='Keywords']">
      <xsl:element name="mods:subject">
        <xsl:call-template name="cumulus_get_lang_attribute"/>
        <xsl:for-each select="field[@name='Keywords']/value">
          <xsl:element name="mods:topic">
            <xsl:attribute name="lang">
              <xsl:value-of select="$mods_default_lang" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value"/>
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Subject -->
    <xsl:if test="field[@name='Subject']">
      <xsl:element name="mods:subject">
        <xsl:attribute name="lang">
          <xsl:value-of select="$mods_default_lang" />
        </xsl:attribute>
        <xsl:for-each select="field[@name='Subject']/value">
          <xsl:element name="mods:topic">
            <xsl:attribute name="lang">
              <xsl:value-of select="$mods_default_lang" />
            </xsl:attribute>
            <xsl:call-template name="cumulus_get_value"/>
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Målestok || Målestock || Scale
         with both 'Målestok', 'Målestock' and 'Scale' as scale.-->
    <xsl:if test="field[@name='Målestok'] or field[@name='Målestock'] or field[@name='Scale']">
      <xsl:element name="mods:subject">
        <xsl:choose>
          <xsl:when test="field[@name='Målestok']">
            <xsl:for-each select="field[@name='Målestok']/value">
              <xsl:element name="mods:cartographics">
                <xsl:element name="mods:scale">
                  <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Målestock']">
            <xsl:for-each select="field[@name='Målestock']/value">
              <xsl:element name="mods:cartographics">
                <xsl:element name="mods:scale">
                  <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Scale']">
            <xsl:for-each select="field[@name='Scale']/value">
              <xsl:element name="mods:cartographics">
                <xsl:element name="mods:scale">
                  <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:call-template name="cumulus_get_value" />
                </xsl:element>
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- Motiv -->
    <xsl:if test="field[@name='Motiv']">
      <xsl:for-each select="field[@name='Motiv']/value">
        <xsl:element name="mods:subject">
          <xsl:attribute name="displayLabel">Motive</xsl:attribute>
          <xsl:element name="mods:topic">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:element>
      </xsl:for-each>
    </xsl:if>

    <!-- Organisation || Organisation, ophav
         with both 'Organisation' and 'Organisation, ophav' as genre.-->
    <xsl:if test="field[@name='Organisation'] or field[@name='Organisation, ophav']">
      <xsl:element name="mods:subject">
        <xsl:choose>
          <xsl:when test="field[@name='Organisation']">
            <xsl:for-each select="field[@name='Organisation']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Organisation, ophav']">
            <xsl:for-each select="field[@name='Organisation, ophav']/value">
              <xsl:element name="mods:topic">
                  <xsl:call-template name="cumulus_get_lang_attribute" />
                  <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- Organisational affiliation of additional recipient || Medmodtagers organisation
         with both 'Organisational affiliation of additional recipient' and 'Medmodtagers organisation' as genre.-->
    <xsl:if test="field[@name='Organisational affiliation of additional recipient'] or field[@name='Medmodtagers organisation']">
      <xsl:element name="mods:subject">
        <xsl:choose>
          <xsl:when test="field[@name='Organisational affiliation of additional recipient']">
            <xsl:for-each select="field[@name='Organisational affiliation of additional recipient']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Medmodtagers organisation']">
            <xsl:for-each select="field[@name='Medmodtagers organisation']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- Organisation affiliation of recipient || Modtagers organisation
         with both 'Organisation affiliation of recipient' and 'Modtagers organisation' as genre.-->
    <xsl:if test="field[@name='Organisation affiliation of recipient'] or field[@name='Modtagers organisation']">
      <xsl:element name="mods:subject">
        <xsl:choose>
          <xsl:when test="field[@name='Organisation affiliation of recipient']">
            <xsl:for-each select="field[@name='Organisation affiliation of recipient']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Modtagers organisation']">
            <xsl:for-each select="field[@name='Modtagers organisation']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- Organisational affiliation of additional sender || Medsenders organisation
         with both 'Organisational affiliation of additional sender' and 'Medsenders organisation' as genre.-->
    <xsl:if test="field[@name='Organisational affiliation of additional sender'] or field[@name='Medsenders organisation']">
      <xsl:element name="mods:subject">
        <xsl:choose>
          <xsl:when test="field[@name='Organisational affiliation of additional sender']">
            <xsl:for-each select="field[@name='Organisational affiliation of additional sender']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Medsenders organisation']">
            <xsl:for-each select="field[@name='Medsenders organisation']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- Organisational affiliation of sender || Afsenders organisation
         with both 'Organisational affiliation of sender' and 'Afsenders organisation' as genre.-->
    <xsl:if test="field[@name='Organisational affiliation of sender'] or field[@name='Afsenders organisation']">
      <xsl:element name="mods:subject">
        <xsl:choose>
          <xsl:when test="field[@name='Organisational affiliation of sender']">
            <xsl:for-each select="field[@name='Organisational affiliation of sender']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Afsenders organisation']">
            <xsl:for-each select="field[@name='Afsenders organisation']/value">
              <xsl:element name="mods:topic">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:element>
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <!-- Person (Only included if Catalog is Luftfoto) -->
    <xsl:if test="contains(field[@name='Catalog Name']/value, 'Luftfoto OM')">
      <xsl:for-each select="field[@name='Person']/value">
        <xsl:element name="mods:subject">
          <xsl:element name="mods:topic">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:element>
      </xsl:for-each>
    </xsl:if>

    <!-- Additional recipient (personal) || Medmodtager
         with both 'Additional recipient (personal)' and 'Medmodtager' as genre. todo: to be removed-->
<!--    <xsl:if test="field[@name='Additional recipient (personal)'] or field[@name='Medmodtager']">-->
<!--      <xsl:element name="mods:subject">-->
<!--        <xsl:choose>-->
<!--          <xsl:when test="field[@name='Additional recipient (personal)']">-->
<!--            <xsl:for-each select="field[@name='Additional recipient (personal)']/value">-->
<!--              <xsl:element name="mods:cartographics">-->
<!--                <xsl:element name="mods:scale">-->
<!--                  <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--                  <xsl:call-template name="cumulus_get_value" />-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--          <xsl:when test="field[@name='Medmodtager']">-->
<!--            <xsl:for-each select="field[@name='Medmodtager']/value">-->
<!--              <xsl:element name="mods:cartographics">-->
<!--                <xsl:element name="mods:scale">-->
<!--                  <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--                  <xsl:call-template name="cumulus_get_value" />-->
<!--                </xsl:element>-->
<!--              </xsl:element>-->
<!--            </xsl:for-each>-->
<!--          </xsl:when>-->
<!--        </xsl:choose>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

    <!-- LCSH -->
    <xsl:for-each select="field[@name='LCSH']/value">
      <xsl:element name="mods:subject">
        <xsl:element name="mods:topic">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:call-template name="cumulus_get_value" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <!-- Sprog -->
    <xsl:for-each select="field[@name='Sprog']/value">
      <xsl:element name="mods:subject">
        <xsl:element name="mods:topic">
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:call-template name="cumulus_get_value" />
        </xsl:element>
      </xsl:element>
    </xsl:for-each>

    <!-- crowd -->
    <!-- Crowd_Emneord -->
    <xsl:if test="field[@name='Crowd_Emneord']">
      <xsl:element name="mods:subject">
        <xsl:attribute name="altRepGroup">crowd</xsl:attribute>
        <xsl:attribute name="displayLabel">Keywords</xsl:attribute>
        <xsl:element name="mods:topic">
          <xsl:for-each select="field[@name='Crowd_Emneord']/value">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:for-each>
        </xsl:element>
      </xsl:element>
    </xsl:if>

    <!-- Crowd_Georeference -->
    <xsl:if test="field[@name='Crowd_Georeference']">
      <xsl:element name="mods:subject">
        <xsl:attribute name="altRepGroup">crowd</xsl:attribute>
        <xsl:attribute name="displayLabel">Georeference</xsl:attribute>
        <xsl:element name="mods:cartographics">
          <xsl:element name="mods:coordinates">
            <xsl:value-of select="field[@name='Crowd_Georeference']/value" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:if>

    <xsl:if test="field[@name='Crowd_Person']">
      <xsl:element name="mods:subject">
        <xsl:attribute name="altRepGroup">crowd</xsl:attribute>
        <xsl:element name="mods:name">
          <xsl:attribute name="type">personal</xsl:attribute>
          <xsl:element name="mods:namePart">
            <xsl:value-of select="field[@name='Crowd_Person']/value" />
          </xsl:element>
          <xsl:element name="mods:role">
            <xsl:element name="mods:roleTerm">
              <xsl:attribute name="type">code</xsl:attribute>
              <xsl:value-of select="'crowd'" />
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:if>


    <xsl:if test="field[@name='Crowd_Bygningsnavn'] or field[@name='Crowd_Lokalitet'] or field[@name='Crowd_Sogn']
               or field[@name='Crowd_Matrikelnummer'] or field[@name='Crowd_Postnummer']
               or field[@name='Crowd_Husnummer'] or field[@name='Crowd_Vejnavn'] or field[@name='Crowd_By']">
      <xsl:element name="mods:subject">
        <xsl:attribute name="altRepGroup">crowd</xsl:attribute>
        <xsl:element name="mods:hierarchicalGeographic">
          <!-- Crowd_Bygningsnavn -->
          <xsl:element name="mods:area">
            <xsl:attribute name="areaType">building</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Bygningsnavn']/value" />
          </xsl:element>
          <!-- Crowd_Sted -->
          <xsl:element name="mods:area">
            <xsl:attribute name="areaType">area</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Sted']/value" />
          </xsl:element>
          <!-- Crowd_Sogn -->
          <xsl:element name="mods:area">
            <xsl:attribute name="areaType">parish</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Sogn']/value" />
          </xsl:element>
          <!-- Crowd_Matrikelnummer -->
          <xsl:element name="mods:area">
            <xsl:attribute name="areaType">cadastre</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Matrikelnummer']/value" />
          </xsl:element>
          <!--Crowd_Lokalitet-->
          <xsl:element name="mods:area">
            <xsl:attribute name="areaType">location</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Lokalitet']/value" />
          </xsl:element>
          <!-- Crowd_Postnummer -->
          <xsl:element name="mods:citySection">
            <xsl:attribute name="citySectionType">zipcode</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Postnummer']/value" />
          </xsl:element>
          <!-- Crowd_Husnummer -->
          <xsl:element name="mods:citySection">
            <xsl:attribute name="citySectionType">housenumber</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Husnummer']/value" />
          </xsl:element>
          <!-- Crowd_Vejnavn -->
          <xsl:element name="mods:citySection">
            <xsl:attribute name="citySectionType">street</xsl:attribute>
            <xsl:value-of select="field[@name='Crowd_Vejnavn']/value" />
          </xsl:element>
          <!--Crowd_By-->
          <xsl:element name="mods:city">
            <xsl:value-of select="field[@name='Crowd_By']/value" />
          </xsl:element>
        </xsl:element> <!--mods:hierarchicalGeographic-->
      </xsl:element> <!--mods:subject-->
    </xsl:if>

    <!-- Topografinummer -->
    <xsl:if test="field[@name='Topografinummer']/value">
      <xsl:element name="mods:subject">
        <xsl:attribute name="displayLabel">Topographical Number</xsl:attribute>
        <xsl:for-each select="field[@name='Topografinummer']">
          <xsl:element name="mods:geographicCode">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="cumulus_get_value" />
          </xsl:element>
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Person Tabel -->
    <xsl:for-each select="field[@name='Person-tabel']/table/row">
      <xsl:call-template name="mods_subject_ophav_person" />
    </xsl:for-each>
  </xsl:template>
  <!-- END subject -->

  <!-- START subject ophav person -->
  <xsl:template name="mods_subject_ophav_person">
    <!-- Person-tabel Recipient (person) -->
    <xsl:if test="(contains(field[@name='Rolle']/value, 'modtager') or
    contains(field[@name='Rolle']/value, 'supplerende modtager')) and
    not(field[@name='Kooperation']/value)">
      <xsl:element name="mods:subject">
        <xsl:attribute name="altRepGroup">rcp1</xsl:attribute>
        <xsl:element name="mods:name">
          <xsl:attribute name="displayLabel">Recipient</xsl:attribute>
          <xsl:attribute name="type">personal</xsl:attribute>
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:element name="mods:role">
            <xsl:element name="mods:roleTerm">
              <xsl:attribute name="type">code</xsl:attribute>
              <xsl:value-of select="'rcp'" />
            </xsl:element>
          </xsl:element>
          <xsl:if test="field[@name='Efternavn']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">family</xsl:attribute>
              <xsl:value-of select="field[@name='Efternavn']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Fornavn']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">given</xsl:attribute>
              <xsl:value-of select="field[@name='Fornavn']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">date</xsl:attribute>
              <xsl:value-of select="field[@name='Født']/value" />
              <xsl:value-of select="'/'" />
              <xsl:value-of select="field[@name='Død']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Nationalitet']/value">
            <xsl:element name="mods:description">
              <xsl:value-of select="field[@name='Nationalitet']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Titel']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">termsOfAddress</xsl:attribute>
              <xsl:value-of select="field[@name='Titel']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Andet navn']/value">
            <xsl:element name="mods:displayForm">
              <xsl:for-each select="field[@name='Andet navn']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:for-each>
            </xsl:element>
          </xsl:if>
        </xsl:element> <!-- END mods:name -->
        <xsl:if test="../../../field[@name='Location of recipient'] or ../../../field[@name='Country (location) of recipient']">
          <xsl:element name="mods:hierarchicalGeographic">
            <xsl:if test="../../../field[@name='Location of recipient']">
              <xsl:element name="mods:city">
                <xsl:value-of select="../../../field[@name='Location of recipient']/value" />
              </xsl:element>
            </xsl:if>
            <xsl:if test="../../../field[@name='Country (location) of recipient']">
              <xsl:element name="mods:country">
                <xsl:value-of select="../../../field[@name='Country (location) of recipient']/value" />
              </xsl:element>
            </xsl:if>
          </xsl:element> <!--mods:hierarchicalGeographic -->
        </xsl:if>
      </xsl:element> <!-- END mods:subject -->
    </xsl:if>
    <!-- END Person-tabel Recipient (person) -->

    <!-- Person-tabel Recipient (organization) -->
    <xsl:if test="(contains(field[@name='Rolle']/value, 'modtager') or
    contains(field[@name='Rolle']/value, 'supplerende modtager')) and
    field[@name='Kooperation']/value">
      <xsl:element name="mods:subject">
        <xsl:attribute name="altRepGroup">rcp2</xsl:attribute>
        <xsl:element name="mods:name">
          <xsl:attribute name="displayLabel">Recipient</xsl:attribute>
          <xsl:attribute name="type">corporate</xsl:attribute>
          <xsl:call-template name="cumulus_get_lang_attribute" />
          <xsl:element name="mods:role">
            <xsl:element name="mods:roleTerm">
              <xsl:attribute name="type">code</xsl:attribute>
              <xsl:value-of select="'rcp'" />
            </xsl:element>
          </xsl:element>
          <xsl:if test="field[@name='Efternavn']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">family</xsl:attribute>
              <xsl:value-of select="field[@name='Efternavn']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Fornavn']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">given</xsl:attribute>
              <xsl:value-of select="field[@name='Fornavn']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">date</xsl:attribute>
              <xsl:value-of select="field[@name='Født']/value" />
              <xsl:value-of select="'/'" />
              <xsl:value-of select="field[@name='Død']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Nationalitet']/value">
            <xsl:element name="mods:description">
              <xsl:value-of select="field[@name='Nationalitet']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Titel']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">termsOfAddress</xsl:attribute>
              <xsl:value-of select="field[@name='Titel']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Andet navn']/value">
            <xsl:element name="mods:displayForm">
              <xsl:for-each select="field[@name='Andet navn']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:for-each>
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Kooperation']/value">
            <xsl:element name="mods:affiliation">
              <xsl:value-of select="field[@name='Kooperation']/value"/>
            </xsl:element>
          </xsl:if>
        </xsl:element> <!-- END mods:name -->
        <xsl:if test="../../../field[@name='Location of recipient'] or ../../../field[@name='Country (location) of recipient']">
          <xsl:element name="mods:hierarchicalGeographic">
            <xsl:if test="../../../field[@name='Location of recipient']">
              <xsl:element name="mods:city">
                <xsl:value-of select="../../../field[@name='Location of recipient']/value" />
              </xsl:element>
            </xsl:if>
            <xsl:if test="../../../field[@name='Country (location) of recipient']">
              <xsl:element name="mods:country">
                <xsl:value-of select="../../../field[@name='Country (location) of recipient']/value" />
              </xsl:element>
            </xsl:if>
          </xsl:element> <!--mods:hierarchicalGeographic -->
        </xsl:if>
      </xsl:element> <!-- END mods:subject -->
    </xsl:if>
    <!-- END Recipient (organization) -->

    <!-- Person-tabel Recipient, different or no role -->
    <xsl:if test="not(contains(field[@name='Rolle']/value, 'modtager')) and
    not(contains(field[@name='Rolle']/value, 'supplerende modtager'))">
      <xsl:element name="mods:subject">
        <xsl:element name="mods:name">
          <xsl:attribute name="type">personal</xsl:attribute>
          <xsl:if test="field[@name='Efternavn']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">family</xsl:attribute>
              <xsl:value-of select="field[@name='Efternavn']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Fornavn']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">given</xsl:attribute>
              <xsl:value-of select="field[@name='Fornavn']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">date</xsl:attribute>
              <xsl:value-of select="field[@name='Født']/value" />
              <xsl:value-of select="'/'" />
              <xsl:value-of select="field[@name='Død']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Nationalitet']/value">
            <xsl:element name="mods:description">
              <xsl:value-of select="field[@name='Nationalitet']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Titel']/value">
            <xsl:element name="mods:namePart">
              <xsl:attribute name="type">termsOfAddress</xsl:attribute>
              <xsl:value-of select="field[@name='Titel']/value" />
            </xsl:element>
          </xsl:if>
          <xsl:if test="field[@name='Andet navn']/value">
            <xsl:element name="mods:displayForm">
              <xsl:for-each select="field[@name='Andet navn']/value">
                <xsl:call-template name="cumulus_get_lang_attribute" />
                <xsl:call-template name="cumulus_get_value" />
              </xsl:for-each>
            </xsl:element>
          </xsl:if>
        </xsl:element> <!-- END mods:name -->
      </xsl:element> <!-- END mods:subject -->
    </xsl:if>
    <!-- END Person-tabel Recipient, different or no role -->

    <!-- OLD: Tabel-person recipient, different or no role
    <xsl:element name="mods:subject">
      <xsl:element name="mods:name">
        <xsl:attribute name="type">
          <xsl:value-of select="'personal'" />
        </xsl:attribute>
        <xsl:if test="field[@name='Efternavn']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'family'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Efternavn']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Fornavn']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'given'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Fornavn']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Født']/value or field[@name='Død']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'date'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Født']/value" />
            <xsl:value-of select="'/'" />
            <xsl:value-of select="field[@name='Død']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Nationalitet']/value">
          <xsl:element name="mods:description">
            <xsl:value-of select="field[@name='Nationalitet']/value" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="field[@name='Titel']/value">
          <xsl:element name="mods:namePart">
            <xsl:attribute name="type">
              <xsl:value-of select="'termsOfAddress'" />
            </xsl:attribute>
            <xsl:value-of select="field[@name='Titel']/value" />
          </xsl:element>
        </xsl:if>
        -->
        <!--      mods:subject/mods:name cannot have sub-element mods:alternativeName,
                  thus making a mods:displayForm for this
        <xsl:if test="field[@name='Andet navn']/value">
          <xsl:element name="mods:displayForm">
            <xsl:for-each select="field[@name='Andet navn']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:element>
        </xsl:if>
      </xsl:element>--> <!--mods:name
    </xsl:element>--> <!--mods:subject-->

<!--    <xsl:if test="field[@name='Andet navn']/value">-->
<!--      <xsl:element name="mods:subject">-->
<!--        <xsl:element name="mods:name">-->
<!--          <xsl:attribute name="displayLabel">Alternative Name</xsl:attribute>-->
<!--          <xsl:attribute name="type">personal</xsl:attribute>-->
<!--          <xsl:for-each select="field[@name='Andet navn']/value">-->
<!--            <xsl:element name="mods:namePart">-->
<!--              <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--              <xsl:call-template name="cumulus_get_value" />-->
<!--            </xsl:element>-->
<!--          </xsl:for-each>-->
<!--        </xsl:element>-->
<!--      </xsl:element>-->
<!--    </xsl:if>-->

  </xsl:template>
  <!-- END subject ophav person -->

  <!-- START tableOfContents -->
  <xsl:template name="mods_tableOfContents">
    <!-- NOTHING YET! -->
  </xsl:template>
  <!-- END tableOfContents -->

  <!-- START targetAudience -->
  <xsl:template name="mods_targetAudience">
    <!-- NOTHING YET! -->
  </xsl:template>
  <!-- END targetAudience -->

  <!-- START titleInfo -->
  <xsl:template name="mods_titleInfo">
    <!-- Titel eller Title -->
<!--    <xsl:choose>-->
<!--      <xsl:when test="field[@name='Titel']">-->
<!--        <xsl:element name="mods:titleInfo">-->
<!--          <xsl:for-each select="field[@name='Titel']/value">-->
<!--            <xsl:call-template name="cumulus_get_lang_attribute" />-->
<!--            <xsl:call-template name="title_info_content" />-->
<!--          </xsl:for-each>-->
<!--          <xsl:call-template name="subtitle_info_content" />-->
<!--        </xsl:element>-->
<!--      </xsl:when>-->
      <xsl:if test="field[@name='Title']">
        <xsl:element name="mods:titleInfo">
          <xsl:for-each select="field[@name='Title']/value">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:call-template name="title_info_content" />
          </xsl:for-each>
          <xsl:call-template name="subtitle_info_content" />
        </xsl:element>
      </xsl:if>
<!--    </xsl:choose>-->

    <!-- alternativ title -->
    <xsl:if test="field[@name='Alternative title']">
      <xsl:element name="mods:titleInfo">
        <xsl:for-each select="field[@name='Alternative title']/value">
          <xsl:attribute name="type">
            <xsl:value-of select="'alternative'" />
          </xsl:attribute>
          <xsl:call-template name="title_info_content" />
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Transcribed title || Transskriberet titel -->
    <xsl:choose>
      <xsl:when test="field[@name='Transcribed title']">
        <xsl:for-each select="field[@name='Transcribed title']/value">
          <xsl:element name="mods:titleInfo">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'translated'" />
            </xsl:attribute>
            <xsl:call-template name="title_info_content" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="field[@name='Transskriberet titel']">
        <xsl:for-each select="field[@name='Transskriberet titel']/value">
          <xsl:element name="mods:titleInfo">
            <xsl:call-template name="cumulus_get_lang_attribute" />
            <xsl:attribute name="type">
              <xsl:value-of select="'translated'" />
            </xsl:attribute>
            <xsl:call-template name="title_info_content" />
          </xsl:element>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Original title -->
    <xsl:if test="field[@name='Original title']">
      <xsl:element name="mods:titleInfo">
        <xsl:for-each select="field[@name='Original title']/value">
          <xsl:attribute name="displayLabel">
            <xsl:value-of select="'Original title'" />
          </xsl:attribute>
          <xsl:call-template name="title_info_content" />
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

    <!-- Crowd_Titel -->
    <xsl:if test="field[@name='Crowd_Titel']">
      <xsl:element name="mods:titleInfo">
        <xsl:for-each select="field[@name='Crowd_Titel']/value">
          <xsl:attribute name="altRepGroup">crowd</xsl:attribute>
          <xsl:call-template name="title_info_content" />
        </xsl:for-each>
      </xsl:element>
    </xsl:if>

  </xsl:template>
  <!-- END titleInfo -->

  <!-- START typeOfResource -->

<!--          ModsEnumeratorSelector.typeOfResource is not needed as of mods.xsd v. 3.7 -->
  <!--      <xsl:value-of select="java:dk.kb.metadata.selector.ModsEnumeratorSelector.typeOfResource(-->
  <!--          field[@name='Materialebetegnelse']/value, -->
  <!--          field[@name='Resourcedescription']/value, -->
  <!--          field[@name='Generel materialebetegnelse']/value, -->
  <!--          field[@name='General Resourcedescription']/value)" />-->

  <xsl:template name="mods_typeOfResource">
    <xsl:if test="field[@name='Generel materialebetegnelse'] or field[@name='Generel Resource Description']">
      <xsl:element name="mods:typeOfResource">
        <xsl:attribute name="displayLabel">
          <xsl:value-of select="'Generel Resource Description'" />
        </xsl:attribute>
        <xsl:choose>
          <xsl:when test="field[@name='Generel materialebetegnelse']">
            <xsl:for-each select="field[@name='Generel materialebetegnelse']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Generel Resource Description']">
            <xsl:for-each select="field[@name='Generel Resource Description']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>
    <xsl:if test="field[@name='Materialebetegnelse'] or field[@name='Resource Description']">
      <xsl:element name="mods:typeOfResource">
        <xsl:attribute name="displayLabel">
          <xsl:value-of select="'Resource Description'" />
        </xsl:attribute>
        <xsl:choose>
          <xsl:when test="field[@name='Materialebetegnelse']">
            <xsl:for-each select="field[@name='Materialebetegnelse']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="field[@name='Resource Description']">
            <xsl:for-each select="field[@name='Resource Description']/value">
              <xsl:call-template name="cumulus_get_lang_attribute" />
              <xsl:call-template name="cumulus_get_value" />
            </xsl:for-each>
          </xsl:when>
        </xsl:choose>
      </xsl:element>
    </xsl:if>

    <xsl:if test="field[@name='Manuscript']">
      <xsl:variable name="SET">
        <xsl:copy-of select="field[@name='Manuscript']/value"/>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$SET = 'yes' or $SET = 'Yes' or $SET = 'ja'">
          <xsl:element name="mods:typeOfResource">
            <xsl:attribute name="manuscript">
              <xsl:value-of select="field[@name='Manuscript']/value" />
            </xsl:attribute>
            <xsl:value-of select="'tekst(håndskrift)'" />
          </xsl:element>
        </xsl:when>
      </xsl:choose>
    </xsl:if>

  </xsl:template>
  <!-- END typeOfResource -->

  <xsl:template name="title_info_content">
    <xsl:if test="java:dk.kb.metadata.utils.TransformUtils.isCumulusValNonSort(.)">
      <xsl:element name="mods:nonSort">
        <xsl:value-of select="java:dk.kb.metadata.utils.TransformUtils.getCumulusValNonSort(.)" />
      </xsl:element>
    </xsl:if>
    <xsl:element name="mods:title">
      <xsl:choose>
        <xsl:when test="java:dk.kb.metadata.utils.TransformUtils.isCumulusValTranslit(.)">
          <xsl:attribute name="transliteration">
            <xsl:value-of select="'rex'" />
          </xsl:attribute>
          <xsl:value-of select="java:dk.kb.metadata.utils.TransformUtils.getCumulusValTranslit(.)" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="cumulus_get_value" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>

  <xsl:template name="subtitle_info_content">
    <xsl:for-each select="field[@name='Subtitle']/value">
      <xsl:element name="mods:subTitle">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>
    <xsl:for-each select="field[@name='Undertitel']/value">
      <xsl:element name="mods:subTitle">
        <xsl:call-template name="cumulus_get_lang_attribute" />
        <xsl:call-template name="cumulus_get_value" />
      </xsl:element>
    </xsl:for-each>
  </xsl:template>

</xsl:transform> 
