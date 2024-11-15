package at.schrer.inject.dummyclasses.yesdeps;

import at.schrer.inject.annotations.Component;

@Component
public class DepComp3 {
    private final DepComp1 dep;


    public DepComp3(DepComp1 dep) {
        this.dep = dep;
    }
}
