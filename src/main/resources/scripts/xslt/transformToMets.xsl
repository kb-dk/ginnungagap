<?xml version="1.0" encoding="UTF-8"?> 
<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:mix="http://www.loc.gov/mix/v20"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:premis="http://www.loc.gov/premis/v3"
    xmlns:pbcore="http://www.pbcore.org/PBCore/PBCoreNamespace.html"
    
    extension-element-prefixes="java">

  <xsl:output encoding="UTF-8" method="xml" indent="yes" />

  <xsl:include href="transformToMods.xsl"/>
  <xsl:include href="transformToPbCore.xsl"/>
  <xsl:include href="transformToPremis.xsl"/>
 
  <xsl:variable name="FILE_GUID" select="java:dk.kb.metadata.utils.GuidExtractionUtils.extractGuid(record/field[@name='GUID']/value)" />
  
  <xsl:variable name="MODS-ID" select="'Mods'" />
  <xsl:variable name="PBCORE-DESCRIPTION-ID" select="'PBCoreDescription'" />
  <xsl:variable name="MODS-RIGHTS-ID" select="'ModsRights'" />
  <xsl:variable name="PREMIS-ID" select="'Premis'" />
  <xsl:variable name="PBCORE-INSTANTIATION-ID" select="'PBCoreInstantiation'" />
  <xsl:variable name="PREMIS-AGENT-ID" select="'PremisAgent'" />
  <xsl:variable name="PREMIS-EVENT-ID" select="'PremisEvent'" />
  <xsl:variable name="PREMIS-OBJECT-ID" select="'PremisObject'" />
  <xsl:variable name="PREMIS-REPRESENTATION-ID" select="'PremisRepresentation'" />
  <xsl:variable name="PREMIS-RIGHTS-ID" select="'PremisRights'" />
  
  <xsl:template match="record">
    <xsl:call-template name="mets_generator" />
  </xsl:template>
  
  <xsl:template name="mets_generator">
    <mets:mets xsi:schemaLocation="http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/version111/mets.xsd">
      <xsl:attribute name="TYPE">
        <xsl:value-of select="'File'" />
      </xsl:attribute>
      <xsl:attribute name="OBJID">
        <xsl:value-of select="field[@name='METADATA GUID']/value" />
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
      
      <!-- START dmdSec -->
      <!-- Add MODS -->
      <xsl:element name="mets:dmdSec">
        <xsl:attribute name="CREATED">
          <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
        </xsl:attribute>    
        <xsl:attribute name="ID">
          <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($MODS-ID)" />
        </xsl:attribute>
        <xsl:element name="mets:mdWrap">
          <xsl:attribute name="MDTYPE">
            <xsl:value-of select="'MODS'" />
          </xsl:attribute>
          <xsl:element name="mets:xmlData">
             <xsl:call-template name="mods" />
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- Add PBCore descriptive metadata, if format allows -->
      <xsl:if test="java:dk.kb.metadata.utils.FileFormatUtils.formatForPbCore(field[@name='formatName']/value)">
        <xsl:element name="mets:dmdSec">
          <xsl:attribute name="CREATED">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:attribute>    
          <xsl:attribute name="ID">
            <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($PBCORE-DESCRIPTION-ID)" />
          </xsl:attribute>
          <xsl:element name="mets:mdWrap">
            <xsl:attribute name="MDTYPE">
              <xsl:value-of select="'OTHER'" />
            </xsl:attribute>
            <xsl:attribute name="OTHERMDTYPE">
              <xsl:value-of select="'PBCORE'" />
            </xsl:attribute>
            <xsl:element name="mets:xmlData">
              <xsl:call-template name="pbcore_description" />      
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:if>
      <!-- END dmdSec -->

      <!-- START amdSec -->
      <xsl:element name="mets:amdSec">
        <!-- ADD PREMIS:OBJECT -->
        <xsl:element name="mets:techMD">
          <xsl:attribute name="CREATED">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:attribute>
          <xsl:attribute name="ID">
            <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($PREMIS-OBJECT-ID)" />
          </xsl:attribute>
          <xsl:element name="mets:mdWrap">
            <xsl:attribute name="MDTYPE">
              <xsl:value-of select="'PREMIS:OBJECT'" />
            </xsl:attribute>
            <xsl:element name="mets:xmlData">
              <xsl:call-template name="premis_object" />
            </xsl:element>
          </xsl:element>
        </xsl:element>
        <!-- Add PBCore technical metadata, if format allows -->
        <xsl:if test="java:dk.kb.metadata.utils.FileFormatUtils.formatForPbCore(field[@name='formatName']/value)">
          <xsl:element name="mets:techMD">
            <xsl:attribute name="CREATED">
              <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
            </xsl:attribute>    
            <xsl:attribute name="ID">
              <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($PBCORE-INSTANTIATION-ID)" />
            </xsl:attribute>
            <xsl:element name="mets:mdWrap">
              <xsl:attribute name="MDTYPE">
                <xsl:value-of select="'OTHER'" />
              </xsl:attribute>
              <xsl:attribute name="OTHERMDTYPE">
                <xsl:value-of select="'PBCORE'" />
              </xsl:attribute>
              <xsl:element name="mets:xmlData">
                <xsl:call-template name="pbcore_instantiation" />      
              </xsl:element>
            </xsl:element>
          </xsl:element>
        </xsl:if>
        <!-- ADD MODS (rights) -->
        <xsl:if test="field[@name='Copyright'] or field[@name='Copyright Notice']/value 
        or field[@name='Ophav-tabel']/table/row or field[@name='Person-tabel']/table/row">
          <xsl:comment>Inside mets:rightsMD</xsl:comment> 
          <xsl:element name="mets:rightsMD">
            <xsl:attribute name="CREATED">
              <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
            </xsl:attribute>
            <xsl:attribute name="ID">
              <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($MODS-RIGHTS-ID)" />
            </xsl:attribute>
            <xsl:element name="mets:mdWrap">
              <xsl:attribute name="MDTYPE">
                <xsl:value-of select="'MODS'" />
              </xsl:attribute>
              <xsl:element name="mets:xmlData">
                <xsl:call-template name="mods_rights" />
              </xsl:element>
            </xsl:element>
          </xsl:element>
        </xsl:if>
        <!-- ADD PREMIS:RIGHTS -->
