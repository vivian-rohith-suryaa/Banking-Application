package com.viiva.filter.loader;

import com.viiva.filter.auth.AuthRegistry;

public class AuthRegistryLoader {
	
	private static final String AUTH_CONFIG_PATH = "config/auth.yaml";
	private static AuthRegistry authRegistry;
	
	static {
		authRegistry = AuthYamlLoader.loadAuthRegistry(AUTH_CONFIG_PATH);
	}
	
	public static AuthRegistry getAuthRegistry() {
		return authRegistry;
	}

}
