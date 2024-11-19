package at.schrer.inject.utils;

import at.schrer.inject.annotations.ByName;

import java.lang.reflect.Parameter;

public final class ReflectionUtils {
    private ReflectionUtils(){}

    public static String getNameForParameter(Parameter parameter){
        ByName annotation = parameter.getAnnotation(ByName.class);
        if (annotation == null) {
            return null;
        }
        return annotation.value();
    }
}
