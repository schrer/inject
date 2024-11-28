package at.schrer.inject.utils;

import at.schrer.inject.blueprints.BeanDescriptor;

import java.lang.reflect.Parameter;

public final class BeanUtils {
    private BeanUtils(){}

    public static BeanDescriptor<Object> parameterToBeanDescriptor(Parameter parameter){
        return new BeanDescriptor<>(
                ReflectionUtils.getNameForParameter(parameter),
                (Class<Object>) parameter.getType()
        );
    }
}
