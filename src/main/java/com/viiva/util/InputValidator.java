package com.viiva.util;

import com.viiva.pojo.user.Gender;
import com.viiva.wrapper.signin.SigninRequest;
import com.viiva.wrapper.signup.SignupRequest;

public class InputValidator {

	public static boolean isValidName(String name) {
		return !(BasicUtil.isBlank(name) && name.matches("^([A-Za-z]{1,})( [A-Za-z]{1,})*$"));
	}

	public static boolean isValidEmail(String email) {
		return !(BasicUtil.isBlank(email)) && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
	}

	public static boolean isValidPhone(String phone) {
		return !(BasicUtil.isBlank(phone)) && phone.matches("^\\d{10}$");
	}

	public static boolean isStrongPassword(String password) {
		return !(BasicUtil.isBlank(password))
				&& password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,20}$");
	}

	public static boolean isValidAadhar(String aadhar) {
		return !(BasicUtil.isBlank(aadhar)) && aadhar.matches("\\d{12}");
	}

	public static boolean isValidPan(String pan) {
		return !(BasicUtil.isBlank(pan) && pan.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$"));
	}

	public static String validateUser(SignupRequest data) {
		StringBuilder validationResult = new StringBuilder("");
		String name = data.getUser().getName();
		String email = data.getUser().getEmail();
		String phone = data.getUser().getPhone();
		String password = data.getUser().getPassword();
		Gender gender = data.getUser().getGender();
		String aadhar = data.getCustomer().getAadhar();
		String pan = data.getCustomer().getPan();

		if (!isValidName(name)) {
			validationResult.append("\nInvalid Name: " + name);
		}
		if (!isValidEmail(email)) {
			validationResult.append("\nInvalid Email Format: " + email);
		}
		if (!isValidPhone(phone)) {
			validationResult.append("\nInvalid Phone Number: " + phone);
		}
		if (!isStrongPassword(password)) {
			validationResult.append("\nPassword: " + password
					+ ". The password must be 8 to 20 characters long, include at least one uppercase letter, at least one number, and at least one special character (@$!%*?&#).");
		}
		if (BasicUtil.isBlank(gender)) {
			validationResult.append("\nNull/Empty Gender: " + gender);
		}
		if (!isValidAadhar(aadhar)) {
			validationResult.append("\nInvalid Aadhar Number:" + aadhar);
		}
		if (!isValidPan(pan)) {
			validationResult.append("\nInvalid PAN Number: " + pan);
		}

		return validationResult.toString();
	}
	
	public static String validateSignin(SigninRequest data) {
		StringBuilder validationResult = new StringBuilder("");
		String email = data.getEmail();
		String password = data.getPassword();
		
		if (!isValidEmail(email)) {
			validationResult.append("\nInvalid Email: " + email);
		}
		if (!isStrongPassword(password)) {
			validationResult.append("\nInvalid Password: " + password);
		}
		
		return validationResult.toString();
		
	}

}
