package org.unicen.ameter.context;

public class BeanConstructorArg {

	private final String value;

	private final String type;
	
	private final String ref;

    private final Bean bean;

	private BeanConstructorArg() {

		this.value = null;
		this.type = null;
		this.ref = null;
        this.bean = null;
	}

	public String getValue() {
		return value;
	}

	public String getRef() {
		return ref;
	}

	public String getType() {
		return type;
	}

    public Bean getBean() {
        return bean;
    }

	@Override
	public String toString() {
        return "BeanConstructorArg [value=" + value + ", type=" + type + ", ref=" + ref + ", bean=" + bean + "]";
	}
}
