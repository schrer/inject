package at.schrer.inject.dummyclasses.safe.interdep.pack2;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.dummyclasses.safe.interdep.pack1.Pack1Class2;

@Component
public class Pack2Class1 {
    private final Pack1Class2 pack1Class2;

    public Pack2Class1(Pack1Class2 pack1Class2) {
        this.pack1Class2 = pack1Class2;
    }
}
