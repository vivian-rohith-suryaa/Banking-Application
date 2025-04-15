package com.viiva.util;

public class Utility {

	public static boolean checkNull(Object object) {
		if (object == null) {
			return true;
		}
		return false;
	}

	public static boolean checkEmpty(Object object) {
		if (object.toString().trim().isEmpty()) {
			return true;
		}
		return false;
	}
}
