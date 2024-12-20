package at.schrer.inject.blueprints;

import at.schrer.inject.constructors.BeanConstructor;
import at.schrer.inject.constructors.FunctionCunstructor;
import at.schrer.inject.exceptions.internal.ComponentInstantiationException;
import at.schrer.inject.exceptions.internal.ConstructionInvocationException;
import at.schrer.inject.structures.Pair;
import at.schrer.inject.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class FunctionBluePrint implements BeanBluePrint<Object> {
    private final BeanDescriptor<Object> beanDescriptor;
    private final FunctionCunstructor<Object> constructor;
    private final boolean dependencyLess;

    public FunctionBluePrint(String name, Method method) {
        this.beanDescriptor = new BeanDescriptor<>(name, (Class<Object>) method.getReturnType());
        this.dependencyLess = method.getParameterCount() == 0;
        this.constructor = new FunctionCunstructor<>(method, this.beanDescriptor.beanClass());
    }

    @Override
    public Optional<String> getBeanAlias() {
        return Optional.ofNullable(this.beanDescriptor.beanAlias());
    }

    @Override
    public boolean canBeDependencyLess() {
        return dependencyLess;
    }

    @Override
    public boolean isMatchingClass(Class<?> clazz) {
        return clazz.isAssignableFrom(this.beanDescriptor.beanClass());
    }

    @Override
    public BeanDescriptor<Object> getBeanDescriptor() {
        return this.beanDescriptor;
    }

    @Override
    public Class<Object> getBeanClass() {
        return this.beanDescriptor.beanClass();
    }

    @Override
    public List<? extends BeanConstructor<Object>> getConstructors() {
        return List.of(constructor);
    }

    @Override
    public boolean satisfiesDescriptor(BeanDescriptor<?> lookingFor) {
        boolean nameMatchesOrIsIgnored = StringUtils.isBlank(lookingFor.beanAlias())
                || this.beanDescriptor.isMatchingName(lookingFor.beanAlias());
        return nameMatchesOrIsIgnored && this.isMatchingClass(lookingFor.beanClass());
    }

    @Override
    public Object getNoArgsInstance() throws ComponentInstantiationException {
        try {
            return this.constructor.getInstance(List.of());
        } catch (ConstructionInvocationException e) {
            throw new ComponentInstantiationException(e, beanDescriptor);
        }
    }

    @Override
    public Object getInstance(List<Pair<BeanDescriptor<Object>, Object>> parameters) throws ComponentInstantiationException {
        try {
            return this.constructor.getInstance(parameters);
        } catch (ConstructionInvocationException e) {
            throw new ComponentInstantiationException(e, beanDescriptor);
        }
    }
}
