package com.viiva.filter.loader;

import com.viiva.filter.route.RouteRegistry;

public class RouteRegistryLoader {

	private static final String ROUTE_CONFIG_PATH = "config/routes.yaml";
	private static RouteRegistry routeRegistry;

	static {
		routeRegistry = YamlLoader.loadYaml(ROUTE_CONFIG_PATH, RouteRegistry.class);
	}

	public static RouteRegistry getRouteRegistry() {
		return routeRegistry;
	}

}
