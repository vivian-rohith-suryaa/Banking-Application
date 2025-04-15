package com.viiva.filter.cors;

import java.util.List;

public class CorsConfig {

	private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private boolean allowCredentials;

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}
	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}
	public List<String> getAllowedMethods() {
		return allowedMethods;
	}
	public void setAllowedMethods(List<String> allowedMethods) {
		this.allowedMethods = allowedMethods;
	}
	public List<String> getAllowedHeaders() {
		return allowedHeaders;
	}
	public void setAllowedHeaders(List<String> allowedHeaders) {
		this.allowedHeaders = allowedHeaders;
	}
	public boolean isAllowCredentials() {
		return allowCredentials;
	}
	public void setAllowCredentials(boolean allowCredentials) {
		this.allowCredentials = allowCredentials;
	}


}
