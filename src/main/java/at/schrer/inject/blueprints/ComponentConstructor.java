package at.schrer.inject.blueprints;

import at.schrer.inject.annotations.ByName;
import at.schrer.inject.exceptions.ComponentInstantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentConstructor<V> {
    private final Constructor<V> constructor;
    private final List<Class<?>> dependencies;
    private final List<BeanDescriptor<Object>> beanDependencies;
    private final boolean dependencyLess;

    public ComponentConstructor(Constructor<V> constructor) {
        this.constructor = constructor;
        this.dependencies = List.of(constructor.getParameterTypes());
        this.dependencyLess = constructor.getParameterCount() == 0;
        this.beanDependencies = Arrays.stream(constructor.getParameters())
                .map(this::parameterToBeanDescriptor)
                .toList();
    }

    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    public List<BeanDescriptor<Object>> getBeanDependencies() {
        return beanDependencies;
    }

    public boolean isDependencyLess() {
        return dependencyLess;
    }

    public boolean matchesParameters(Object... parameters) {
        Set<Class<?>> providedParamClasses = Arrays.stream(parameters)
                .map(Object::getClass)
                .collect(Collectors.toSet());
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

    public V getInstance(Object... parameters) {
        if (parameters.length > 1) {
            parameters = sortMethodParameters(parameters, constructor.getParameterTypes());
        }
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ComponentInstantiationException(e);
        }
    }

    private Object[] sortMethodParameters(Object[] parameters, Class<?>[] typesInOrder) {
        if (parameters.length != typesInOrder.length) {
            throw new ComponentInstantiationException("Wrong number of parameters given for this constructor.");
        }
        Object[] sortedParameters = new Object[typesInOrder.length];
        for (int i = 0; i < typesInOrder.length; i++) {
            Class<?> target = typesInOrder[i];
            for (Object param : parameters) {
                if (target.isAssignableFrom(param.getClass())) {
                    sortedParameters[i] = param;
                }
            }
            if (sortedParameters[i] == null) {
                throw new ComponentInstantiationException("Instance of class " + target + " is missing as provided parameter.");
            }
        }
        return sortedParameters;
    }

    private BeanDescriptor<Object> parameterToBeanDescriptor(Parameter parameter){
        ByName nameAnnotation = parameter.getAnnotation(ByName.class);
        String name = null;
        if (nameAnnotation != null) {
            name = nameAnnotation.value();
        }
        return new BeanDescriptor<>(name, (Class<Object>) parameter.getType());
    }
}
