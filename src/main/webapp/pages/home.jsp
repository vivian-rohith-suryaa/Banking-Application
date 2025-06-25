<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    HttpSession sess = request.getSession(false);
    long userId = (sess != null && sess.getAttribute("userId") != null) ? (long) sess.getAttribute("userId") : -1;
    long branchId = (sess != null && sess.getAttribute("branchId") != null) ? (long) sess.getAttribute("branchId") : -1;
    byte role = (sess != null && sess.getAttribute("role") != null) ? (byte) sess.getAttribute("role") : 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Viiva Banc</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/header.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/footer.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/styles/home.css">
    <script>
        const sessionUserId = <%= userId %>;
        const sessionBranchId = <%= branchId %>;
        const sessionRole = <%= role %>;
        const contextPath = "<%= request.getContextPath() %>";
    </script>
</head>
<body>
<%@ include file="header.jsp" %>

<div class="dashboard-wrapper">
    <aside class="sidebar">
        <div class="menu-item" data-page="dashboard.jsp">
            <img src="<%=request.getContextPath()%>/icons/home.svg" alt="Home">
            <span>Home</span>
        </div>
        <div class="menu-item" data-page="profile.jsp">
            <img src="<%=request.getContextPath()%>/icons/profile.svg" alt="Profile">
            <span>Profile</span>
        </div>
        <div class="menu-item" data-page="account.jsp">
            <img src="<%=request.getContextPath()%>/icons/account.svg" alt="Accounts">
            <span>Accounts</span>
        </div>
        <div class="menu-item" data-page="payment.jsp">
            <img src="<%=request.getContextPath()%>/icons/payment.svg" alt="Payments">
            <span>Payments</span>
        </div>
        <div class="menu-item" data-page="statement.jsp">
            <img src="<%=request.getContextPath()%>/icons/statement.svg" alt="Statement">
            <span>Statement</span>
        </div>
        <div class="menu-item" data-page="branch.jsp">
            <img src="<%=request.getContextPath()%>/icons/branch.svg" alt="Branch">
            <span>Branch</span>
        </div>
        <% if (role >= 2) { %>
        <div class="menu-item" data-page="employee.jsp">
            <img src="<%=request.getContextPath()%>/icons/employee.svg" alt="Employee">
            <span>Manage</span>
        </div>
        <% } %>
    </aside>

    <main class="main-content" id="main-content"></main>
</div>

<%@ include file="footer.jsp" %>
<script>
    window.sessionUserId = <%= session.getAttribute("userId") != null ? session.getAttribute("userId") : -1 %>;
    window.contextPath = "<%= request.getContextPath() %>";
    window.sessionBranchId = <%= branchId %>;
    window.sessionRole = <%= role %>;
</script>

<script type="module" src="<%=request.getContextPath()%>/scripts/home.js"></script>
</body>
</html>
