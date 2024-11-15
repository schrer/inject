package at.schrer.inject.dummyclasses.yesdeps;

import at.schrer.inject.annotations.Component;

@Component
public class DepComp4 {

    private final DepComp1 dep1;
    private final DepComp2 dep2;
    private final DepComp3 dep3;

    public DepComp4(DepComp1 dep1, DepComp2 dep2, DepComp3 dep3) {
        this.dep1 = dep1;
        this.dep2 = dep2;
        this.dep3 = dep3;
    }
}
