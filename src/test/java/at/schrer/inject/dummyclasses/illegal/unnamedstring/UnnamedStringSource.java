package at.schrer.inject.dummyclasses.illegal.unnamedstring;

import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;

@BeanSource
public class UnnamedStringSource {
    @Component
    public static String getUnnamedString(){
        return "UnnamedString";
    }
}
