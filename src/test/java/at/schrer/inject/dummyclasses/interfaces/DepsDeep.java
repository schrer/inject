package at.schrer.inject.dummyclasses.interfaces;

import at.schrer.inject.annotations.Component;

@Component
public class DepsDeep {
    private final DepsOnIFAndAC depsOnIFAndAC;
    private final SomeInterface someInterface;
    private final SomeAbstractClass someAbstractClass;

    public DepsDeep(DepsOnIFAndAC depsOnIFAndAC, SomeInterface someInterface, SomeAbstractClass someAbstractClass) {
        this.depsOnIFAndAC = depsOnIFAndAC;
        this.someInterface = someInterface;
        this.someAbstractClass = someAbstractClass;
    }

    public DepsOnIFAndAC getDepsOnIFAndAC() {
        return depsOnIFAndAC;
    }

    public SomeInterface getSomeInterface() {
        return someInterface;
    }

    public SomeAbstractClass getSomeAbstractClass() {
        return someAbstractClass;
    }
}
