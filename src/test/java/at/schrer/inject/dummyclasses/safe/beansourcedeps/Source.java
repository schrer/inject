package at.schrer.inject.dummyclasses.safe.beansourcedeps;

import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;

@BeanSource
public class Source {
    private Source(){}

    @Component
    public static OneClass oneClass(TwoClass twoClass){
        return new OneClass(twoClass);
    }
}
