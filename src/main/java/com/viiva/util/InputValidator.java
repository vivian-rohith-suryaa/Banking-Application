package com.viiva.util;

import com.viiva.pojo.branch.Branch;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.user.Gender;
import com.viiva.pojo.user.User;
import com.viiva.wrapper.signin.SigninRequest;
import com.viiva.wrapper.user.UserWrapper;

public class InputValidator {

	public static boolean isValidName(String name) {
		 return !(BasicUtil.isBlank(name)) && name.matches("^([A-Za-z]{1,})( [A-Za-z]{1,})*$");
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
		return !(BasicUtil.isBlank(pan)) && pan.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");
	}
	
	public static boolean isValidAddress(String address) {
		return !(BasicUtil.isBlank(address)) && address.matches("^[a-zA-Z0-9\\s,.\\-/#()]{5,100}$");
	}

	public static StringBuilder validateUser(UserWrapper data) {
		StringBuilder validationResult = new StringBuilder("");
		String name = data.getUser().getName();
		String email = data.getUser().getEmail();
		String phone = data.getUser().getPhone();
		Gender gender = data.getUser().getGender();
		String aadhar = data.getCustomer().getAadhar();
		String pan = data.getCustomer().getPan();
		String address = data.getCustomer().getAddress();
		
		if (!isValidName(name)) {
			validationResult.append("Invalid Name. ");
		}
		if (!isValidEmail(email)) {
			validationResult.append("Invalid Email Format. ");
		}
		if (!isValidPhone(phone)) {
			validationResult.append("Invalid Phone Number. ");
		}
		if (BasicUtil.isBlank(gender)) {
			validationResult.append("Invalid Gender. ") ;
		}
		if (!isValidAadhar(aadhar)) {
			validationResult.append("Invalid Aadhar Number. ") ;
		}
		if (!isValidPan(pan)) {
			validationResult.append("Invalid PAN Number. ") ;
		}
		
		if(!isValidAddress(address)) {
			validationResult.append("Invalid Address. ");
		}

		return validationResult;
	}
	
	public static StringBuilder validateSignin(SigninRequest data) {
		StringBuilder validationResult = new StringBuilder("");
		String email = data.getEmail();
		String password = data.getPassword();
		
		if (!isValidEmail(email)) {
			validationResult.append("Invalid Email. ") ;
		}
		if (!isStrongPassword(password)) {
			validationResult.append("Invalid Password. ") ;
		}
		
		return validationResult;
		
	}
	
	public static StringBuilder validateEmployee(User data) {
		
		StringBuilder validationResult = new StringBuilder("");
		String name = data.getName();
		String email = data.getEmail();
		String phone = data.getPhone();
		Gender gender = data.getGender();
		
		if (!isValidName(name)) {
			validationResult.append("Invalid Name. ");
		}
		if (!isValidEmail(email)) {
			validationResult.append("Invalid Email Format. ");
		}
		if (!isValidPhone(phone)) {
			validationResult.append("Invalid Phone Number. ");
		}
		if (BasicUtil.isBlank(gender)) {
			validationResult.append("Invalid Gender. ") ;
		}
		
		return validationResult;
	}
	
	public static StringBuilder validateAddress(Branch data) {
		StringBuilder validationResult = new StringBuilder("");
		
		StringBuilder address = new StringBuilder();
		String locality = data.getLocality();
		String district = data.getDistrict();
		String state = data.getState();
		
		if (locality != null && !locality.isEmpty()) {
			address.append(locality);
		}
		if (district != null && !district.isEmpty()) {
		    if (address.length() > 0) address.append(", ");
		    address.append(district);
		}
		if (state != null && !state.isEmpty()) {
		    if (address.length() > 0) address.append(", ");
		    address.append(state);
		}
		
		if(!isValidAddress(address.toString())){
			validationResult.append("Invalid Address. ");
		}
		
		return validationResult;
		
	}
	

}
