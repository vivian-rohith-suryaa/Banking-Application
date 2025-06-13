<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<link rel="stylesheet"
	href="<%= request.getContextPath() %>/styles/header.css">
<header>
	<div class="header-container">
		<div class="header-left">
			<a href="home.jsp" class="logo-link"> <img
				src="<%= request.getContextPath() %>/images/viiva_logo.png"
				alt="Viiva Banc Logo" class="logo-img" title="viiva banc">
			</a> <a href="home.jsp" class="bank-name-link"> <img
				src="<%= request.getContextPath() %>/images/viiva_banc.png"
				alt="Viiva Banc" class="name-img" title="viiva banc">
			</a>
		</div>
		<div class="header-right">
			<a href="profile.jsp" title="View Profile"> <img
				src="images/profile_icon.svg" alt="Profile" class="profile-icon">
			</a>
		</div>
	</div>
</header>
