package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;

import java.util.List;

public class ValueConstructor<V> implements BeanConstructor<V> {

    private final V value;

    public ValueConstructor(V value){
        this.value = value;
    }

    @Override
    public List<Class<?>> getDependencies() {return List.of();}

    @Override
    public List<BeanDescriptor<Object>> getBeanDependencies() {return List.of();}

    @Override
    public boolean isDependencyLess() {return true;}

    @Override
    public boolean matchesParameters(Object... parameters) {return true;}

    @Override
    public V getInstance(Object... parameters) {
        return this.value;
    }
}
