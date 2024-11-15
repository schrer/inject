package at.schrer.inject.dummyclasses.interfaces;

import at.schrer.inject.annotations.Component;

@Component
public class UsesInterface {
    private final SomeInterface someInterface;

    public UsesInterface(SomeInterface someInterface) {
        this.someInterface = someInterface;
    }

    public SomeInterface getSomeInterface() {
        return someInterface;
    }
}
