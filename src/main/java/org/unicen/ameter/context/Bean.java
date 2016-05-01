package org.unicen.ameter.context;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Bean {

	private final String id;
	
	@SerializedName("class")
	private final String classType;
	
	private final List<BeanProperty> properties;
	
	private final List<BeanConstructorArg> contructorArgs;
	
	private final FactoryBean factoryBean;

	private Bean() {
		
		this.id = null;
		this.classType = null;
		this.properties = null;
		this.contructorArgs = null;
		this.factoryBean = null;
	}

	public String getId() {
		return id;
	}

	public String getClassType() {
		return classType;
	}

	public List<BeanProperty> getProperties() {
		return properties;
	}

	public List<BeanConstructorArg> getContructorArgs() {
		return contructorArgs;
	}

	public FactoryBean getFactoryBean() {
		return factoryBean;
	}

	@Override
	public String toString() {
		return "Bean [id=" + id + ", classType=" + classType + ", properties=" + properties + ", contructorArgs="
				+ contructorArgs + ", factoryBean=" + factoryBean + "]";
	}
}
