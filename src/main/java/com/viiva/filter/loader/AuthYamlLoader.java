package com.viiva.filter.loader;

import com.viiva.filter.auth.AuthRegistry;
import com.viiva.filter.auth.AuthRule;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AuthYamlLoader {

	public static AuthRegistry loadAuthRegistry(String fileName) {
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

			LoaderOptions options = new LoaderOptions();
			Constructor constructor = new Constructor(AuthRegistry.class, options);

			Yaml yaml = new Yaml(constructor);
			AuthRegistry registry = yaml.loadAs(inputStream, AuthRegistry.class);

			for (AuthRule rule : registry.getAuth()) {
				List<Integer> rawRoles = (List<Integer>) (List<?>) rule.getRoles();
				List<Integer> mappedRoles = new ArrayList<>();

				for (Integer code : rawRoles) {
					mappedRoles.add(code.intValue());
				}
				rule.setRoles(mappedRoles);
			}

			return registry;

		} catch (Exception e) {
			throw new RuntimeException("Failed to load auth YAML: " + fileName, e);
		}
	}
}
