package at.schrer.inject.exceptions.internal;

import at.schrer.inject.blueprints.BeanDescriptor;

public class ComponentInstantiationException extends Exception {
    private final BeanDescriptor<?> beanDescriptor;

    public ComponentInstantiationException(String message, BeanDescriptor<?> beanDescriptor) {
        super(message);
        this.beanDescriptor = beanDescriptor;
    }

    public ComponentInstantiationException(Throwable cause, BeanDescriptor<?> beanDescriptor) {
        super(cause);
        this.beanDescriptor = beanDescriptor;
    }

    public BeanDescriptor<?> getBeanDescriptor() {
        return beanDescriptor;
    }
}
