package com.viiva.filter.loader;

import com.viiva.filter.cors.CorsConfig;

public class CorsConfigLoader {

	private static final String CORS_CONFIG_PATH = "config/cors.yaml";
	private static CorsConfig corsConfig;

    static {
        corsConfig = YamlLoader.loadYaml(CORS_CONFIG_PATH, CorsConfig.class);
    }

    public static CorsConfig getCorsConfig() {
        return corsConfig;
    }

}
