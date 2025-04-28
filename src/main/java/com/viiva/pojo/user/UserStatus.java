package com.viiva.pojo.user;

public enum UserStatus {

	INACTIVE(0), ACTIVE(1), SUSPENDED(2);

	private final int code;

	UserStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static UserStatus fromCode(int code) {
		for (UserStatus s : values()) {
			if (s.code == code) {
				return s;
			}
		}
		throw new IllegalArgumentException("Invalid Status code: " + code);
	}

}
