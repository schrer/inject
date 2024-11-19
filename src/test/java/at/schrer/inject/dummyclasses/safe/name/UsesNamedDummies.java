package at.schrer.inject.dummyclasses.safe.name;

import at.schrer.inject.annotations.ByName;
import at.schrer.inject.annotations.Component;

@Component
public class UsesNamedDummies {
    private final NamingInterface namedDummy;
    private final NamingInterface moreNamedDummy;
    private final NamingInterface evenMoreNamedDummy;

    public UsesNamedDummies(
            @ByName(NamingInterface.NAMEDDUMMY) NamingInterface namedDummy,
            @ByName(NamingInterface.MORENAMEDDUMMY) NamingInterface moreNamedDummy,
            @ByName(NamingInterface.EVENMORENAMEDDUMMY) NamingInterface evenMoreNamedDummy
    ) {
        this.namedDummy = namedDummy;
        this.moreNamedDummy = moreNamedDummy;
        this.evenMoreNamedDummy = evenMoreNamedDummy;
    }

    public NamingInterface getNamedDummy() {
        return namedDummy;
    }

    public NamingInterface getMoreNamedDummy() {
        return moreNamedDummy;
    }

    public NamingInterface getEvenMoreNamedDummy() {
        return evenMoreNamedDummy;
    }
}
