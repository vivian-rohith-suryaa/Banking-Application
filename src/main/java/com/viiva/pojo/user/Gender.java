package com.viiva.pojo.user;

public enum Gender {
	MALE, FEMALE, OTHERS;
	
	public static Gender fromString(String value) {
	    if (value == null) return OTHERS;
	    try {
	        return Gender.valueOf(value.toUpperCase());
	    } catch (IllegalArgumentException e) {
	        return OTHERS;
	    }
	}
	
}
