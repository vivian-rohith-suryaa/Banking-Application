<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/styles/users.css">

<div class="employee-data-panel">
    <div class="section-header">
        <h2 id="section-title">Users</h2>
        <div class="filters">
            <select id="filter-type"></select>
            <input type="text" id="filter-value" placeholder="Enter value" />
            <button id="filter-button">Search</button>
            <button id="clear-button">Clear</button>
        </div>
    </div>
    <div class="data-table-wrapper">
        <table id="data-table">
            <thead></thead>
            <tbody id="data-body"></tbody>
        </table>
    </div>
    <div class="data-pagination">
        <button id="prev-page">Previous</button>
        <span id="page-info">Page 1</span>
        <button id="next-page">Next</button>
    </div>
</div>

<script type="module">
    const contextPath = "<%= request.getContextPath() %>";
    const userId = <%= userId %>;
    const role = <%= role %>;
    const branchId = <%= branchId %>;

    import(`${contextPath}/scripts/users.js`)
        .then(mod => mod.initUsersModule(contextPath, userId, role, branchId));
</script>
