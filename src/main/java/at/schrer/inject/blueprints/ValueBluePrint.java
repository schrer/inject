package at.schrer.inject.blueprints;

import java.util.Optional;

public class ValueBluePrint<T> implements BeanBluePrint<T> {

    private final T value;
    private final BeanDescriptor<T> beanDescriptor;

    public ValueBluePrint(String alias, T value) {
        this.value = value;
        this.beanDescriptor = new BeanDescriptor<>(alias, (Class<T>) value.getClass());
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
    public T getNoArgsInstance() {
        return value;
    }

    @Override
    public T getInstance(Object... parameters) {
        return value;
    }
}
