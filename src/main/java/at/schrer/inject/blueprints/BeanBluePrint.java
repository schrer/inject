package at.schrer.inject.blueprints;

import at.schrer.inject.constructors.BeanConstructor;
import at.schrer.inject.exceptions.ComponentInstantiationException;
import at.schrer.inject.structures.Tuple;

import java.util.List;
import java.util.Optional;

public interface BeanBluePrint<T> {
    /**
     * Return the alias of the bean, if one is available.
     * @return the alias of the bean, or an empty Optional
     */
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

    /**
     * Provides the bean descriptor for the contained bean, which includes the bean alias and the bean class.
     * @return the bean descriptor of the contained bean
     */
    BeanDescriptor<T> getBeanDescriptor();

    /**
     * Provides the class of the contained bean
     * @return the class of the contained bean.
     */
    Class<T> getBeanClass();

    /**
     * Returns the available constructors for the bean. These can be one or more and can be dependency-less or not
     * @return the constructors available for the bean
     */
    List<? extends BeanConstructor<T>> getConstructors();

    /**
     * Checks if the bean of this blueprint satisfies a given bean description.
     * For this the bean name needs to match (if the given descriptor has one)
     * and the class contained in the blueprints is the same or subclass of the provided bean descriptors class.
     * @param lookingFor the bean descriptor to match
     * @return true if the descriptor matches the contained bean, false otherwise
     */
    boolean satisfiesDescriptor(BeanDescriptor<?> lookingFor);

    /**
     * Creates an instance of the bean with a no-args constructor
     * @return a bean instance
     * @throws ComponentInstantiationException if there is no no-args constructor available or an error happens during instantiation
     */
    T getNoArgsInstance()
            throws ComponentInstantiationException;

    T getInstance(List<Tuple<BeanDescriptor<Object>, Object>> parameters)
            throws ComponentInstantiationException;
}
