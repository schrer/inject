package at.schrer.inject.dummyclasses.safe.beansource;

import at.schrer.inject.TestConstants;
import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;

@BeanSource
public class BSTestSource {

    @Component
    public static SomeClass someClass(){
        return new SomeClass();
    }

    @Component(name = TestConstants.ComponentNames.AC_INST1)
    public static AnotherClass name1Class(){
        return new AnotherClass(TestConstants.ComponentNames.AC_INST1);
    }

    @Component(name = TestConstants.ComponentNames.AC_INST2)
    public static AnotherClass name2Class(){
        return new AnotherClass(TestConstants.ComponentNames.AC_INST2);
    }

    @Component(name = TestConstants.ComponentNames.AC_INST3)
    public static AnotherClass name3Class(){
        return new AnotherClass(TestConstants.ComponentNames.AC_INST3);
    }

    public static AnotherClass notASource(){
        return new AnotherClass("notASource");
    }
}
