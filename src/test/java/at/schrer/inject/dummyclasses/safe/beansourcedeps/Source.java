package at.schrer.inject.dummyclasses.safe.beansourcedeps;

import at.schrer.inject.TestConstants;
import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.ByName;
import at.schrer.inject.annotations.Component;

@BeanSource
public class Source {
    private Source(){}

    @Component
    public static OneClass oneClass(ThreeClass threeClass){
        return new OneClass(threeClass);
    }

    @Component
    public static DepsNeedToBeInOrder inOrder(
            @ByName(TestConstants.ComponentNamesBeanSourceDepPack.TC1) TwoClass tc1,
            @ByName(TestConstants.ComponentNamesBeanSourceDepPack.TC2) TwoClass tc2,
            @ByName(TestConstants.ComponentNamesBeanSourceDepPack.TC3) TwoClass tc3
    ){
        return new DepsNeedToBeInOrder(tc1, tc2, tc3);
    }

    @Component(name = TestConstants.ComponentNamesBeanSourceDepPack.TC1)
    public static TwoClass tcOne(){
        return new TwoClass(TestConstants.ComponentNamesBeanSourceDepPack.TC1);
    }

    @Component(name = TestConstants.ComponentNamesBeanSourceDepPack.TC3)
    public static TwoClass tcThree(){
        return new TwoClass(TestConstants.ComponentNamesBeanSourceDepPack.TC3);
    }

    @Component
    public static TwoClass tcUnnamed(){
        return new TwoClass("unnamed");
    }

    @Component(name = TestConstants.ComponentNamesBeanSourceDepPack.TC2)
    public static TwoClass tcTwo(){
        return new TwoClass(TestConstants.ComponentNamesBeanSourceDepPack.TC2);
    }
}
