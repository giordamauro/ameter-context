package org.unicen.ameter.context.engine;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.unicen.ameter.context.PropertyPlaceholder;

public class PlaceholderContainer {

	private final Map<String, String> properties;

	public PlaceholderContainer(PropertyPlaceholder propertyPlaceholder) {

		Objects.requireNonNull(propertyPlaceholder, "PropertyPlaceholder cannot be null");
		
		this.properties = new HashMap<>();

		List<String> files = propertyPlaceholder.getFiles();
		Map<String, String> fileProperties = getFileProperties(files);
		
		properties.putAll(propertyPlaceholder.getProperties());
		properties.putAll(fileProperties);
	}

	public Map<String, String> getFileProperties(List<String> files) {

		Map<String, String> properties = new HashMap<>();
		
		for (String file : files) {
			
			Map<String, String> fileProperties = getFileProperties(file);
			properties.putAll(fileProperties);
		}
		
		return properties;
	}

	public Map<String, String> getFileProperties(String file) {

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(file));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return new HashMap<String, String>(properties);
	}

	public Map<String, String> getPlaceholderProperties() {
		return Collections.unmodifiableMap(properties);
	}
}
