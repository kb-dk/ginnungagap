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
    <jsp:param name="page" value="ginnungagap"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>Cumulus Preservation Service</h1>
    <h2>Ginnungagap</h2>
    <h3>Version: ${version}</h3>
</div>
<div id="conf" class="container">
  <h3>Configuration</h3>
  <div id="local" class="container">
    <h4>Local configuration:</h4>
    <div class="container">
      <p><b>localOutputDir:</b> ${localConf.getLocalOutputDir().getAbsolutePath()}</p>
      <c:if test="${localConf.getIsTest() eq true}">
        <p><b>RUNNING IN TEST MODE</b></p>
        <p><b>localArchiveDir:</b> ${localConf.getLocalArchiveDir().getAbsolutePath()}</p>
      </c:if>
    </div>
  </div>
  <div id="cumulus" class="container">
    <h4>Cumulus configuration:</h4>
    <div class="container">
      <p><b>serverUrl:</b> ${cumulusConf.getServerUrl()}</p>
      <p><b>userName:</b> ${cumulusConf.getUserName()}</p>
      <p><b>userPassword:</b> ${cumulusConf.getUserPassword() }</p>
      <p><b>Catalogs:</b></p>
      <ul>
        <c:forEach items="${cumulusConf.getCatalogs()}" var="catalog">
          <li>${catalog}</li>
        </c:forEach>
      </ul>
    </div>
  </div>
  <div id="mail" class="container">
    <h4>Mail configuration:</h4>
    <div class="container">
      <p><b>Sender:</b> ${mailConf.getSender()}</p>
      <p><b>Receivers:</b></p>
      <ul>
        <c:forEach items="${mailConf.getReceivers()}" var="receiver">
          <li>${receiver}</li>
        </c:forEach>
      </ul>
    </div>
  </div>
  <div id="bitmag" class="container">
    <h4>Bitrepository and packaging configuration:</h4>
    <div class="container">
      <p><b>componentId:</b> ${bitmagConf.getComponentId()}</p>
      <p><b>maxNumberOfFailingPillars:</b> ${bitmagConf.getMaxNumberOfFailingPillars()}</p>
      <p><b>warcFileSizeLimit:</b> ${bitmagConf.getWarcFileSizeLimit()}</p>
      <p><b>tempDir:</b> ${bitmagConf.getTempDir()}</p>
      <p><b>algorithm:</b> ${bitmagConf.getAlgorithm()}</p>
    </div>
  </div>
  <div id="transformation" class="container">
    <h4>Transformation configuration:</h4>
    <div class="container">
      <p><b>xsltDir:</b> ${transformationConf.getXsltDir()}</p>
      <p><b>xsdDir:</b> ${transformationConf.getXsdDir()}</p>
      <p><b>metadataTempDir:</b> ${transformationConf.getMetadataTempDir()}</p>
      <p><b>Required Cumulus fields:</b></p>
      <ul>
        <li>Required fields with content:</li>
        <ul>
          <div class="row_base_field">
            <div class="container">
              <c:forEach items="${transformationConf.getRequiredFields().getBaseFields()}" var="field">
                <li>${field}</li>
              </c:forEach>
            </div>
          </div>
        </ul>
        <li>Required writable fields:</li>
        <ul>
          <div class="row_base_field">
            <div class="container">
              <c:forEach items="${transformationConf.getRequiredFields().getWritableFields()}" var="field">
                <li>${field}</li>
              </c:forEach>
            </div>
          </div>
        </ul>
      </ul>
    </div>
  </div>
</div>
</body>
</html>
