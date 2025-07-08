<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/styles/header.css">

<%
    String uri = request.getRequestURI();
    boolean isLandingPage = uri.contains("signin.jsp") || uri.contains("signup.jsp");
%>

<header class="banner">
    <div class="logo-container">
        <img src="<%= request.getContextPath() %>/images/viiva_logo.png" alt="Viiva Banc Logo" class="logo" />
        <img src="<%= request.getContextPath() %>/images/viiva_banc.png" alt="Viiva Banc" class="bank-name" />
    </div>

    <% if (!isLandingPage) { %>
    <div class="header-right">
        <img src="<%= request.getContextPath() %>/icons/user.svg" alt="Profile" id="profile-icon" class="header-icon">
    </div>
    <% } %>
</header>

<script src="<%= request.getContextPath() %>/scripts/header.js"></script>
