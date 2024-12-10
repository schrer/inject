package at.schrer.inject.utils;

import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.exceptions.internal.ConstructionInvocationException;
import at.schrer.inject.structures.Pair;

import java.lang.reflect.Parameter;
import java.util.List;
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

    /**
     * Sorts the provided instances to match the order of the provided parameters.
     * @param instances the instances that should be sorted
     * @param parameters the parameters that dictate the order
     * @return the sorted instances
     */
    public static Object[] sortMethodParameters(List<Pair<BeanDescriptor<Object>, Object>> instances, Parameter[] parameters) throws ConstructionInvocationException {
        if (parameters.length != instances.size()) {
            throw new ConstructionInvocationException("Wrong number of parameters given for this constructor.");
        }
        Object[] sortedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter target = parameters[i];
            for (Pair<BeanDescriptor<Object>, Object> instance : instances) {
                BeanDescriptor<Object> descriptor = instance.left();
                if (descriptor.isMatchingParameter(target)) {
                    sortedParameters[i] = instance.right();
                }
            }
            if (sortedParameters[i] == null) {
                throw new ConstructionInvocationException("Instance of class " + target + " is missing as provided parameter.");
            }
        }
        return sortedParameters;
    }
}
