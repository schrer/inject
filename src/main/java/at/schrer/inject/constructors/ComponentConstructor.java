package at.schrer.inject.constructors;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.exceptions.internal.ConstructionInvocationException;
import at.schrer.inject.structures.Pair;
import at.schrer.inject.utils.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentConstructor<V> implements BeanConstructor<V>{
    private final Constructor<V> constructor;
    private final List<Class<?>> dependencies;
    private final List<BeanDescriptor<Object>> beanDependencies;
    private final boolean dependencyLess;

    public ComponentConstructor(Constructor<V> constructor) {
        this.constructor = constructor;
        this.dependencies = List.of(constructor.getParameterTypes());
        this.dependencyLess = constructor.getParameterCount() == 0;
        this.beanDependencies = Arrays.stream(constructor.getParameters())
                .map(BeanUtils::parameterToBeanDescriptor)
                .toList();
    }

    @Override
    public List<BeanDescriptor<Object>> getBeanDependencies() {
        return beanDependencies;
    }

    @Override
    public boolean isDependencyLess() {
        return dependencyLess;
    }

    @Override
    public boolean matchesParameters(List<Pair<BeanDescriptor<Object>, Object>> parameters) {
        List<Class<?>> providedParamClasses = parameters.stream()
                .map(it -> it.left().beanClass())
                .collect(Collectors.toList());
        // TODO what about names?
        return providedParamClasses.size() == dependencies.size()
                && containsAllMatchingClasses(providedParamClasses, dependencies)
                && containsAllMatchingInterfaces(dependencies, providedParamClasses);
    }

    private boolean containsAllMatchingClasses(Collection<Class<?>> providedClasses, Collection<Class<?>> lookingForInterfaces) {
        for (Class<?> iFace : lookingForInterfaces) {
            boolean iFaceImplementationFound = false;
            for (Class<?> clazz : providedClasses) {
                if (iFace.isAssignableFrom(clazz)) {
                    iFaceImplementationFound = true;
                    break;
                }
            }
            if (!iFaceImplementationFound) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAllMatchingInterfaces(Collection<Class<?>> providedInterfaces, Collection<Class<?>> lookingForClasses) {
        for (Class<?> clazz : lookingForClasses) {
            boolean topClassFound = false;
            for (Class<?> iFace : providedInterfaces) {
                if (iFace.isAssignableFrom(clazz)) {
                    topClassFound = true;
                    break;
                }
            }
            if (!topClassFound) {
                return false;
            }
        }
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
            instances = BeanUtils.sortMethodParameters(parameters, constructor.getParameters());
        }

        try {
            return constructor.newInstance(instances);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ConstructionInvocationException(e);
        }
    }
}
