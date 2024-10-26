package at.schrer.utils.inject.dummyclasses.multistepcycle;

import at.schrer.utils.inject.annotations.Component;

@Component
public class MultiCy3 {
    private final MultiCy1 multiCy1;

    public MultiCy3(MultiCy1 multiCy1) {
        this.multiCy1 = multiCy1;
    }
}
