package at.schrer.inject;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ComponentBluePrint<T> implements BeanBluePrint<T>{
    private final List<ComponentConstructor<T>> constructors;
    private final ComponentConstructor<T> noArgConstructor;
    private final Class<T> componentClass;
    private final String componentAlias;

    public ComponentBluePrint(Class<T> componentClass) {
        this.componentClass = componentClass;
        List<ComponentConstructor<T>> allConstructors = new ArrayList<>();
        for (Constructor<?> constructor : componentClass.getConstructors()) {
            allConstructors.add((ComponentConstructor<T>) new ComponentConstructor<>(constructor));
        }

        this.constructors = Collections.unmodifiableList(allConstructors);
        this.noArgConstructor = constructors.stream()
                .filter(ComponentConstructor::isDependencyLess)
                .findAny().orElse(null);
        String componentName = componentClass.getAnnotation(Component.class).name();
        if (!StringUtils.isBlank(componentName)) {
            this.componentAlias = componentName;
        } else {
            this.componentAlias = null;
        }
    }

    @Override
    public boolean canBeDependencyLess(){
        return noArgConstructor != null;
    }

    @Override
    public boolean isSameClass(Class<?> clazz){
        return this.componentClass == clazz;
    }

    public boolean isMatchingClass(Class<?> clazz){
        return clazz.isAssignableFrom(this.componentClass);
    }

    @Override
    public T getNoArgsInstance() throws ComponentInstantiationException {
        if (noArgConstructor == null) {
            throw new ComponentInstantiationException("No argument-less constructor available for this class.");
        }

        return noArgConstructor.getInstance();
    }

    @Override
    public T getInstance(Object... parameters) throws ComponentInstantiationException {
        Optional<ComponentConstructor<T>> constructor = constructors.stream()
                .filter(it -> it.matchesParameters(parameters))
                .findFirst();
        if (constructor.isEmpty()) {
            throw new ComponentInstantiationException("No matching constructor found for parameters");
        }
        return constructor.get().getInstance(parameters);
    }

    public List<ComponentConstructor<T>> getConstructors(){
        return constructors;
    }

    public Class<T> getComponentClass(){
        return this.componentClass;
    }

    @Override
    public Optional<String> getBeanAlias() {
        return Optional.ofNullable(this.componentAlias);
    }

    public static class ComponentConstructor<V> {
        private final Constructor<V> constructor;
        private final List<Class<?>> dependencies;
        private final boolean dependencyLess;

        public ComponentConstructor(Constructor<V> constructor) {
            this.constructor = constructor;
            this.dependencies = List.of(constructor.getParameterTypes());
            this.dependencyLess = constructor.getParameterCount() == 0;
        }

        public List<Class<?>> getDependencies() {
            return dependencies;
        }

        public boolean isDependencyLess() {
            return dependencyLess;
        }

        public boolean matchesParameters(Object... parameters){
            Set<Class<?>> providedParamClasses = Arrays.stream(parameters)
                    .map(Object::getClass)
                    .collect(Collectors.toSet());
            return providedParamClasses.size() == dependencies.size()
                    && containsAllMatchingClasses(providedParamClasses, dependencies)
                    && containsAllMatchingInterfaces(dependencies, providedParamClasses);
        }

        private boolean containsAllMatchingClasses(Collection<Class<?>> providedClasses, Collection<Class<?>> lookingForInterfaces){
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

        private boolean containsAllMatchingInterfaces(Collection<Class<?>> providedInterfaces, Collection<Class<?>> lookingForClasses){
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
            } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
                throw new ComponentInstantiationException(e);
            }
        }

        private Object[] sortMethodParameters(Object[] parameters, Class<?>[] typesInOrder){
            if (parameters.length != typesInOrder.length) {
                throw new ComponentInstantiationException("Wrong number of parameters given for this constructor.");
            }
            Object[] sortedParameters = new Object[typesInOrder.length];
            for(int i = 0; i < typesInOrder.length; i++) {
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
    }
}
