<!DOCTYPE html>
<html lang="en"
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>Ginnungagap</title>
    <jsp:include page="includes/head.jsp" />
</head>
<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="workflow"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>Cumulus Preservation Service Workflow</h1>
    <h2>Ginnungagap</h2>
</div>
<div id="conf" class="container">
  <div id="local" class="container">
    <h4>Local configuration:</h4>
    <p><b>localOutputDir:</b> ${conf.getLocalConfiguration().getLocalOutputDir().getAbsolutePath()}</p>
    <c:if test="${conf.getLocalConfiguration().getIsTest() eq true}">
      <p><b>RUNNING IN TEST MODE</b></p>
      <p><b>localArchiveDir:</b> ${conf.getLocalConfiguration().getLocalArchiveDir().getAbsolutePath()}</p>
    </c:if>
  </div>
  <div id="cumulus" class="container">
    <h4>Cumulus configuration:</h4>
    <p><b>serverUrl:</b> ${conf.getCumulusConf().getServerUrl()}</p>
    <p><b>userName:</b> ${conf.getCumulusConf().getUserName()}</p>
    <p><b>userPassword:</b> ************** </p>
    <p><b>Catalogs:</b></p>
    <ul>
      <div class="row">
        <c:forEach items="${conf.getCumulusConf().getCatalogs()}" var="catalog">
          <li>${catalog}</li>
        </c:forEach>
      </div>
    </ul>
  </div>
  <div id="bitmag" class="container">
    <h4>Bitrepository and packaging configuration:</h4>
    <p><b>componentId:</b> ${conf.getBitmagConf().getComponentId()}</p>
    <p><b>maxNumberOfFailingPillars:</b> ${conf.getBitmagConf().getMaxNumberOfFailingPillars()}</p>
    <p><b>warcFileSizeLimit:</b> ${conf.getBitmagConf().getWarcFileSizeLimit()}</p>
    <p><b>tempDir:</b> ${conf.getBitmagConf().getTempDir()}</p>
    <p><b>algorithm:</b> ${conf.getBitmagConf().getAlgorithm()}</p>
  </div>
  <div id="transformation" class="container">
    <h4>Transformation configuration:</h4>
    <p><b>xsltDir:</b> ${conf.getTransformationConf().getXsltDir()}</p>
    <p><b>xsdDir:</b> ${conf.getTransformationConf().getXsdDir()}</p>
    <p><b>metadataTempDir:</b> ${conf.getTransformationConf().getMetadataTempDir()}</p>
    <p><b>Required Cumulus fields:</b></p>
    <ul>
      <li>Required fields with content:</li>
      <ul>
        <div class="row_base_field">
          <c:forEach items="${conf.getTransformationConf().getRequiredFields().getBaseFields()}" var="field">
            <li>${field}</li>
          </c:forEach>
        </div>
      </ul>
      <li>Required writable fields:</li>
      <ul>
        <div class="row_base_field">
          <c:forEach items="${conf.getTransformationConf().getRequiredFields().getWritableFields()}" var="field">
            <li>${field}</li>
          </c:forEach>
        </div>
      </ul>
    </ul>
  </div>
</div>
</body>
</html>
