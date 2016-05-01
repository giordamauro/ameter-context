package org.unicen.ameter.context;

import java.util.List;

public class Context {

	private final PropertyPlaceholder placeholders;

	private final List<Bean> beans;

	private Context() {
		
		this.placeholders = null;
		this.beans = null;
	}
	
	public PropertyPlaceholder getPlaceholders() {
		return placeholders;
	}

	public List<Bean> getBeans() {
		return beans;
	}
}
