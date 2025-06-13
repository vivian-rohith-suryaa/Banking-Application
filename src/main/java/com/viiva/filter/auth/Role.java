package com.viiva.filter.auth;

public final class Role {
	
	public static final byte ANONYMOUS = 0;
    public static final byte CUSTOMER = 1;
    public static final byte EMPLOYEE = 2;
    public static final byte MANAGER = 3;
    public static final byte SUPERADMIN = 4;

    public static String getName(byte code) {
        switch (code) {
            case 0: return "ANONYMOUS";
            case 1: return "CUSTOMER";
            case 2: return "EMPLOYEE";
            case 3: return "MANAGER";
            case 4: return "SUPERADMIN";
            default: return "UNKNOWN";
        }
    }
}
