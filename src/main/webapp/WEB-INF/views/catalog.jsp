<!DOCTYPE html>
<html lang="en"
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>Catalog preservation</title>
    <jsp:include page="includes/head.jsp" />
</head>
<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="catalog"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>Catalog structmap Preservation</h1>
    <h2>Ginnungagap</h2>
</div>
<div id="conf" class="container">
  <div class="container">
    <form>
      <p><b>Catalog:</b>
        <select name="catalog">
          <c:forEach items="${catalogs}" var="catalog">
            <option value="${catalog}">${catalog}</option>
          </c:forEach>
        </select>
      </p>
      <p><b>ID:</b> <input type="text" name="ieID" /></p>
      <p><b>Allow subset (will not fail on unprepared records):</b>
        <input type="radio" id="allow1" name="allowSubSet" value="TRUE">
        <label for="allow1">Allow</label>
        <input type="radio" id="allow2" name="allowSubSet" value="FALSE" checked>
        <label for="allow2">Fail</label>
      </p>

      <p><b>Bitrepository collectionID:</b> <input type="text" name="collectionID" /></p>
      <button type="submit" formaction="${pageContext.request.contextPath}/catalog/extract" class="primary submit-btn">Extract</button>
      <button type="submit" formaction="${pageContext.request.contextPath}/catalog/preserve" class="primary submit-btn">Preserve</button>
    </form>
  </div>
</div>
</body>
</html>
