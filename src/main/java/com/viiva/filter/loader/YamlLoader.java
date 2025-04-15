package com.viiva.filter.loader;

import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class YamlLoader {

	public static <T> T loadYaml(String fileName, Class<T> clazz) {
		Yaml yaml = new Yaml();
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)){
			return yaml.loadAs(inputStream, clazz);
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to load YAML file: "+fileName, e);
		}
	}

}
