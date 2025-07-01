<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sign In - Viiva Banc</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/styles/signin.css">
</head>

<body>
	<jsp:include page="/pages/header.jsp" />

	<div class="whole-container">
		<div class="image-container">
			<img src="<%=request.getContextPath()%>/images/signin.png"
				alt="Sign-In Image">
		</div>
		<div class="auth-container">
			<form id="loginForm" action="login" method="post"
				class="form login-form active json-form" data-form-type="login"
				data-endpoint="<%=request.getContextPath()%>/viiva/auth/signin"
				data-allow-submit="true">
				<h2>Login</h2>
				<div class="input-box">
					<input type="email" name="email" required maxlength="30"
						pattern= "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
						title="Enter a valid email address (e.g., user@example.com)"
						placeholder=" "> <label>Email</label>
				</div>
				<div class="input-box">
					<div class="password-wrapper">
						<input type="password" name="password" required maxlength="20"
							pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,20}$"
							title="8–20 characters, at least 1 uppercase letter, 1 digit, and 1 special character"
							oninput="this.value = this.value.replace(/[^A-Za-z\d@$!%*?&#]/g, '')"
							onpaste="return false" oncopy="return false" placeholder=" ">
						<span class="toggle-password" title="Toggle Password">&#128065;</span>
					</div>
					<label>Password</label>
				</div>

				<button type="submit" class="submit-btn">Login</button>
				<div id="signin-message" class="form-message"></div>
			</form>
			<p>Don't have an account? <a href="signup.jsp" >Sign up</a></p>

		</div>
	</div>
	<script src="<%=request.getContextPath()%>/scripts/signin.js" type="module"></script>
	<jsp:include page="/pages/footer.jsp" />
	
</body>
</html>