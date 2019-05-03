<!DOCTYPE html>
<html lang="en"
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>Metadata</title>
    <jsp:include page="includes/head.jsp" />
</head>
<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="metadata"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>Cumulus Preservation Service</h1>
    <h2>Ginnungagap</h2>
</div>
<div id="conf" class="container">
  <div class="container">
    <form action="${pageContext.request.contextPath}/metadata/extract">
      <p><b>ID:</b> <input type="text" name="ID" /></p>
      <p><b>ID Type:</b>
        <input type="radio" id="idType1" name="idType" value="UUID" checked>
        <label for="idType1">UUID</label>
        <input type="radio" id="idType2" name="idType" value="NAME">
        <label for="idType2">Record Name</label>
      </p>
      <p><b>Catalog:</b>
        <select name="catalog">
          <c:forEach items="${catalogs}" var="catalog">
            <option value="${catalog}">${catalog}</option>
          </c:forEach>
        </select>
      </p>
      <p><b>Metadata Type:</b>
        <input type="radio" id="metadataType1" name="metadataType" value="METS" checked>
        <label for="metadataType1">METS</label>
        <input type="radio" id="metadataType2" name="metadataType" value="KBIDS">
        <label for="metadataType2">KBIDS</label>
      </p>
      <p><b>Extract/Create</b>
        <input type="radio" id="source1" name="source" value="cumulus" checked>
        <label for="source1">Cumulus</label>
        <input type="radio" id="source2" name="source" value="archive">
        <label for="source2">Archive</label>
      </p>
      <button type="retrieve" class="primary submit-btn">Retrieve</button>
    </form>
  </div>
</div>
</body>
</html>
