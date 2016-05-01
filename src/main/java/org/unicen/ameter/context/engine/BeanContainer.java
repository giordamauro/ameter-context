package org.unicen.ameter.context.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.unicen.ameter.context.Bean;
import org.unicen.ameter.context.BeanConstructorArg;
import org.unicen.ameter.context.FactoryBean;

public class BeanContainer {

	private final Map<String, Object> beanInstances = new HashMap<>();
	
	public BeanContainer(List<Bean> beans, Map<String, String> placeholderProperties) {
	
		Objects.requireNonNull(beans, "Beans cannot be null");
		
		Map<String, Bean> beansById = getBeansById(beans);
		for(Entry<String, Bean> beanById : beansById.entrySet()) {
		
			Object beanInstance = createBeanInstance(beanById.getValue(), placeholderProperties);
			beanInstances.put(beanById.getKey(), beanInstance);
		}
	}
	
	public Object createBeanInstance(Bean bean, Map<String, String> placeholderProperties) {

		FactoryBean factoryBean = bean.getFactoryBean();
		List<BeanConstructorArg> constructorArgs = bean.getContructorArgs();
		
		if(factoryBean != null && constructorArgs != null) {
			throw new IllegalStateException("Cannot instantiate bean having factoryBean and constructorArgs, only one must be present - " + bean);
		}
		
		Class<?> beanClass = getClassForName(bean.getClassType());
		Object[] args = getConstructorArgs(constructorArgs);
		
		Object beanInstance = createInstanceByConstructorArgs(beanClass, args);
		
		return beanInstance;
	}

	public Object[] getConstructorArgs(List<BeanConstructorArg> contructorArgs) {
		
		Object[] args = new Object[contructorArgs.size()];
		
		int i = 0;
		for(BeanConstructorArg contructorArg : contructorArgs) {
			
			String ref = contructorArg.getRef();
			if(contructorArg.getRef() != null) {
				Object refInstance = beanInstances.get(ref);
				
				if(refInstance == null){
					throw new IllegalStateException("Cannot find reference: " + ref);
				}
				args[i] = refInstance;
			}
			else {
				String value = contructorArg.getValue();
				if(value == null) {
					throw new IllegalStateException("Ref or value must be set");
				}				
				String type = contructorArg.getType();
				
				args[i] = getContstructorArgValue(value, type);
			}
			i++;
		}
		return args;
	}
	
	public Object getContstructorArgValue(String value, String type) {
		
		Object argValue = value;
		if(type != null) {
		
			Class<?> valueType = getClassForName(type);
			if(valueType.isAssignableFrom(Integer.class)){
				argValue = Integer.valueOf(value);
			}
			else if(valueType.isAssignableFrom(int.class)){
				argValue = Integer.valueOf(value).intValue();
			}
		}

		return argValue;
	}
	
	public Object createInstanceByConstructorArgs(Class<?> beanClass, Object[] contructorArgs) {
		
		Class<?>[] parameterTypes = new Class<?>[contructorArgs.length];
		int i = 0;
		for(Object arg : contructorArgs) {
			parameterTypes[i] = arg.getClass();
			i++;
		}
		
		try {
			Constructor<?> constructor = beanClass.getConstructor(parameterTypes);
			Object newInstance = constructor.newInstance(contructorArgs);
			
			return newInstance;
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public Map<String, Bean> getBeansById(List<Bean> beans) {

		Map<String, Bean> beansById = new HashMap<>();
		
		for(Bean bean : beans) {
					
			String beanId = bean.getId();
			if(beanId != null) {
				
				if(beansById.containsKey(beanId)){
					throw new IllegalStateException(String.format("Bean id %s is not unique - %s", beanId, bean));
				}
				beansById.put(beanId, bean);
			}
			else {				
				String classType = bean.getClassType();
				Objects.requireNonNull(classType, "Bean class cannot be null");
				
				if(beansById.containsKey(classType)){
					throw new IllegalStateException(String.format("Bean type %s is not unique - %s" , classType, bean));
				}
				beansById.put(classType, bean);
			}
		}
	
		return beansById;
	}
	
	public Class<?> getClassForName(String classType) {

		try {
			return Class.forName(classType);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
}
