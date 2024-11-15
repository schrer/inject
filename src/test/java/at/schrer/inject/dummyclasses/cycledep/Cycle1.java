package at.schrer.inject.dummyclasses.cycledep;

import at.schrer.inject.annotations.Component;

@Component
public class Cycle1 {
    private final Cycle2 cycle2;

    public Cycle1(Cycle2 cycle2) {
        this.cycle2 = cycle2;
    }
}
