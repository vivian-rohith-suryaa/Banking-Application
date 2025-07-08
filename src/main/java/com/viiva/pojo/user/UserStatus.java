package com.viiva.pojo.user;

public enum UserStatus {

	INACTIVE(0), ACTIVE(1), SUSPENDED(2);

	private final byte code;

	UserStatus(int i) {
		this.code = (byte) i;
	}

	public byte getCode() {
		return code;
	}

	public static UserStatus fromCode(byte code) {
		for (UserStatus s : values()) {
			if (s.code == code) {
				return s;
			}
		}
		throw new IllegalArgumentException("Invalid Status code: " + code);
	}

}
