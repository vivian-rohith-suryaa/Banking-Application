<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Viiva Banc</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/styles/landing.css">
</head>
<body>
	<jsp:include page="/pages/header.jsp" />

	<div class="whole-container">
		<div class="image-container">
			<img src="<%= request.getContextPath() %>/images/signup.png"
				alt="Landing Image">
		</div>
		<div class="auth-container">
			<div class="tab-switch">
				<button class="tab-btn active" id="loginTab">Login</button>
				<button class="tab-btn" id="signupTab">Signup</button>
			</div>
			<div class="form-wrapper">
				<form action="login" method="post" class="form login-form active">
					<h2>Login</h2>
					<div class="input-box">
						<input type="text" name="email" required placeholder=" ">
						<label>Email</label>
					</div>
					<div class="input-box">
						<input type="password" name="password" required placeholder=" ">
						<label>Password</label>
					</div>
					<button type="button" class="submit-btn">Login</button>
				</form>

				<form action="signup" method="post" class="form signup-form">
					<h2>Signup</h2>
					<div class="form-row">
						<div class="input-box">
							<input type="text" name="fullname" required placeholder=" ">
							<label>Full Name</label>
						</div>
						<div class="input-box">
							<select name="gender" required>
								<option value="" disabled selected hidden></option>
								<option value="Male">Male</option>
								<option value="Female">Female</option>
								<option value="Other">Other</option>
							</select> <label>Gender</label>
						</div>
					</div>
					<div class="form-row">
						<div class="input-box">
							<input type="email" name="email" required placeholder=" ">
							<label>Email</label>
						</div>
						<div class="input-box">
							<input type="text" name="phone" required placeholder=" ">
							<label>Phone</label>
						</div>
					</div>
					<div class="form-row">
						<div class="input-box">
							<input type="date" name="dob" required placeholder=" "> <label>Date
								of Birth</label>
						</div>
						<div class="input-box">
							<input type="text" name="aadhar" required placeholder=" ">
							<label>Aadhar Number</label>
						</div>
					</div>
					<div class="form-row">
						<div class="input-box">
							<input type="text" name="pan" required placeholder=" "> <label>PAN
								Number</label>
						</div>
						<div class="input-box">
							<input type="text" name="address" required placeholder=" ">
							<label>Address</label>
						</div>
					</div>
					<div class="form-row">
						<div class="input-box">
							<input type="password" name="password" required placeholder=" ">
							<label>Password</label>
						</div>
						<div class="input-box">
							<input type="password" name="confirmPassword" required
								placeholder=" "> <label>Confirm Password</label>
						</div>
					</div>
					<button type="button" class="submit-btn">Signup</button>
				</form>
			</div>
		</div>
	</div>

	<script>
    const loginTab = document.getElementById('loginTab');
    const signupTab = document.getElementById('signupTab');
    const loginForm = document.querySelector('.login-form');
    const signupForm = document.querySelector('.signup-form');

    loginTab.addEventListener('click', () => {
      loginTab.classList.add('active');
      signupTab.classList.remove('active');
      loginForm.classList.add('active');
      signupForm.classList.remove('active');
    });

    signupTab.addEventListener('click', () => {
      signupTab.classList.add('active');
      loginTab.classList.remove('active');
      signupForm.classList.add('active');
      loginForm.classList.remove('active');
    });
  </script>

	<jsp:include page="/pages/footer.jsp" />
</body>
</html>