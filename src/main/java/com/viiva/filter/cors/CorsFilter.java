package com.viiva.filter.cors;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.viiva.filter.loader.CorsConfigLoader;
import com.viiva.util.Utility;

public class CorsFilter implements Filter {

	private CorsConfig corsConfig;

	@Override
	public void init(FilterConfig filterConfig) {
		this.corsConfig = CorsConfigLoader.getCorsConfig();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (Utility.checkNull(corsConfig) || Utility.checkEmpty(corsConfig)) {
			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		String origin = httpRequest.getHeader("Origin");
		List<String> allowedOrigins = corsConfig.getAllowedOrigins();

		boolean originAllowed = origin != null && (allowedOrigins.contains("*") || allowedOrigins.contains(origin));

		if (originAllowed) {
			if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
				applyCorsHeaders(httpResponse, origin);
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			applyCorsHeaders(httpResponse, origin);
		}
		chain.doFilter(httpRequest, httpResponse);
	}

	public void applyCorsHeaders(HttpServletResponse httpResponse, String origin) {
		httpResponse.setHeader("Access-Control-Allow-Origin", origin);
		httpResponse.setHeader("Access-Control-Allow-Methods", String.join(",", corsConfig.getAllowedMethods()));
		httpResponse.setHeader("Access-Control-Allow-Headers", String.join(",", corsConfig.getAllowedHeaders()));
		httpResponse.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsConfig.isAllowCredentials()));
	}
}