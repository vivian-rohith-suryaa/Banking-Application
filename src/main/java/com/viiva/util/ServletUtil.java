package com.viiva.util;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class ServletUtil {
	
	public static String getRelativePath(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.substring((request.getContextPath() + request.getServletPath()).length());
	}
	
	public static Map<String, Object> extractPathParams(HttpServletRequest request) {
		String relativePath = getRelativePath(request);
		String[] pathParts = relativePath.split("/");
		
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		for (int i= 1; i<pathParts.length-1; i+=2) {
			paramMap.put(pathParts[i], pathParts[i+1]);
		}
		
		return paramMap;
	}
	
}


