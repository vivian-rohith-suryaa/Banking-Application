package com.viiva.filter.route;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.viiva.filter.loader.RouteRegistryLoader;
import com.viiva.util.BasicUtil;

public class RouteFilter implements Filter {

	private RouteRegistry routeRegistry;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.routeRegistry = RouteRegistryLoader.getRouteRegistry();
		System.out.println("Route Filter Initialised.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (BasicUtil.isBlank(routeRegistry)) {
			// System.out.println("Null here");
			chain.doFilter(httpRequest, response);
			return;
		}

		String path = httpRequest.getRequestURI();
		String method = httpRequest.getMethod();

		Optional<RouteConfig> matchedRoute = routeRegistry.getRoutes().stream()
				.filter(route -> pathMatches(route.getPath(), path) && route.getMethod().equalsIgnoreCase(method))
				.findFirst();

		if (matchedRoute.isPresent()) {
			System.out.println(matchedRoute.get().getPath() + " " + matchedRoute.get().getMethod() + " "
					+ matchedRoute.get().getHandler());
			request.setAttribute("handler", matchedRoute.get().getHandler());
			System.out.println("Route Filter Cleared.");
			chain.doFilter(httpRequest, response);
		} else {
			System.out.println("Filter Request: " + httpRequest);
			System.out.println("Filter Response: " + response);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND,
					"No handler found for this route.");
		}
	}

	private boolean pathMatches(String configPath, String requestPath) {
		String regex = configPath.replaceAll(":\\w+", "[^/]+") + "$";
		return requestPath.matches(regex);
	}

}
