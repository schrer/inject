package at.schrer.inject.dummyclasses.safe.beansourcedeps;

public class OneClass {
    private final TwoClass twoClass;

    public OneClass(TwoClass twoClass) {
        this.twoClass = twoClass;
    }

    public TwoClass getTwoClass() {
        return twoClass;
    }
}
