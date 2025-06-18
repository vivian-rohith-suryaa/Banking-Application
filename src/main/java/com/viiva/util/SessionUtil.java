package com.viiva.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.viiva.exceptions.AuthException;

import java.util.HashMap;
import java.util.Map;

public class SessionUtil {
	
	public static void setupUserSession(HttpServletRequest request, Map<String, Object> loginData) {

		HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true); 
        Long userId = (Long) loginData.get("userId");
        byte role = ((Number) loginData.get("role")).byteValue();

        session.setAttribute("userId", userId);
        session.setAttribute("role", role);

        if (!BasicUtil.isNull(role) && (role == 2 || role == 3 || role == 4)) {
            Object branchId = loginData.get("branchId");
            if (branchId != null) {
                session.setAttribute("branchId", branchId);
                System.out.println("[SESSION] Created new session for userId=" + userId + ", role=" + role + ", branchId: "+branchId);
            }
        }

        System.out.println("[SESSION] Created new session for userId=" + userId + ", role=" + role);
    }
	
	public static Map<String, Object> extractSessionAttributes(HttpServletRequest request) throws AuthException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || session.getAttribute("role") == null) {
            throw new AuthException("Unauthorized: No valid session found.");
        }

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("userId", session.getAttribute("userId"));
        sessionData.put("role", session.getAttribute("role"));
        if (session.getAttribute("branchId") != null) {
            sessionData.put("branchId", session.getAttribute("branchId"));
        }
        return sessionData;
    }
}
