package at.schrer.inject.dummyclasses.illegal.errorinsourcefun;

import at.schrer.inject.TestConstants;
import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;

@BeanSource
public class TheProblemSource {
    private TheProblemSource(){}

    @Component(name = TestConstants.ProblemObjects.PROB1)
    public static String nullObject() {
        return null;
    }

    @Component(name = TestConstants.ProblemObjects.PROB2)
    public static String throwAnException() {
        throw new RuntimeException("Something fucked up happened here.");
    }
}
