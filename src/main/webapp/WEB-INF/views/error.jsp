<!DOCTYPE html>
<html lang="en"
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>Ginnungagap ERROR page</title>
    <jsp:include page="includes/head.jsp" />
</head>
<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="error"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>Cumulus Preservation Service</h1>
    <h2>Ginnungagap error</h2>
</div>
<div class="container">    
    <p><b>Status code:</b> ${statusCode}</p>
    <p><b>Error message:</b> ${error.getLocalizedMessage()}</p>
    <c:forEach begin="1" end="5" step="1" varStatus="loopCounter" items="${error.getStackTrace()}" var="line" >
      <p>&emsp;${line.toString()}</p>
    </c:forEach>
    <c:if test="${not empty error.getCause()}">
      <p><b>Cause:</b> ${error.getCause().getLocalizedMessage()}</p>
      <c:forEach begin="1" end="10" step="1" varStatus="loopCounter" items="${error.getCause().getStackTrace()}" var="line2">
        <p>&emsp;${line2.toString()}</p>
      </c:forEach>
      <c:if test="${not empty error.getCause().getCause()}">
        <p><b>Cause:</b> ${error.getCause().getCause().getLocalizedMessage()}</p>
        <c:forEach begin="1" end="15" step="1" varStatus="loopCounter" items="${error.getCause().getCause().getStackTrace()}" var="line3">
          <p>&emsp;${line3.toString()}</p>
        </c:forEach>
      </c:if>
    </c:if>
</div>
</body>
</html>
