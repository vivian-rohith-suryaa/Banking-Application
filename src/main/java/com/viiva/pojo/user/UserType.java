package com.viiva.pojo.user;

public enum UserType {

	CUSTOMER(0), EMPLOYEE(1), MANAGER(2), SUPERADMIN(3);

	private final byte code;

	UserType(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	public static UserType fromCode(byte code) {
		for (UserType t : values()) {
			if (t.code == code) {
				return t;
			}
		}
		throw new IllegalArgumentException("Invalid Status code: " + code);
	}

}
