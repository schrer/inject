package at.schrer.inject.utils;

import at.schrer.inject.blueprints.BeanDescriptor;

import java.lang.reflect.Parameter;
import java.util.Set;

public final class BeanUtils {
    private static final Set<Class<?>> basicTypes = Set.of(
            String.class,
            Character.class,
            Byte.class,
            Short.class,
            Boolean.class,
            Long.class,
            Integer.class,
            Double.class,
            Float.class
    );
    private BeanUtils(){}

    public static BeanDescriptor<Object> parameterToBeanDescriptor(Parameter parameter){
        return new BeanDescriptor<>(
                ReflectionUtils.getNameForParameter(parameter),
                (Class<Object>) parameter.getType()
        );
    }

    /**
     * Returns true if the provided class needs a name to be identified.
     * This is the case for basic types, to make sure they are unique.
     * @param clazz the class to check
     * @return true if the class needs a name, false otherwise
     * @param <T> the type of the class
     */
    public static <T> boolean isMandatoryNameType(Class<T> clazz){
        return basicTypes.contains(clazz);
    }
}
