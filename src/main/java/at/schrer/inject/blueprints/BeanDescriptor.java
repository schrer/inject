package at.schrer.inject.blueprints;

import at.schrer.inject.utils.ReflectionUtils;
import at.schrer.inject.utils.StringUtils;

import java.lang.reflect.Parameter;

public record BeanDescriptor<T>(String beanAlias, Class<T> beanClass) {

    public boolean descriptorHoldsSuperClassOf(Class<?> clazz) {
        return this.beanClass.isAssignableFrom(clazz);
    }

    public boolean descriptorHoldsSubClassOf(Class<?> clazz) {
        return clazz.isAssignableFrom(this.beanClass);
    }

    /**
     * Checks if this bean descriptor has a non-empty bean name and equals the
     *
     * @param name the name to compare the bean to
     * @return true if the descriptor has a name and it matches the provided name, false otherwise
     */
    public boolean isMatchingName(String name) {
        return beanAlias != null && !beanAlias.isBlank() && beanAlias.equals(name);
    }

    public boolean isMatchingParameter(Parameter parameter){
        Class<?> neededClass = parameter.getType();
        String neededName = ReflectionUtils.getNameForParameter(parameter);
        boolean ignoreNameMatching = StringUtils.isBlank(neededName);

        return descriptorHoldsSubClassOf(neededClass) && (ignoreNameMatching || neededName.equals(this.beanAlias()));
    }

    @Override
    public String toString(){
        String nameRepresentation = StringUtils.isBlank(beanAlias) ? "" : beanAlias + " ";
        String classNameRepresentation = this.beanClass.getName();
        return nameRepresentation + classNameRepresentation;
    }
}
