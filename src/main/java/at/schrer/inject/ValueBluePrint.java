package at.schrer.inject;

import java.util.Optional;

public class ValueBluePrint<T> implements BeanBluePrint<T> {

    private final T value;
    private final Class<T> valueClass;
    private final String valueAlias;

    public ValueBluePrint(String alias, T value) {
        this.value = value;
        this.valueClass = (Class<T>) value.getClass();
        this.valueAlias = alias;
    }

    @Override
    public Optional<String> getBeanAlias() {
        return Optional.of(this.valueAlias);
    }

    @Override
    public boolean canBeDependencyLess() {
        return true;
    }

    @Override
    public boolean isMatchingClass(Class<?> clazz) {
        return clazz.isAssignableFrom(this.valueClass);
    }

    @Override
    public boolean isSameClass(Class<?> clazz) {
        return this.valueClass == clazz;
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
