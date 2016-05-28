package org.unicen.ameter.context.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.unicen.ameter.context.Bean;
import org.unicen.ameter.context.BeanDeclaration;
import org.unicen.ameter.context.BeanProperty;

public class BeanContainer {

    private final Map<String, String> placeholderProperties;
    
	private final Map<String, Object> instancesById = new HashMap<>();
	private final Map<Class<?>, Object> instancesByClass = new HashMap<>();
	
	public BeanContainer(List<Bean> beans, Map<String, String> placeholderProperties) {
	
		Objects.requireNonNull(beans, "Beans cannot be null");
        Objects.requireNonNull(placeholderProperties, "PlaceholderProperties cannot be null");
		
		this.placeholderProperties = placeholderProperties;
		
		for(Bean bean : beans) {
			createBean(bean);
		}
	}
	
	public Object createBean(Bean bean) {

		Object beanInstance = createBeanInstance(bean);
		addBeanProperties(beanInstance, bean.getProperties());
		
		registerBeanInstance(beanInstance, bean.getId());
        
		return beanInstance;
	}
	
	public Object createBeanInstance(Bean bean) {

	    Objects.requireNonNull(bean, "Bean cannot be null");
	    
        if(bean.getFactoryBean() != null && bean.getConstructorArgs() != null) {
            throw new IllegalStateException("Cannot instantiate bean having factoryBean and constructorArgs, only one must be present - " + bean);
        }

        Object beanInstance = null;

        Class<?> beanClass = getClassForName(bean.getClassType());

        if(bean.getConstructorArgs() != null) {

            List<BeanDeclaration> constructorArgs = bean.getConstructorArgs();
            List<Object> list = getByDeclarationList(constructorArgs);
            Object[] args = list.toArray();
            
            beanInstance = createInstanceByConstructorArgs(beanClass, args);
        }
        else if(bean.getFactoryBean() != null) {
            
            //TODO: create by FactoryBean
            throw new UnsupportedOperationException("Not implemented yet.");
        }
        else {
            beanInstance = createInstanceByDefaultConstructor(beanClass);
        }
        
        return beanInstance;
	}
	
	public void addBeanProperties(Object beanInstance, List<BeanProperty> properties) {
	
	    Objects.requireNonNull(beanInstance, "BeanInstance cannot be null");
	    
	    if(properties != null) {
	        
	        for(BeanProperty property : properties) {
	            
	            if(property.getName() == null) {
	                throw new IllegalStateException("BeanProperty name cannot be null");
	            }
	            
	            Object propertyValue = getByDeclaration(property);
	            setFieldValue(beanInstance, property.getName(), propertyValue);
	        }
	    }
	}
	
	public void setFieldValue(Object target, String fieldName, Object value) {
	    
	    Objects.requireNonNull(target, "Target cannot be null");
        Objects.requireNonNull(fieldName, "FieldName cannot be null");
        
        Class<?> targetClass = target.getClass();
	    try {
            Field propertyField = targetClass.getDeclaredField(fieldName);
            
            boolean accessible = propertyField.isAccessible();
            propertyField.set(target, value);
            propertyField.setAccessible(accessible);
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
	}
	
	public void registerBeanInstance(Object beanInstance, String beanId) {
	    
	    Objects.requireNonNull(beanInstance, "BeanInstance cannot be null");
	    
	    if (beanId != null) {

            if (instancesById.containsKey(beanId)) {
                throw new IllegalStateException(String.format("Bean id %s is not unique", beanId));
            }
            instancesById.put(beanId, beanInstance);
        }        

	    // TODO: Manage more than 1 class instance
        instancesByClass.put(beanInstance.getClass(), beanInstance);
	}

	public List<Object> getByDeclarationList(List<BeanDeclaration> declarationList) {
		
	    List<Object> instanceList = new ArrayList<>();
	    
		for(BeanDeclaration contructorArg : declarationList) {
			
		    Object beanInstance = getByDeclaration(contructorArg);
		    instanceList.add(beanInstance);
		}
		
		return instanceList;
	}
	
	public Object getByDeclaration(BeanDeclaration declaration) {
	    
	    Object beanInstance = null;
	    
	    if(declaration.getRef() != null) {
            
            String ref = declaration.getRef();
            beanInstance = getBeanById(ref);
        }
        else if(declaration.getValue() != null) {
            
            String value = declaration.getValue();
            String type = declaration.getType();
            
            beanInstance = getValueType(value, type);
        }
        else if(declaration.getBean() != null) {
            
            Bean bean = declaration.getBean();
            beanInstance = createBean(bean);
        }
        else if(declaration.getList() != null) {
            
            beanInstance = getByDeclarationList(declaration.getList());
        }
        else {
            throw new IllegalStateException("Ref, Value, List or Bean must be set");
        }
	    
	    return beanInstance;
	}
	
	public Object getValueType(String value, String type) {
		
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
