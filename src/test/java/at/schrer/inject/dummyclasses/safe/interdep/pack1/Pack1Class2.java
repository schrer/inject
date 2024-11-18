package at.schrer.inject.dummyclasses.safe.interdep.pack1;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.dummyclasses.safe.interdep.pack2.Pack2Class2;

@Component
public class Pack1Class2 {
    private final Pack2Class2 pack2Class2;

    public Pack1Class2(Pack2Class2 pack2Class2) {
        this.pack2Class2 = pack2Class2;
    }
}
