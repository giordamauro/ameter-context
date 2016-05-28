package org.unicen.ameter.context;

public class BeanExample {

    private final String value;

    public BeanExample(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BeanExample [value=" + value + "]";
    }
}