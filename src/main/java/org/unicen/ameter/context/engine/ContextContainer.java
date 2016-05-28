package org.unicen.ameter.context.engine;

import java.io.FileReader;
import java.util.Map;
import java.util.Objects;

import org.unicen.ameter.context.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class ContextContainer {

	private final Gson gson;
	private final Map<String, String> placeholderProperties;
	private final BeanContainer beanContainer;
	
	public ContextContainer(String contextFile) {

		Objects.requireNonNull(contextFile, "ContextFile cannot be null");

		this.gson = new GsonBuilder().create();
		Context context = readJsonContext(contextFile);

		PlaceholderContainer placeholderContainer = new PlaceholderContainer(context.getPlaceholders());
		this.placeholderProperties = placeholderContainer.getPlaceholderProperties();
		
		this.beanContainer = new BeanContainer(context.getBeans(), placeholderProperties);
	}

	private Context readJsonContext(String contextFile) {

		try {
			JsonReader reader = new JsonReader(new FileReader(contextFile));
			return gson.fromJson(reader, Context.class);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot read context", e);
		}
	}

	public String getProperty(String name) {
		
		String value = placeholderProperties.get(name);
		if(value == null) {
			throw new IllegalStateException("Property not found: " + name);
		}
		return value;
	}
	
	public <T> T getBean(Class<T> beanClass) {	    
	    return beanContainer.getBeanByClass(beanClass);
	}

	public <T> T getBean(Class<T> beanClass, String beanId) {
		return null;
	}
}
