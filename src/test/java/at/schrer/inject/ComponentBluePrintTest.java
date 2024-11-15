package at.schrer.inject;

import at.schrer.inject.blueprints.ComponentBluePrint;
import at.schrer.inject.dummyclasses.name.NamedDummy;
import at.schrer.inject.dummyclasses.name.NoNameDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentBluePrintTest {

    @Test
    void loadName(){
        // Given
        String namedDummyName = "thisDummyIsNamed";
        ComponentBluePrint<NoNameDummy> noNameBluePrint = new ComponentBluePrint<>(NoNameDummy.class);
        ComponentBluePrint<NamedDummy> namedBluePrint = new ComponentBluePrint<>(NamedDummy.class);

        // Then
        assertTrue(noNameBluePrint.getBeanAlias().isEmpty());
        assertTrue(namedBluePrint.getBeanAlias().isPresent());
        assertEquals(namedDummyName, namedBluePrint.getBeanAlias().get());
    }
}
