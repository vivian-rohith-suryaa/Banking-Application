package com.viiva.filter.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.viiva.filter.loader.AuthRegistryLoader;
import com.viiva.util.BasicUtil;

public class AuthFilter implements Filter{
	
	private AuthRegistry authRegistry;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.authRegistry = AuthRegistryLoader.getAuthRegistry();
		System.out.println("Auth Filter Initialised.");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String path = httpRequest.getRequestURI();
		String method = httpRequest.getMethod();
		
		System.out.println("\nRequest URI: " + path);
		System.out.println("Request method: " + method);
		
		Optional<AuthRule> matchedRuleOpt = authRegistry.getAuth().stream()
				.filter(rule -> BasicUtil.pathMatches(rule.getPath(), path) && rule.getMethod().equalsIgnoreCase(method))
				.findFirst();
		
		if (!matchedRuleOpt.isPresent()) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access rule not defined.");
			return;
		}
		
		AuthRule matchedRule = matchedRuleOpt.get();
		List<Integer> allowedRoles = matchedRule.getRoles();
		
		if (allowedRoles.contains(0)) {
			chain.doFilter(httpRequest, httpResponse);
			return;
		}
		
		HttpSession session = httpRequest.getSession(false);
		if (session == null || session.getAttribute("role") == null) {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: No session or role.");
			return;
		}
		
		byte userRole = ((Number) session.getAttribute("role")).byteValue();
		
		boolean accessAllowed = allowedRoles.stream()
		        .mapToInt(Integer::intValue)
		        .anyMatch(allowed -> allowed == userRole);

		    if (!accessAllowed) {
		        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Access denied.");
		        return;
		    }

		System.out.println("Auth Filter passed for role: " + userRole);
		chain.doFilter(httpRequest, httpResponse);
		
	}


}
