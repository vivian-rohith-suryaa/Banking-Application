package com.viiva.pojo.user;

public enum UserType {

	CUSTOMER(0), EMPLOYEE(1), MANAGER(2), SUPERADMIN(3);

	private final int code;

	UserType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static UserType fromCode(int code) {
		for (UserType t : values()) {
			if (t.code == code) {
				return t;
			}
		}
		throw new IllegalArgumentException("Invalid Status code: " + code);
	}

}
