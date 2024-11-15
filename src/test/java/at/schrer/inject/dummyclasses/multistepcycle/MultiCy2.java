package at.schrer.inject.dummyclasses.multistepcycle;

import at.schrer.inject.annotations.Component;

@Component
public class MultiCy2 {
    private final MultiCy3 multiCy3;

    public MultiCy2(MultiCy3 multiCy3) {
        this.multiCy3 = multiCy3;
    }
}
