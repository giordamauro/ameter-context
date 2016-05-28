package org.unicen.ameter.context.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.unicen.ameter.context.Bean;
import org.unicen.ameter.context.BeanConstructorArg;
import org.unicen.ameter.context.FactoryBean;

public class BeanContainer {

    private final Map<String, String> placeholderProperties;
    
	private final Map<String, Object> instancesById = new HashMap<>();
	private final Map<Class<?>, Object> instancesByClass = new HashMap<>();
	
	public BeanContainer(List<Bean> beans, Map<String, String> placeholderProperties) {
	
		Objects.requireNonNull(beans, "Beans cannot be null");
        Objects.requireNonNull(placeholderProperties, "PlaceholderProperties cannot be null");
		
		this.placeholderProperties = placeholderProperties;
		
		for(Bean bean : beans) {
			createBeanInstance(bean);
		}
	}
	
	public Object createBeanInstance(Bean bean) {

		FactoryBean factoryBean = bean.getFactoryBean();
		List<BeanConstructorArg> constructorArgs = bean.getConstructorArgs();
		
		if(factoryBean != null && constructorArgs != null) {
			throw new IllegalStateException("Cannot instantiate bean having factoryBean and constructorArgs, only one must be present - " + bean);
		}
		
		Class<?> beanClass = getClassForName(bean.getClassType());
		Object beanInstance = null;
		
		if(constructorArgs != null) {
    	
		    Object[] args = getConstructorArgs(constructorArgs);
    		beanInstance = createInstanceByConstructorArgs(beanClass, args);
		}
		else if(factoryBean != null) {
		    
		    //TODO: create by FactoryBean
		}
		else {
		    beanInstance = createInstanceByDefaultConstructor(beanClass);
		}
		
        if (bean.getId() != null) {

            if (instancesById.containsKey(bean.getId())) {
                throw new IllegalStateException(String.format("Bean id %s is not unique - %s", bean.getId(), bean));
            }
            instancesById.put(bean.getId(), beanInstance);
        }
        
//      TODO: Manage more than 1 class instance
		
		instancesByClass.put(beanInstance.getClass(), beanInstance);
		
		return beanInstance;
	}

	public Object[] getConstructorArgs(List<BeanConstructorArg> contructorArgs) {
		
		Object[] args = new Object[contructorArgs.size()];
		
		int i = 0;
		for(BeanConstructorArg contructorArg : contructorArgs) {
			
			if(contructorArg.getRef() != null) {
				
			    String ref = contructorArg.getRef();
			    Object refInstance = getBeanById(ref);
				
			    args[i] = refInstance;
			}
			else if(contructorArg.getValue() != null) {
			    
				String value = contructorArg.getValue();
				String type = contructorArg.getType();
				
				args[i] = getContstructorArgValue(value, type);
			}
			else if(contructorArg.getBean() != null) {
			    
			    Bean bean = contructorArg.getBean();
			    args[i] = createBeanInstance(bean);
			}
			else {
			    throw new IllegalStateException("Ref, value or bean must be set");
			}
			i++;
		}
		return args;
	}
	
	public Object getContstructorArgValue(String value, String type) {
		
//	    TODO: Manage primitive types
	    
		Object argValue = value;
		Class<?> valueType = String.class;
		        
		if(type != null) {
		
			valueType = getClassForName(type);
			if(valueType.isAssignableFrom(Integer.class)){
				argValue = Integer.valueOf(value);
			}
			else if(valueType.isAssignableFrom(int.class)){
				argValue = Integer.valueOf(value).intValue();
			}
		}

		if(valueType.isAssignableFrom(String.class) && value.startsWith("${") && value.endsWith("}")) {
		    String propertyName = value.substring(2, value.length() - 1);
		    String propertyValue = placeholderProperties.get(propertyName);
		    
		    if(propertyValue == null) {
		        throw new IllegalStateException("PropertyName not found - " + propertyName);
		    }
		    
		    argValue = propertyValue;
		}
		
		return argValue;
	}
	
   public Object createInstanceByDefaultConstructor(Class<?> beanClass) {

       try {
           Object newInstance = beanClass.newInstance();
           
           return newInstance;
           
       } catch (Exception e) {
           throw new IllegalStateException(e);
       }
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
	
	public Class<?> getClassForName(String classType) {

	    Objects.requireNonNull(classType, "ClassType cannot be null");
	    
		try {
			return Class.forName(classType);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Object getBeanById(String ref) {
	    
	    Objects.requireNonNull(ref, "BeanId cannot be null");
	    Object refInstance = instancesById.get(ref);
        
        if(refInstance == null){
            throw new IllegalStateException("Cannot find reference: " + ref);
        }
    
        return refInstance;
	}
	
	public <T> T getBeanByClass(Class<T> beanType) {
	    
	    Objects.requireNonNull(beanType, "BeanType cannot be null");
	    Object beanInstance = instancesByClass.get(beanType);
        
        if(beanInstance == null){
            throw new IllegalStateException("Cannot find beanType: " + beanType);
        }
    
        @SuppressWarnings("unchecked")
        T castedInstance = (T) beanInstance;
        
        return castedInstance;
	}

}
