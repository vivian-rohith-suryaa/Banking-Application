package com.viiva.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletUtil {

	private static final Map<String, BiConsumer<Object, HttpServletRequest>> handlerExtra = new HashMap<String, BiConsumer<Object, HttpServletRequest>>();

	static {
		handlerExtra.put("com.viiva.handler.signin.SigninHandler", ServletUtil::handleAuthExtra);
		handlerExtra.put("com.viiva.handler.signup.SignupHandler", ServletUtil::handleAuthExtra);
	};


	public static void processHandlerExtras(String handlerName, Object result, HttpServletRequest request) {
		BiConsumer<Object, HttpServletRequest> action = handlerExtra.get(handlerName);

		if (action != null) {
			
			action.accept(result, request);
		}
	}

	public static void handleAuthExtra(Object result, HttpServletRequest request) {
		System.out.println("Came here");
		
		if (result instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (Map<String, Object>) result;
			HttpSession session = request.getSession();
			
			System.out.println("Setting session:");
			System.out.println("User ID: " + resultMap.get("userId"));
			System.out.println("User Type: " + resultMap.get("userType"));
			System.out.println("Session ID: " + session.getId());
			
			session.setAttribute("userId", resultMap.get("userId"));
			session.setAttribute("userType", resultMap.get("userType"));

		}
	}

	public static void checkSession(HttpServletRequest request) {
		HttpSession session = request.getSession(); // returns existing or creates new
		System.out.println("Checking session ID: " + session.getId());

		if (session != null) {
			java.util.Enumeration<String> attributeNames = session.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				String name = attributeNames.nextElement();
				Object value = session.getAttribute(name);
				System.out.println("Session Attribute: " + name + " = " + value);
			}
		} 
	}
	
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
	

	

//	String uri = request.getRequestURI();              // e.g., /viiva/user/1243
//	String contextPath = request.getContextPath();     // usually "", unless deployed in a subpath
//	String servletPath = request.getServletPath();     // e.g., /viiva
//	String relativePath = uri.substring((contextPath + servletPath).length()); // e.g., /user/1243
//	
//	System.out.println("URI: "+uri+"\nContext path: "+contextPath+"\nServlet Path: "+servletPath+"\nRelative Path: "+relativePath);
//	
//	String[] pathParts = relativePath.split("/");      // ["", "user", "1243"]
//	if (pathParts.length >= 3) {
//		request.setAttribute("resource", pathParts[1]);  // "user"
//		request.setAttribute("resourceId", pathParts[2]); // "1243"
//		System.out.println("Extracted resource: " + pathParts[1] + ", ID: " + pathParts[2]);
//	}

