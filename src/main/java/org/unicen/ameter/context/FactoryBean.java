package org.unicen.ameter.context;

public class FactoryBean {

	private final String ref;
	
	private final String method;

	private FactoryBean() {
		
		this.ref = null;
		this.method = null;
	}

	public String getRef() {
		return ref;
	}

	public String getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "FactoryBean [ref=" + ref + ", method=" + method + "]";
	}
}
