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
import com.viiva.util.BasicUtil;

public class CorsFilter implements Filter {

	private CorsConfig corsConfig;

	@Override
	public void init(FilterConfig filterConfig) {
		this.corsConfig = CorsConfigLoader.getCorsConfig();
		System.out.println("Cors Filter Initialised.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (BasicUtil.isBlank(corsConfig)) {
			chain.doFilter(httpRequest, httpResponse);
			return;
		}

		System.out.println("\nRequest URI: " + httpRequest.getRequestURI());
		System.out.println("Request method: " + httpRequest.getMethod());
		System.out.println("Content-Type: " + httpRequest.getContentType());
		System.out.println("Headers: " + httpRequest.getHeader("Content-Type"));

		String origin = httpRequest.getHeader("Origin");

		if (BasicUtil.isNull(origin)) {
			origin = "*";
		}

		System.out.println("Origin: " + origin);

		List<String> allowedOrigins = corsConfig.getAllowedOrigins();

		boolean originAllowed = (allowedOrigins.contains("*") || allowedOrigins.contains(origin));

		if (originAllowed) {
			if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
				applyCorsHeaders(httpResponse, origin);
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			applyCorsHeaders(httpResponse, origin);

			System.out.println("CORS Filter cleared.\n");

			chain.doFilter(httpRequest, httpResponse);

		} else {
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cors filter rejection.");
		}

	}

	public void applyCorsHeaders(HttpServletResponse httpResponse, String origin) {
		httpResponse.setHeader("Access-Control-Allow-Origin", origin);
		httpResponse.setHeader("Access-Control-Allow-Methods", String.join(",", corsConfig.getAllowedMethods()));
		httpResponse.setHeader("Access-Control-Allow-Headers", String.join(",", corsConfig.getAllowedHeaders()));
		httpResponse.setHeader("Access-Control-Allow-Credentials", String.valueOf(corsConfig.isAllowCredentials()));
	}
}