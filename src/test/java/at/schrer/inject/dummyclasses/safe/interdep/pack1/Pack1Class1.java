package at.schrer.inject.dummyclasses.safe.interdep.pack1;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.dummyclasses.safe.interdep.pack2.Pack2Class1;

@Component
public class Pack1Class1 {
    private final Pack2Class1 pack2Class1;

    public Pack1Class1(Pack2Class1 pack2Class1) {
        this.pack2Class1 = pack2Class1;
    }
}
