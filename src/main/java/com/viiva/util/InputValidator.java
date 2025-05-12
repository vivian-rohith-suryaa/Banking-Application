package com.viiva.util;

import com.viiva.pojo.user.Gender;
import com.viiva.wrapper.signin.SigninRequest;
import com.viiva.wrapper.user.UserWrapper;

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

	public static StringBuilder validateUser(UserWrapper data) {
		StringBuilder validationResult = new StringBuilder("");
		String name = data.getUser().getName();
		String email = data.getUser().getEmail();
		String phone = data.getUser().getPhone();
		Gender gender = data.getUser().getGender();
		String aadhar = data.getCustomer().getAadhar();
		String pan = data.getCustomer().getPan();
		
		if (!isValidName(name)) {
			validationResult.append("Invalid Name: " + name).append(" || ");
		}
		if (!isValidEmail(email)) {
			validationResult.append("Invalid Email Format: " + email).append(" || ");
		}
		if (!isValidPhone(phone)) {
			validationResult.append("Invalid Phone Number: " + phone).append(" || ");
		}
		if (BasicUtil.isBlank(gender)) {
			validationResult.append("Invalid Gender: " + gender).append(" || ");
		}
		if (!isValidAadhar(aadhar)) {
			validationResult.append("Invalid Aadhar Number:" + aadhar).append(" || ");
		}
		if (!isValidPan(pan)) {
			validationResult.append("Invalid PAN Number: " + pan).append(" || ");
		}

		return validationResult;
	}
	
	public static StringBuilder validateSignin(SigninRequest data) {
		StringBuilder validationResult = new StringBuilder("");
		String email = data.getEmail();
		String password = data.getPassword();
		
		if (!isValidEmail(email)) {
			validationResult.append("Invalid Email: " + email).append(" || ");
		}
		if (!isStrongPassword(password)) {
			validationResult.append("Invalid Password: " + password).append(" || ");
		}
		
		return validationResult;
		
	}
	

}
