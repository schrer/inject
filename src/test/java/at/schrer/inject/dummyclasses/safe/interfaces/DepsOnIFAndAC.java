package at.schrer.inject.dummyclasses.safe.interfaces;

import at.schrer.inject.annotations.Component;

@Component
public class DepsOnIFAndAC {
    private final SomeInterface someInterface;
    private final SomeAbstractClass someAbstractClass;

    public DepsOnIFAndAC(SomeInterface someInterface, SomeAbstractClass someAbstractClass) {
        this.someInterface = someInterface;
        this.someAbstractClass = someAbstractClass;
    }

    public SomeInterface getSomeInterface() {
        return someInterface;
    }

    public SomeAbstractClass getSomeAbstractClass() {
        return someAbstractClass;
    }
}
