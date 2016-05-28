package org.unicen.ameter.context;

public class FactoryBean {

	private final String ref;
	
	private final String method;

    private final Bean bean;

	private FactoryBean() {
		
		this.ref = null;
		this.method = null;
        this.bean = null;
	}

	public String getRef() {
		return ref;
	}

	public String getMethod() {
		return method;
	}

    public Bean getBean() {
        return bean;
	}

	@Override
	public String toString() {
        return "FactoryBean [ref=" + ref + ", method=" + method + ", bean=" + bean + "]";
	}
}
