package at.schrer.inject.blueprints;

import at.schrer.inject.constructors.ValueConstructor;

import java.util.List;
import java.util.Optional;

public class ValueBluePrint implements BeanBluePrint<String> {

    private final String value;
    private final BeanDescriptor<String> beanDescriptor;
    private final ValueConstructor<String> constructor;

    public ValueBluePrint(String alias, String value) {
        this.value = value;
        this.beanDescriptor = new BeanDescriptor<>(alias, String.class);
        this.constructor = new ValueConstructor<>(value);
    }

    @Override
    public Optional<String> getBeanAlias() {
        return Optional.of(this.beanDescriptor.beanAlias());
    }

    @Override
    public boolean canBeDependencyLess() {
        return true;
    }

    @Override
    public boolean isMatchingClass(Class<?> clazz) {
        return clazz.isAssignableFrom(this.beanDescriptor.beanClass());
    }

    @Override
    public String getNoArgsInstance() {
        return value;
    }

    @Override
    public String getInstance(Object... parameters) {
        return value;
    }

    @Override
    public BeanDescriptor<String> getBeanDescriptor() {
        return this.beanDescriptor;
    }

    @Override
    public Class<String> getComponentClass() {
        return beanDescriptor.beanClass();
    }

    @Override
    public List<ValueConstructor<String>> getConstructors() {
        return List.of(constructor);
    }

    @Override
    public boolean satisfiesDescriptor(BeanDescriptor<?> lookingFor) {
        // Values are identified mostly by name, so name match is required everytime
        return this.beanDescriptor.isMatchingName(lookingFor.beanAlias())
                && this.isMatchingClass(lookingFor.beanClass());
    }
}
