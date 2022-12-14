<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:premis="http://www.loc.gov/premis/v3"
    
    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <xsl:include href="transformToMix.xsl"/>
  <xsl:include href="transformToBext.xsl"/>
  
  <xsl:variable name="id" select="record/field[@name='objectIdentifierValue']/value"/>
  <xsl:variable name="PREMIS_LOCATION" select="'http://www.loc.gov/premis/v3 http://id.kb.dk/standards/premis/version_3_0/premis.xsd'" />
  <xsl:variable name="PREMIS_VERSION" select="'3.0'" />

  <xsl:template name="premis_preservation">
    <!-- Preservation level for bit safety. -->
    <xsl:element name="premis:preservationLevel">
      <!-- preservationLevelType -->
      <xsl:element name="premis:preservationLevelType">
        <xsl:value-of select="'bitSafety'" />
      </xsl:element>
      
      <!-- preservationLevelValue -->
      <xsl:element name="premis:preservationLevelValue">
        <xsl:value-of select="java:dk.kb.metadata.selector.PremisPreservationLevelEnumeratorSelector.getBitPreservationLevelValue(
                field[@name='preservationLevelValue_BitSafety']/value)" />
      </xsl:element>

      <!-- preservationLevelRationale -->
      <xsl:if test="field[@name='preservationLevelRationale_BitSafety']">
        <xsl:element name="premis:preservationLevelRationale">
          <xsl:value-of select="field[@name='preservationLevelRationale_BitSafety']/value" />
        </xsl:element>
      </xsl:if>
      
      <!-- preservationLevelDateAssigned -->
      <xsl:element name="premis:preservationLevelDateAssigned">
        <xsl:choose>
          <xsl:when test="field[@name='preservationLevelDateAssigned']">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
                  'YYYYMMdd',field[@name='preservationLevelDateAssigned']/value)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:otherwise>
        </xsl:choose> 
      </xsl:element>
    </xsl:element>
    <!-- Preservation level for logical preservation. -->
    <xsl:element name="premis:preservationLevel">
      <!-- preservationLevelType -->
      <xsl:element name="premis:preservationLevelType">
        <xsl:value-of select="'logical'" />
      </xsl:element>

      <!-- preservationLevelValue -->
      <xsl:element name="premis:preservationLevelValue">
        <xsl:value-of select="java:dk.kb.metadata.selector.PremisPreservationLevelEnumeratorSelector.getLogicalPreservationLevelValue(
                field[@name='preservationLevelValue_Logical']/value)" />
      </xsl:element>

      <!-- preservationLevelRationale -->
      <xsl:if test="field[@name='preservationLevelRationale_Logical']">
        <xsl:element name="premis:preservationLevelRationale">
          <xsl:value-of select="field[@name='preservationLevelRationale_Logical']/value" />
        </xsl:element>
      </xsl:if>

      <!-- preservationLevelDateAssigned -->
      <xsl:element name="premis:preservationLevelDateAssigned">
        <xsl:choose>
          <xsl:when test="field[@name='preservationLevelDateAssigned']">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
                  'YYYYMMdd',field[@name='preservationLevelDateAssigned']/value)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:otherwise>
        </xsl:choose> 
      </xsl:element>
    </xsl:element>
    <!-- Preservation level for confidentiality. -->
    <xsl:element name="premis:preservationLevel">
      <!-- preservationLevelType -->
      <xsl:element name="premis:preservationLevelType">
        <xsl:value-of select="'confidentiality'" />
      </xsl:element>
    
      <!-- preservationLevelValue -->
      <xsl:element name="premis:preservationLevelValue">
        <xsl:value-of select="java:dk.kb.metadata.selector.PremisPreservationLevelEnumeratorSelector.getConfidentialityPreservationLevelValue(
                field[@name='preservationLevelValue_Confidentiality']/value)" />
      </xsl:element>
      
      <!-- preservationLevelRationale -->
      <xsl:if test="field[@name='preservationLevelRationale_Confidentiality']">
        <xsl:element name="premis:preservationLevelRationale">
          <xsl:value-of select="field[@name='preservationLevelRationale_Confidentiality']/value" />
        </xsl:element>
      </xsl:if>

      <!-- preservationLevelDateAssigned -->
      <xsl:element name="premis:preservationLevelDateAssigned">
        <xsl:choose>
          <xsl:when test="field[@name='preservationLevelDateAssigned']">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
                  'YYYYMMdd',field[@name='preservationLevelDateAssigned']/value)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:otherwise>
        </xsl:choose> 
      </xsl:element>
    </xsl:element>
  </xsl:template>
  
  <xsl:template name="premis_object">
    <premis:object xsi:schemaLocation="{$PREMIS_LOCATION}" version="{$PREMIS_VERSION}">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">premis:file</xsl:attribute>
      <!-- START 1.1 objectIdentifier -->
      <xsl:element name="premis:objectIdentifier">
        <xsl:element name="premis:objectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:objectIdentifierValue">
          <xsl:call-template name="premis_object_identifier_value" />
        </xsl:element>
      </xsl:element>
      <!-- END 1.1 objectIdentifier -->
      
      <!-- BEGIN 1.5 ObjectCharacteristics -->
      <xsl:element name="premis:objectCharacteristics">
        <!-- 1.5.1 compositionLevel -->
        <xsl:element name="premis:compositionLevel">
          <xsl:if test="field[@name='compositionLevel']/value">
            <xsl:value-of select="field[@name='compositionLevel']/value" />
          </xsl:if>
        </xsl:element>
        
        <!-- 1.5.2 messageDigest -->
        <xsl:if test="field[@name='CHECKSUM_ORIGINAL_MASTER']">
          <xsl:element name="premis:fixity">
            <xsl:element name="premis:messageDigestAlgorithm">
            <xsl:choose>
              <xsl:when test="field[@name='messageDigestAlgorithm']">
                <xsl:value-of select="field[@name='messageDigestAlgorithm']/value" />
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'MD5'" />
              </xsl:otherwise>
            </xsl:choose>
            </xsl:element>
            <xsl:element name="premis:messageDigest">
              <xsl:value-of select="field[@name='CHECKSUM_ORIGINAL_MASTER']/value" />
            </xsl:element>
          </xsl:element>
        </xsl:if>
        
        <!-- 1.5.3 size-->
        <xsl:choose>
          <xsl:when test="field[@name='size']">
            <xsl:element name="premis:size">
              <xsl:value-of select="field[@name='size']/value" />
            </xsl:element>
          </xsl:when>
          <xsl:when test="field[@name='File Data Size']">
            <xsl:element name="premis:size">
              <xsl:value-of select="field[@name='File Data Size']/value" />
            </xsl:element>
          </xsl:when>
        </xsl:choose>
        
        <!-- 1.5.4 format -->
        <xsl:element name="premis:format">
          <xsl:element name="premis:formatDesignation">
            <xsl:choose>
              <xsl:when test="field[@name='formatName']/value">
                <xsl:element name="premis:formatName">
                  <xsl:value-of select="field[@name='formatName']/value" />
                </xsl:element>
                <xsl:element name="premis:formatVersion">
                  <xsl:value-of select="field[@name='formatVersion']/value" />
                </xsl:element>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:element name="premis:formatName">
                    <xsl:value-of select="field[@name='objectCharacteristicsFormatName']/value" />
                  </xsl:element>
                  <xsl:for-each select="field[@name='objectCharacteristicsFormatVersion']/value">
                    <xsl:element name="premis:formatVersion">
                      <xsl:value-of select="." />
                    </xsl:element>
                  </xsl:for-each>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:element>
          <xsl:element name="premis:formatNote">
            <xsl:value-of select="concat('Validated by Cumulus version: ', java:dk.kb.metadata.selector.AgentSelector.getCumulusVersion())" />
          </xsl:element>
        </xsl:element>
        
        <!-- 1.5.5 creatingApplication -->
        <xsl:if test="field[@name='creatingApplication'] or field[@name='Software']">
          <xsl:element name="premis:creatingApplication">
            <xsl:element name="premis:creatingApplicationName">
              <xsl:choose>
                <xsl:when test="field[@name='creatingApplication']">
                  <xsl:value-of select="field[@name='creatingApplication']/value" />
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="field[@name='Software']/value" />
                </xsl:otherwise>
              </xsl:choose>
            </xsl:element>
          </xsl:element>
        </xsl:if>
        
        <!-- 1.5.7 objectCharacteristicsExtension -->
        <xsl:if test="java:dk.kb.metadata.utils.FileFormatUtils.formatForMix(field[@name='formatName']/value)">
          <xsl:element name="premis:objectCharacteristicsExtension">
            <xsl:call-template name="mix" />
          </xsl:element>
        </xsl:if>
        <xsl:if test="java:dk.kb.metadata.utils.FileFormatUtils.formatForBext(field[@name='formatName']/value)">
          <xsl:element name="premis:objectCharacteristicsExtension">
            <xsl:call-template name="bext" />
          </xsl:element>
        </xsl:if>
        
      </xsl:element>
      <!-- END 1.5 ObjectCharacteristics -->
      
      <!-- START 1.15 linkingRightsStatementIdentifier -->
      <xsl:if test="field[@name='rightsStatementIdentifierValue']">
        <xsl:element name="premis:linkingRightsStatementIdentifier">
          <xsl:element name="premis:linkingRightsStatementIdentifierType">
            <xsl:call-template name="premis_rights_identifier_type" />
          </xsl:element>
          <xsl:element name="premis:linkingRightsStatementIdentifierValue">
            <xsl:call-template name="premis_rights_identifier_value" />
          </xsl:element>
        </xsl:element>
      </xsl:if>
      <!-- END 1.15 linkingRightsStatementIdentifier -->
    </premis:object>
  </xsl:template>
  
  <xsl:template name="premis_event_for_file">
    <premis:event xsi:schemaLocation="{$PREMIS_LOCATION}" version="{$PREMIS_VERSION}">
      <!-- eventIdentifier -->
      <xsl:element name="premis:eventIdentifier">
        <xsl:element name="premis:eventIdentifierType">
          <xsl:call-template name="premis_event_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:eventIdentifierValue">
         <xsl:call-template name="premis_event_identifier_value" />
        </xsl:element>
      </xsl:element>
      
      <!-- eventType -->
      <xsl:element name="premis:eventType">
        <xsl:choose>
          <xsl:when test="field[@name='Bevarings metadata historik']">
            <xsl:value-of select="'metadata modification'" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'ingestion'" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
      
      <!-- date time -->
      <xsl:element name="premis:eventDateTime">
        <xsl:choose>
          <xsl:when test="field[@name='eventDateTime']">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
                'EEE MMM dd HH:mm:ss z yyyy',field[@name='eventDateTime']/value)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
      
      <!-- event detail information -->
      <xsl:element name="premis:eventDetailInformation">
        <xsl:element name="premis:eventDetail">
          <xsl:value-of select="java:dk.kb.metadata.selector.PremisUtils.getEnvironmentAndProperties()" />
        </xsl:element>
      </xsl:element>
      
      <!-- linkingAgentIdentifier -->
      <xsl:element name="premis:linkingAgentIdentifier">
        <xsl:element name="premis:linkingAgentIdentifierType">
          <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgentType()" />
        </xsl:element>
        <xsl:element name="premis:linkingAgentIdentifierValue">
          <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgentValue()" />
        </xsl:element>
      </xsl:element>
      
      <!-- linkingObjectIdentifier -->
      <xsl:element name="premis:linkingObjectIdentifier">
        <xsl:element name="premis:linkingObjectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:linkingObjectIdentifierValue">
          <xsl:call-template name="premis_object_identifier_value" />
        </xsl:element>
      </xsl:element>
      <xsl:element name="premis:linkingObjectIdentifier">
        <xsl:element name="premis:linkingObjectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:linkingObjectIdentifierValue">
          <xsl:call-template name="premis_identifier_value" />
        </xsl:element>
      </xsl:element>
      <!-- When this is an update, then add the linking identifier to the previous metadata  -->
      <xsl:if test="field[@name='Bevarings metadata historik']">
        <xsl:element name="premis:linkingObjectIdentifier">
          <xsl:element name="premis:linkingObjectIdentifierType">
            <xsl:call-template name="premis_identifier_type" />
          </xsl:element>
          <xsl:element name="premis:linkingObjectIdentifierValue">
            <xsl:value-of select="java:dk.kb.metadata.selector.PremisUtils.getLatestHistoricGuid(
                    field[@name='Bevarings metadata historik']/value)" />
          </xsl:element>
          <xsl:element name="premis:linkingObjectRole">
            <xsl:value-of select="'source'" />
          </xsl:element>
        </xsl:element>
      </xsl:if>
    </premis:event>
  </xsl:template>
  
  <xsl:template name="premis_event_for_representation">
    <premis:event xsi:schemaLocation="{$PREMIS_LOCATION}" version="{$PREMIS_VERSION}">
      <!-- eventIdentifier -->
      <xsl:element name="premis:eventIdentifier">
        <xsl:element name="premis:eventIdentifierType">
          <xsl:call-template name="premis_event_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:eventIdentifierValue">
         <xsl:call-template name="premis_event_identifier_value" />
        </xsl:element>
      </xsl:element>
      
      <!-- eventType -->
      <xsl:element name="premis:eventType">
        <xsl:choose>
          <xsl:when test="field[@name='Bevarings metadata historik']">
            <xsl:value-of select="'metadata modification'" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'ingestion'" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
      
      <!-- date time -->
      <xsl:element name="premis:eventDateTime">
        <xsl:choose>
          <xsl:when test="field[@name='eventDateTime']">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getDateTime(
                'EEE MMM dd HH:mm:ss z yyyy',field[@name='eventDateTime']/value)" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
      
      <!-- linkingAgentIdentifier -->
      <xsl:element name="premis:linkingAgentIdentifier">
        <xsl:element name="premis:linkingAgentIdentifierType">
          <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgentType()" />
        </xsl:element>
        <xsl:element name="premis:linkingAgentIdentifierValue">
          <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getApiAgentValue()" />
        </xsl:element>
      </xsl:element>
      
      <!-- linkingObjectIdentifier -->
      <xsl:element name="premis:linkingObjectIdentifier">
        <xsl:element name="premis:linkingObjectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:linkingObjectIdentifierValue">
          <xsl:call-template name="premis_representation_identifier_value" />
        </xsl:element>
      </xsl:element>
      <!-- When this is an update, then add the linking identifier to the previous metadata  -->
      <xsl:if test="field[@name='Bevarings metadata historik']">
        <xsl:element name="premis:linkingObjectIdentifier">
          <xsl:element name="premis:linkingObjectIdentifierType">
            <xsl:call-template name="premis_identifier_type" />
          </xsl:element>
          <xsl:element name="premis:linkingObjectIdentifierValue">
            <xsl:value-of select="java:dk.kb.metadata.selector.PremisUtils.getLatestHistoricGuid(
                    field[@name='Bevarings metadata historik']/value)" />
          </xsl:element>
          <xsl:element name="premis:linkingObjectRole">
            <xsl:value-of select="'source'" />
          </xsl:element>
        </xsl:element>
      </xsl:if>
    </premis:event>
  </xsl:template>
  
  <xsl:template name="premis_identifier_type">
    <xsl:value-of select="'UUID'" />
  </xsl:template>
  
  <xsl:template name="premis_object_identifier_value">
    <xsl:value-of select="java:dk.kb.metadata.utils.GuidExtractionUtils.extractGuid(field[@name='GUID']/value)" />
  </xsl:template>
  
  <xsl:template name="premis_identifier_value">
    <xsl:value-of select="field[@name='METADATA GUID']/value" />
  </xsl:template>

  <xsl:template name="premis_representation_identifier_value">
    <xsl:value-of select="field[@name='Representation metadata guid']/value" />
  </xsl:template>

  <xsl:template name="premis_intellectual_entity_for_file">
    <xsl:value-of select="field[@name='relatedObjectIdentifierValue_intellectualEntity']/value" />
  </xsl:template>

  <xsl:template name="premis_intellectual_entity_for_representation">
    <xsl:value-of select="field[@name='Representation intellectual guid']/value" />
  </xsl:template>

  <xsl:template name="premis_agent_identifier_type">
    <xsl:if test="field[@name='agentIdentifierType']">
      <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getAgentType(field[@name='agentIdentifierType']/value)" />
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="premis_agent_identifier_value">
    <xsl:if test="field[@name='agentIdentifierValue']">
      <xsl:value-of select="java:dk.kb.metadata.selector.AgentSelector.getAgentValue(field[@name='agentIdentifierValue']/value)" />
    </xsl:if>
  </xsl:template>

  <xsl:template name="premis_event_identifier_type">
    <xsl:value-of select="'UUID'" />
  </xsl:template>
  
  <xsl:template name="premis_event_identifier_value">
    <xsl:value-of select="java:dk.kb.metadata.utils.IdentifierManager.getEventIdentifier($id)" />
  </xsl:template>  

  <xsl:template name="premis_rights_identifier_type">
    <xsl:value-of select="'UUID'" />
  </xsl:template>
  
  <xsl:template name="premis_rights_identifier_value">
    <xsl:value-of select="'rightsStatementIdentifierValue'" />
  </xsl:template>  
  
  <xsl:template name="premis_relationship_for_file">
    <premis:object xsi:schemaLocation="{$PREMIS_LOCATION}" version="{$PREMIS_VERSION}">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">premis:representation</xsl:attribute>
      <!-- START 1.1 objectIdentifier -->
      <xsl:element name="premis:objectIdentifier">
        <xsl:element name="premis:objectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:objectIdentifierValue">
          <xsl:call-template name="premis_identifier_value" />
        </xsl:element>
      </xsl:element>
      <!-- END 1.1 objectIdentifier -->
      
      <!-- START 1.3 preservation level -->
      <xsl:call-template name="premis_preservation" />
      <!-- END 1.3 preservation level -->
      
      <!-- START 1.13 relationship -->
      <xsl:element name="premis:relationship">
        <xsl:element name="premis:relationshipType">
          <xsl:value-of select="'structural'" />
        </xsl:element>
        <xsl:element name="premis:relationshipSubType">
          <xsl:value-of select="'represents'" />
        </xsl:element>
        <xsl:element name="premis:relatedObjectIdentifier">
          <xsl:element name="premis:relatedObjectIdentifierType">
            <xsl:call-template name="premis_identifier_type" />
          </xsl:element>
          <xsl:element name="premis:relatedObjectIdentifierValue">
            <xsl:call-template name="premis_intellectual_entity_for_file" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END 1.13 relationship -->
    </premis:object>
  </xsl:template>
  
  <xsl:template name="premis_relationship_for_representation">
    <premis:object xsi:schemaLocation="{$PREMIS_LOCATION}" version="{$PREMIS_VERSION}">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">premis:representation</xsl:attribute>
      <!-- START 1.1 objectIdentifier -->
      <xsl:element name="premis:objectIdentifier">
        <xsl:element name="premis:objectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:objectIdentifierValue">
          <xsl:call-template name="premis_representation_identifier_value" />
        </xsl:element>
      </xsl:element>
      <!-- END 1.1 objectIdentifier -->
      
      <!-- START 1.3 preservation level -->
      <xsl:call-template name="premis_preservation" />
      <!-- END 1.3 preservation level -->
      
      <!-- START 1.13 relationship -->
      <xsl:element name="premis:relationship">
        <xsl:element name="premis:relationshipType">
          <xsl:value-of select="'structural'" />
        </xsl:element>
        <xsl:element name="premis:relationshipSubType">
          <xsl:value-of select="'represents'" />
        </xsl:element>
        <xsl:element name="premis:relatedObjectIdentifier">
          <xsl:element name="premis:relatedObjectIdentifierType">
            <xsl:call-template name="premis_identifier_type" />
          </xsl:element>
          <xsl:element name="premis:relatedObjectIdentifierValue">
            <xsl:call-template name="premis_intellectual_entity_for_representation" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END 1.13 relationship -->
    </premis:object>
  </xsl:template>
  
  <!-- Template for the premis for a catalog -->
  <xsl:template name="premis_intellectual_entity_catalog">
    <premis:object xsi:schemaLocation="{$PREMIS_LOCATION}" version="{$PREMIS_VERSION}">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">premis:representation</xsl:attribute>
      <!-- START 1.1 objectIdentifier -->
      <xsl:element name="premis:objectIdentifier">
        <xsl:element name="premis:objectIdentifierType">
          <xsl:call-template name="premis_identifier_type" />
        </xsl:element>
        <xsl:element name="premis:objectIdentifierValue">
          <xsl:value-of select="uuid" />
        </xsl:element>
      </xsl:element>
      
      <!-- START 1.13 relationship -->
      <xsl:element name="premis:relationship">
        <xsl:element name="premis:relationshipType">
          <xsl:value-of select="'structural'" />
        </xsl:element>
        <xsl:element name="premis:relationshipSubType">
          <xsl:value-of select="'represents'" />
        </xsl:element>
        <xsl:element name="premis:relatedObjectIdentifier">
          <xsl:element name="premis:relatedObjectIdentifierType">
            <xsl:value-of select="'UUID'" />
          </xsl:element>
          <xsl:element name="premis:relatedObjectIdentifierValue">
            <xsl:value-of select="ie_uuid" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END 1.13 relationship -->
    </premis:object>
  </xsl:template>
</xsl:stylesheet> 
