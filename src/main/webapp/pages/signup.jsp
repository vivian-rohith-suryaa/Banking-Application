<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sign Up - Viiva Banc</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/styles/signup.css">
</head>
<body>
	<jsp:include page="/pages/header.jsp" />
	<div class="whole-container">
		<div class="image-container">
			<img src="<%=request.getContextPath()%>/images/signin.png"
				alt="Sign-Up Image">
		</div>
		<div class="auth-container">
			<form id="signupForm" action="signup" method="post"
				class="form signup-form json-form" data-form-type="signup"
				data-endpoint="<%=request.getContextPath()%>/viiva/auth/signup"
				data-allow-submit="true">
				<h2>Signup</h2>
				<div class="form-row">
					<div class="input-box">
						<input type="text" name="name" required maxlength="40"
							oninput="this.value = this.value.replace(/[^A-Za-z ]/g, '')"
							pattern="^[A-Za-z]+(?: [A-Za-z]+)*$"
							title="Name should contain only letters and spaces. Minimum 2 characters."
							placeholder=" "> <label>Name<span class="required">*</span></label>
					</div>
					<div class="input-box">
						<select name="gender" required
							title="Select gender: MALE, FEMALE, or OTHERS">
							<option value="" disabled selected hidden></option>
							<option value="MALE">Male</option>
							<option value="FEMALE">Female</option>
							<option value="OTHER">Other</option>
						</select> <label>Gender<span class="required">*</span></label>
					</div>
				</div>
				<div class="form-row">
					<div class="input-box">
						<input type="email" name="email" required maxlength="30"
							pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
							title="Enter a valid email address (e.g., user@example.com)"
							placeholder=" "> <label>Email<span class="required">*</span></label>
					</div>
					<div class="input-box">
						<input type="text" name="phone" required maxlength="10" pattern="^\d{10}$"
							oninput="this.value = this.value.replace(/[^0-9]/g, '')"
							title="Enter a valid 10-digit mobile number" placeholder=" ">
						<label>Phone<span class="required">*</span></label>
					</div>
				</div>
				<div class="form-row">
					<div class="input-box">
						<input type="date" name="dob" required
							title="Select your date of birth (must not be empty)"
							placeholder=" "> <label>Date of Birth<span class="required">*</span></label>
					</div>
					<div class="input-box">
						<input type="text" name="aadhar" required maxlength="12" pattern="^\d{12}$"
							oninput="this.value = this.value.replace(/[^0-9]/g, '')"
							title="Enter a valid 12-digit Aadhaar number" placeholder=" ">
						<label>Aadhar Number<span class="required">*</span></label>
					</div>
				</div>
				<div class="form-row">
					<div class="input-box">
						<input type="text" name="pan" required maxlength="10"
							pattern="^[A-Z]{5}[0-9]{4}[A-Z]$"
							oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')"
							title="Enter a valid PAN number (e.g., ABCDE1234F)"
							placeholder=" "> <label>PAN Number<span class="required">*</span></label>

					</div>
					<div class="input-box">
						<input type="text" name="address" required maxlength="80"
							title="Enter your current residential address"
							pattern="^[a-zA-Z0-9\s,.\-/#()]{5,100}$" placeholder=" ">
						<label>Address<span class="required">*</span></label>
					</div>
				</div>
				<div class="form-row">
					<div class="input-box">
						<div class="password-wrapper">
							<input type="password" name="password" id="password" required maxlength="20"
								pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,20}$"
								oninput="this.value = this.value.replace(/[^A-Za-z\d@$!%*?&#]/g, '')"
								onpaste="return false" oncopy="return false"
								title="8–20 characters, at least 1 uppercase letter, 1 digit, and 1 special character"
								placeholder=" "> <span class="toggle-password"
								title="Show Password">&#128065;</span>
						</div>
						<label>Password<span class="required">*</span></label>
					</div>
					<div class="input-box">
						<div class="password-wrapper">
							<input type="password" name="confirmPassword" id="confirmPassword" required maxlength="20"
								pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,20}$"
								oninput="validateConfirmPassword()" onpaste="return false"
								oncopy="return false"
								title="Re-enter the same password for confirmation"
								placeholder=" "> <span class="toggle-password"
								title="Toggle Password">&#128065;</span>
						</div>
						<label>Confirm Password<span class="required">*</span></label>

					</div>
				</div>
				<button type="submit" class="submit-btn">Signup</button>
				<div id="signup-message" class="form-message"></div>
			</form>
			<p>Already have an account? <a href="signin.jsp">Sign in</a></p>
		</div>
	</div>
	<script src="<%=request.getContextPath()%>/scripts/signup.js" type="module"></script>
	<jsp:include page="/pages/footer.jsp" />
</body>
</html>