<!DOCTYPE html>
<html lang="en"
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var = "disable_workflow" value = "false" />
<head>
    <title>Validation Workflow</title>
    <jsp:include page="includes/head.jsp" />
    <meta http-equiv="Refresh" content="30">
</head>
<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="workflow"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>Cumulus Preservation Service: Validation Workflow</h1>
    <h2>Ginnungagap</h2>
</div>
<div id="main" class="container">
  <div id="workflow" class="container">
    <p><b>Name:</b> ${workflow.getName()}</p>
    <p><b>Description:</b> ${workflow.getDescription()}</p>
    <p><b>Current state:</b> ${workflow.getState()}</p>
    <p><b>Next run:</b> ${workflow.getNextRunDate()}</p>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name of step</th>
            <th>State</th>
            <th>Time for last run (in millis)</th>
            <th>Results for last run</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${workflow.getSteps()}" var="step">
            <c:if test="${step.getStatus() eq 'Running'}">
                <c:set var = "disable_workflow" value = "true"/>
            </c:if>
            <tr>
                <td>${step.getName()}</td>
                <td>${step.getStatus()}</td>
                <td>${step.getExecutionTime()}</td>
                <td>${step.getResultOfLastRun()}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <form action="${pageContext.request.contextPath}/validation/run" method="post">
        <button type="submit" class="btn btn-success" <c:if test="${disable_workflow eq true}">disabled</c:if> >Run Workflow</button>
    </form>
  </div>
</div>
</body>
</html>
