package org.unicen.ameter.context;

import org.unicen.ameter.context.engine.ContextContainer;

public class ContextEngineTest {
    
    public static void main(String[] args) {
     
        ContextContainer contextContainer = new ContextContainer("context.json");
        BeanExample bean = contextContainer.getBean(BeanExample.class);
        
        System.out.println(bean);
    }
    
}
