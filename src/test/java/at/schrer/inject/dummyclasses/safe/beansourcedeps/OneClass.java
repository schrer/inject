package at.schrer.inject.dummyclasses.safe.beansourcedeps;

public class OneClass {
    private final ThreeClass threeClass;

    public OneClass(ThreeClass threeClass) {
        this.threeClass = threeClass;
    }

    public ThreeClass getTwoClass() {
        return threeClass;
    }
}
