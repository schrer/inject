package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;

import java.util.List;

public interface BeanConstructor<V> {
    List<Class<?>> getDependencies();
    List<BeanDescriptor<Object>> getBeanDependencies();
    boolean isDependencyLess();
    boolean matchesParameters(Object... parameters);
    V getInstance(Object... parameters);
}
