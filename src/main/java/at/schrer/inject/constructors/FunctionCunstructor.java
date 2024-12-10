package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.exceptions.internal.ConstructionInvocationException;
import at.schrer.inject.structures.Pair;
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
        return beanDependencies;
    }

    @Override
    public boolean isDependencyLess() {
        return beanDependencies.isEmpty();
    }

    @Override
    public boolean matchesParameters(List<Pair<BeanDescriptor<Object>, Object>> parameters) {
        // TODO implement
        return true;
    }

    @Override
    public V getInstance(List<Pair<BeanDescriptor<Object>, Object>> parameters) throws ConstructionInvocationException {

        Object[] instances;
        if (parameters.isEmpty()){
            instances = new Object[0];
        } else if (parameters.size() == 1) {
            instances = new Object[1];
            instances[0] = parameters.getFirst().right();
        } else {
            instances = BeanUtils.sortMethodParameters(parameters, sourceMethod.getParameters());
        }

        try {
            return (V) sourceMethod.invoke(null, instances);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConstructionInvocationException(e);
        }
    }
}
