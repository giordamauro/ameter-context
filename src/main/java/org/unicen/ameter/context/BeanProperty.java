package org.unicen.ameter.context;

public class BeanProperty {

	private final String name;
	
	private final String value;
	
	private final String ref;

	private BeanProperty() {
		
		this.name = null;
		this.value = null;
		this.ref = null;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getRef() {
		return ref;
	}

	@Override
	public String toString() {
		return "BeanProperty [name=" + name + ", value=" + value + ", ref=" + ref + "]";
	}
}
