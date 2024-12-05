package at.schrer.inject.blueprints;

import at.schrer.inject.constructors.ComponentConstructor;
import at.schrer.inject.exceptions.ComponentInstantiationException;
import at.schrer.inject.annotations.Component;
import at.schrer.inject.structures.Pair;
import at.schrer.inject.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.util.*;

public class ComponentBluePrint<T> implements BeanBluePrint<T>{
    private final List<ComponentConstructor<T>> constructors;
    private final ComponentConstructor<T> noArgConstructor;
    private final BeanDescriptor<T> beanDescriptor;

    public ComponentBluePrint(Class<T> componentClass) {
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
            this.beanDescriptor = new BeanDescriptor<>(componentName, componentClass);
        } else {
            this.beanDescriptor = new BeanDescriptor<>(null, componentClass);
        }
    }

    @Override
    public boolean canBeDependencyLess(){
        return noArgConstructor != null;
    }

    @Override
    public boolean isMatchingClass(Class<?> clazz){
        return clazz.isAssignableFrom(this.beanDescriptor.beanClass());
    }

    @Override
    public T getNoArgsInstance() throws ComponentInstantiationException {
        if (noArgConstructor == null) {
            throw new ComponentInstantiationException("No argument-less constructor available for class " + this.getBeanClass().getName());
        }

        return noArgConstructor.getInstance(List.of());
    }

    @Override
    public T getInstance(List<Pair<BeanDescriptor<Object>, Object>> parameters) throws ComponentInstantiationException {
        Optional<ComponentConstructor<T>> constructor = constructors.stream()
                .filter(it -> it.matchesParameters(parameters))
                .findFirst();
        if (constructor.isEmpty()) {
            throw new ComponentInstantiationException("No matching constructor found for class " + this.getBeanClass().getName() + " and parameters provided parameters");
        }
        return constructor.get().getInstance(parameters);
    }

    @Override
    public List<ComponentConstructor<T>> getConstructors(){
        return constructors;
    }

    @Override
    public boolean satisfiesDescriptor(BeanDescriptor<?> lookingFor) {
        boolean nameMatchesOrIsIgnored = StringUtils.isBlank(lookingFor.beanAlias())
                || this.beanDescriptor.isMatchingName(lookingFor.beanAlias());
        return nameMatchesOrIsIgnored && this.isMatchingClass(lookingFor.beanClass());
    }

    @Override
    public Class<T> getBeanClass(){
        return this.beanDescriptor.beanClass();
    }

    @Override
    public Optional<String> getBeanAlias() {
        return Optional.ofNullable(this.beanDescriptor.beanAlias());
    }

    @Override
    public BeanDescriptor<T> getBeanDescriptor() {
        return this.beanDescriptor;
    }
}
