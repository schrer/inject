package at.schrer.inject.dummyclasses.illegal.cycledep;

import at.schrer.inject.annotations.Component;

@Component
public class Cycle2 {
    private final Cycle1 cycle1;

    public Cycle2(Cycle1 cycle1) {
        this.cycle1 = cycle1;
    }
}
