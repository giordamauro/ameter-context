package org.unicen.ameter.context;

import java.util.List;
import java.util.Map;

public class PropertyPlaceholder {

	private final List<String> files;
	
	private final Map<String, String> properties;

	private PropertyPlaceholder() {
		
		this.files = null;
		this.properties = null;
	}
	
	public List<String> getFiles() {
		return files;
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
