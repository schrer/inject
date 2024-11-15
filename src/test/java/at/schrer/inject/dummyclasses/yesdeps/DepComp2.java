package at.schrer.inject.dummyclasses.yesdeps;

import at.schrer.inject.annotations.Component;

@Component
public class DepComp2 {

    private final DepComp1 dep;

    public DepComp2(DepComp1 dep) {
        this.dep = dep;
    }

    public DepComp1 getDep() {
        return dep;
    }
}
