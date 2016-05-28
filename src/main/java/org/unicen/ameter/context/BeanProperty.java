package org.unicen.ameter.context;

public class BeanProperty extends BeanDeclaration {

	private final String name;
	
	private BeanProperty() {
		
		this.name = null;
	}

	public String getName() {
		return name;
	}

    @Override
    public String toString() {
        return "BeanProperty [name=" + name + ", getValue()=" + getValue() + ", getRef()=" + getRef() + ", getBean()="
                + getBean() + ", getList()=" + getList() + "]";
    }
}
