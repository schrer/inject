package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.structures.Tuple;

import java.util.List;

public interface BeanConstructor<V> {
    List<BeanDescriptor<Object>> getBeanDependencies();
    boolean isDependencyLess();
    boolean matchesParameters(List<Tuple<BeanDescriptor<Object>, Object>> parameters);
    V getInstance(List<Tuple<BeanDescriptor<Object>, Object>> parameters);
}
