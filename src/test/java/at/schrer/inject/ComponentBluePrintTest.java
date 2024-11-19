package at.schrer.inject;

import at.schrer.inject.blueprints.ComponentBluePrint;
import at.schrer.inject.dummyclasses.safe.name.NamedDummy;
import at.schrer.inject.dummyclasses.safe.name.NoNameDummy;
import org.junit.jupiter.api.Test;

import static at.schrer.inject.dummyclasses.safe.name.NamingInterface.NAMEDDUMMY;
import static org.junit.jupiter.api.Assertions.*;

class ComponentBluePrintTest {

    @Test
    void loadName(){
        // Given
        String namedDummyName = NAMEDDUMMY;
        ComponentBluePrint<NoNameDummy> noNameBluePrint = new ComponentBluePrint<>(NoNameDummy.class);
        ComponentBluePrint<NamedDummy> namedBluePrint = new ComponentBluePrint<>(NamedDummy.class);

        // Then
        assertTrue(noNameBluePrint.getBeanAlias().isEmpty());
        assertTrue(namedBluePrint.getBeanAlias().isPresent());
        assertEquals(namedDummyName, namedBluePrint.getBeanAlias().get());
    }
}
