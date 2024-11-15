package at.schrer.inject;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface BeanBluePrint<T> {
    Optional<String> getBeanAlias();

    /**
     * Checks if the bean can be instantiated without any arguments.
     *
     * @return true if a no-args constructor is available, false otherwise.
     */
    boolean canBeDependencyLess();

    boolean isMatchingClass(Class<?> clazz);

    boolean isSameClass(Class<?> clazz);

    T getNoArgsInstance()
            throws InvocationTargetException, InstantiationException, IllegalAccessException;

    T getInstance(Object... parameters)
            throws ContextException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
