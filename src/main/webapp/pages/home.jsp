<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%
    byte role = session != null && session.getAttribute("role") != null ? (byte) session.getAttribute("role") : 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Viiva Banc</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/footer.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/home.css">
</head>
<body>
    <%@ include file="header.jsp" %>

    <aside class="sidebar">
        <div class="menu-item active">
            <img src="<%= request.getContextPath() %>/icons/profile.svg" alt="Profile">
            <a href="profile.jsp">Profile</a>
        </div>
        <div class="menu-item">
            <img src="<%= request.getContextPath() %>/icons/account.svg" alt="Accounts">
            <a href="account.jsp">Accounts</a>
        </div>
        <div class="menu-item">
            <img src="<%= request.getContextPath() %>/icons/payment.svg" alt="Payments">
            <a href="#">Payments</a>
        </div>
        <div class="menu-item">
            <img src="<%= request.getContextPath() %>/icons/transaction.svg" alt="Transactions">
            <a href="#">Transactions</a>
        </div>
        <div class="menu-item">
            <img src="<%= request.getContextPath() %>/icons/statement.svg" alt="Statement">
            <a href="#">Statement</a>
        </div>
        <% if (role >= 2) { %>
        <div class="menu-item">
            <img src="<%= request.getContextPath() %>/icons/employee.svg" alt="Employee">
            <a href="#">Employee</a>
        </div>
        <div class="menu-item">
            <img src="<%= request.getContextPath() %>/icons/branch.svg" alt="Branch">
            <a href="#">Branch</a>
        </div>
        <% } %>
    </aside>

    <main class="main-content">
    	<%@ include file="profile.jsp" %>
    </main>

    <%@ include file="footer.jsp" %>
</body>
</html>
