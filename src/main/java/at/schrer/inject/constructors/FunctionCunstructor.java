package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.exceptions.ComponentInstantiationException;
import at.schrer.inject.structures.Tuple;
import at.schrer.inject.utils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class FunctionCunstructor<V> implements BeanConstructor<V>{
    private final Method sourceMethod;
    private final Class<V> beanClass;
    private final List<BeanDescriptor<Object>> beanDependencies;

    public FunctionCunstructor(Method sourceMethod, Class<V> beanClass) {
        this.sourceMethod = sourceMethod;
        this.beanClass = beanClass;
        this.beanDependencies = Arrays.stream(sourceMethod.getParameters())
                .map(BeanUtils::parameterToBeanDescriptor)
                .toList();
    }

    @Override
    public List<BeanDescriptor<Object>> getBeanDependencies() {
        // TODO add info for methods with dependencies
        return List.of();
    }

    @Override
    public boolean isDependencyLess() {
        // TODO add check for methods with dependencies
        return true;
    }

    @Override
    public boolean matchesParameters(List<Tuple<BeanDescriptor<Object>, Object>> parameters) {
        return true;
    }

    @Override
    public V getInstance(List<Tuple<BeanDescriptor<Object>, Object>> parameters) {
        try {
            return (V) this.sourceMethod.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ComponentInstantiationException("Failed to create instance of " + beanClass, e);
        }
    }
}
