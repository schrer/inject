package at.schrer.inject.blueprints;

import at.schrer.inject.constructors.BeanConstructor;
import at.schrer.inject.exceptions.ComponentInstantiationException;

import java.util.List;
import java.util.Optional;

public interface BeanBluePrint<T> {
    Optional<String> getBeanAlias();

    /**
     * Checks if the bean can be instantiated without any arguments.
     *
     * @return true if a no-args constructor is available, false otherwise.
     */
    boolean canBeDependencyLess();

    /**
     * Checks if the blueprint describes a class that is the same or a subclass of the provided class parameter.
     *
     * @param clazz the class object to be checked
     * @return true if the blueprints class is the same or subclass of the provided class.
     */
    boolean isMatchingClass(Class<?> clazz);

    BeanDescriptor<T> getBeanDescriptor();
    Class<T> getComponentClass();
    List<? extends BeanConstructor<T>> getConstructors();

    boolean satisfiesDescriptor(BeanDescriptor<?> lookingFor);

    T getNoArgsInstance()
            throws ComponentInstantiationException;

    T getInstance(Object... parameters)
            throws ComponentInstantiationException;
}
