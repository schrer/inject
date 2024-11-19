package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.structures.Tuple;

import java.util.List;

public class ValueConstructor<V> implements BeanConstructor<V> {

    private final V value;

    public ValueConstructor(V value){
        this.value = value;
    }

    @Override
    public List<BeanDescriptor<Object>> getBeanDependencies() {return List.of();}

    @Override
    public boolean isDependencyLess() {return true;}

    @Override
    public boolean matchesParameters(List<Tuple<BeanDescriptor<Object>, Object>> parameters) {
        return true;
    }

    @Override
    public V getInstance(List<Tuple<BeanDescriptor<Object>, Object>> parameters) {
        return this.value;
    }
}
