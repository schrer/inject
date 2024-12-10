package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.exceptions.internal.ConstructionInvocationException;
import at.schrer.inject.structures.Pair;

import java.util.List;

public interface BeanConstructor<V> {
    List<BeanDescriptor<Object>> getBeanDependencies();
    boolean isDependencyLess();
    boolean matchesParameters(List<Pair<BeanDescriptor<Object>, Object>> parameters);
    V getInstance(List<Pair<BeanDescriptor<Object>, Object>> parameters) throws ConstructionInvocationException;
}
