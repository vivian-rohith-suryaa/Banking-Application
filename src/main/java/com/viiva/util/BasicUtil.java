package com.viiva.util;

import org.mindrot.jbcrypt.BCrypt;

public class BasicUtil {

	public static boolean isNull(Object object) {
		return object == null;
	}

	public static boolean isBlank(Object object) {
		return isNull(object) || object.toString().trim().isEmpty();
	}

	public static String encrypt(String password) throws Exception {

		String salt = BCrypt.gensalt(12);

		String hashedPassword = BCrypt.hashpw(password, salt);

		return hashedPassword;
	}
	
	public static boolean checkPassword(String password, String storedHash) {
		return BCrypt.checkpw(password, storedHash);
	}
	
}