<!--         <xsl:element name="mets:rightsMD"> -->
<!--           <xsl:attribute name="CREATED"> -->
<!--             <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" /> -->
<!--           </xsl:attribute> -->
<!--           <xsl:attribute name="ID"> -->
<!--             <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($PREMIS-RIGHTS-ID)" /> -->
<!--           </xsl:attribute> -->
<!--           <xsl:element name="mets:mdWrap"> -->
<!--             <xsl:attribute name="MDTYPE"> -->
<!--               <xsl:value-of select="'PREMIS:RIGHTS'" /> -->
<!--             </xsl:attribute> -->
<!--             <xsl:element name="mets:xmlData"> -->
<!--               <xsl:call-template name="premis_rights" /> -->
<!--             </xsl:element> -->
<!--           </xsl:element> -->
<!--         </xsl:element> -->
        <!-- ADD PREMIS:EVENT -->
        <xsl:element name="mets:digiprovMD">
          <xsl:attribute name="CREATED">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:attribute>
          <xsl:attribute name="ID">
            <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($PREMIS-EVENT-ID)" />
          </xsl:attribute>
          <xsl:element name="mets:mdWrap">
            <xsl:attribute name="MDTYPE">
              <xsl:value-of select="'PREMIS:EVENT'" />
            </xsl:attribute>
            <xsl:element name="mets:xmlData">
              <xsl:call-template name="premis_event_for_file" />
            </xsl:element>
          </xsl:element>
        </xsl:element>
        <!-- ADD PREMIS -->
        <xsl:element name="mets:digiprovMD">
          <xsl:attribute name="CREATED">
            <xsl:value-of select="java:dk.kb.metadata.utils.CalendarUtils.getCurrentDate()" />
          </xsl:attribute>
          <xsl:attribute name="ID">
            <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.createNewMdId($PREMIS-REPRESENTATION-ID)" />
          </xsl:attribute>
          <xsl:element name="mets:mdWrap">
            <xsl:attribute name="MDTYPE">
              <xsl:value-of select="'PREMIS'" />
            </xsl:attribute>
            <xsl:element name="mets:xmlData">
              <xsl:call-template name="premis_relationship_for_file" />
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END amdSec -->
      
      <!-- START fileSec -->
      <xsl:element name="mets:fileSec">
        <xsl:element name="mets:fileGrp">
          <xsl:element name="mets:file">
            <xsl:attribute name="ID">
              <xsl:value-of select="java:dk.kb.metadata.utils.FileIdHandler.getFileID($FILE_GUID)" />
            </xsl:attribute>
            <xsl:element name="mets:FLocat">
              <xsl:attribute name="LOCTYPE">
                <xsl:value-of select="'URN'" />
              </xsl:attribute>
              <xsl:attribute name="xlink:href">
                <xsl:value-of select="concat('urn:uuid:', $FILE_GUID)" />
              </xsl:attribute>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END fileSec -->
      
      <!-- START structMap -->
      <xsl:element name="mets:structMap">
        <xsl:attribute name="TYPE">
          <xsl:value-of select="'logical'" />
        </xsl:attribute>
    
        <xsl:element name="mets:div">
          <xsl:attribute name="DMDID">
            <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.getDivAttributeFor(concat($MODS-ID, ',', $PBCORE-DESCRIPTION-ID))" />
          </xsl:attribute>
          <xsl:attribute name="ADMID">
            <xsl:value-of select="java:dk.kb.metadata.utils.MdIdHandler.getDivAttributeFor(concat($MODS-RIGHTS-ID, ',', $PREMIS-ID, ',', $PREMIS-AGENT-ID, ',', $PREMIS-EVENT-ID, ',', $PREMIS-OBJECT-ID, ',', $PREMIS-RIGHTS-ID, ',', $PBCORE-INSTANTIATION-ID, ',', $PREMIS-REPRESENTATION-ID))" />
          </xsl:attribute>
          <xsl:element name="mets:fptr">
            <xsl:attribute name="FILEID">
              <xsl:value-of select="java:dk.kb.metadata.utils.FileIdHandler.getFileID($FILE_GUID)" />
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- END structMap -->
      
    </mets:mets>
  </xsl:template>
</xsl:transform> 
