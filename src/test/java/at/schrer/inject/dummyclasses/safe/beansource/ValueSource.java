package at.schrer.inject.dummyclasses.safe.beansource;

import at.schrer.inject.TestConstants;
import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;

@BeanSource
public class ValueSource {
    @Component(name = TestConstants.ComponentNamesBeanSourcePack.VAL_1)
    public static String someString(){
        return TestConstants.ComponentNamesBeanSourcePack.VAL_1;
    }

    @Component(name = TestConstants.ComponentNamesBeanSourcePack.VAL_2)
    public static String anotherString(){
        return TestConstants.ComponentNamesBeanSourcePack.VAL_2;
    }
}
