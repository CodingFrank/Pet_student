<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="students">

    <h2>Student Information</h2>


    <table class="table table-striped">
        <tr>
            <th>Name</th>
            <td><b><c:out value="${student.firstName} ${student.lastName}"/></b></td>
        </tr>
        <tr>
            <th>Address</th>
            <td><c:out value="${student.address}"/></td>
        </tr>
        <tr>
            <th>City</th>
            <td><c:out value="${student.city}"/></td>
        </tr>
        <tr>
            <th>Telephone</th>
            <td><c:out value="${student.telephone}"/></td>
        </tr>
    </table>

    <spring:url value="{studentId}/edit.html" var="editUrl">
        <spring:param name="studentId" value="${student.id}"/>
    </spring:url>
    <a href="${fn:escapeXml(editUrl)}" class="btn btn-default">Edit Student</a>

    <br/>
    <br/>
    <br/>
</petclinic:layout>
