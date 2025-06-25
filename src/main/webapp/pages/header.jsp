<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/styles/header.css">

<%
    String uri = request.getRequestURI();
    boolean isLandingPage = uri.contains("signin.jsp") || uri.contains("signup.jsp");
%>

<header class="banner">
    <div class="logo-container" onclick="location.href='<%= request.getContextPath() %>/pages/home.jsp'">
        <img src="<%= request.getContextPath() %>/images/viiva_logo.png" alt="Viiva Banc Logo" class="logo" />
        <img src="<%= request.getContextPath() %>/images/viiva_banc.png" alt="Viiva Banc" class="bank-name" />
    </div>

    <% if (!isLandingPage) { %>
    <div class="header-right">
        <img src="<%= request.getContextPath() %>/icons/user.svg" alt="Profile" id="profile-icon" class="header-icon">

        <div id="profile-popup" class="profile-popup" style="display:none;">
            <img src="<%= request.getContextPath() %>/icons/popup-profile.svg" alt="Profile Picture" class="popup-profile-img">
            <div class="popup-name" id="popup-name">User Name</div>
            <div class="popup-id" id="popup-id">ID: </div>
            <div class="popup-email" id="popup-email">user@example.com</div>

            <div class="popup-actions">
                <img src="<%= request.getContextPath() %>/icons/popup-account.svg" title="Accounts" id="goto-accounts" class="popup-action-icon">
                <img src="<%= request.getContextPath() %>/icons/popup-payment.svg" title="Payments" id="goto-payments" class="popup-action-icon">
                <img src="<%= request.getContextPath() %>/icons/edit.svg" title="Edit Profile" id="edit-profile" class="popup-action-icon">
                <img src="<%= request.getContextPath() %>/icons/logout.svg" title="Logout" id="logout-btn" class="popup-action-icon">
            </div>
        </div>
    </div>
    <% } %>
</header>

<script src="<%= request.getContextPath() %>/scripts/header.js"></script>
