package org.unicen.ameter.context;

import java.util.List;

public class BeanDeclaration {

    private final String value;

    private final String type;

    private final String ref;

    private final Bean bean;

    private final List<BeanDeclaration> list;

    protected BeanDeclaration() {

        this.value = null;
        this.type = null;
        this.ref = null;
        this.bean = null;
        this.list = null;
    }

    public String getValue() {
        return value;
    }

    public String getRef() {
        return ref;
    }

    public Bean getBean() {
        return bean;
    }

    public List<BeanDeclaration> getList() {
        return list;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "BeanDeclaration [value=" + value + ", type=" + type + ", ref=" + ref + ", bean=" + bean + ", list="
                + list + "]";
    }
}
