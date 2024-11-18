package at.schrer.inject.blueprints;

public record BeanDescriptor<T>(String beanAlias, Class<T> beanClass) {

    public boolean descriptorHoldsSuperClassOf(Class<?> clazz) {
        return clazz.isAssignableFrom(this.beanClass);
    }

    public boolean descriptorHoldsSubClassOf(Class<?> clazz) {
        return this.beanClass.isAssignableFrom(clazz);
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
}
