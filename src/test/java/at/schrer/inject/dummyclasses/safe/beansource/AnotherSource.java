package at.schrer.inject.dummyclasses.safe.beansource;

import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;

import static at.schrer.inject.TestConstants.ComponentNames.AC_INST4;

@BeanSource
public class AnotherSource {
    @Component(name = AC_INST4)
    public static AnotherClass anotherClassInstance(){
        return new AnotherClass(AC_INST4);
    }

    @Component
    public AnotherClass notStaticNotPickedUp(){
        return new AnotherClass("bla");
    }
}
