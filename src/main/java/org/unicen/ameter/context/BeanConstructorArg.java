package org.unicen.ameter.context;

public class BeanConstructorArg {

	private final String value;

	private final String type;
	
	private final String ref;

	private BeanConstructorArg() {

		this.value = null;
		this.type = null;
		this.ref = null;
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

	@Override
	public String toString() {
		return "BeanConstructorArg [value=" + value + ", type=" + type + ", ref=" + ref + "]";
	}
}
