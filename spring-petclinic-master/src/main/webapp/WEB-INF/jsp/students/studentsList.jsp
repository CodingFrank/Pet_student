<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="students">
    <h2>Students</h2>

    <table id="vets" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 80px;">Name</th>
            <th style="width: 120px;">Address</th>
            <th>City</th>
            <th style="width: 80px">Telephone</th>
            <th>Age</th>
            <th style="width: 80px">Department</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${selections}" var="student">
            <tr>
                <td>
                    <spring:url value="/students/{studentId}.html" var="studentUrl">
                        <spring:param name="studentId" value="${student.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(studentUrl)}"><c:out value="${student.firstName} ${student.lastName}"/></a>
                </td>
                <td>
                    <c:out value="${student.address}"/>
                </td>
                <td>
                    <c:out value="${student.city}"/>
                </td>
                <td>
                    <c:out value="${student.telephone}"/>
                </td>
                <td>
                    <c:out value="${student.age}"/>
                </td>
                <td>
                    <c:out value="${student.department}"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
