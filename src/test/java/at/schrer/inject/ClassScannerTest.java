package at.schrer.inject;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.dummyclasses.depless.Component1;
import at.schrer.inject.dummyclasses.depless.Component2;
import at.schrer.inject.dummyclasses.depless.NonComponent1;
import at.schrer.inject.dummyclasses.depless.sub.Component3;
import at.schrer.inject.dummyclasses.depless.sub.NonComponent2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassScannerTest {
    private static final String NO_DEP_DUMMY_PACKAGE = "at.schrer.inject.dummyclasses.depless";

    @Test
    void loadAllClasses() throws IOException, URISyntaxException, ClassNotFoundException {
        // Given
        ClassScanner scanner = new ClassScanner(NO_DEP_DUMMY_PACKAGE);
        // When
        List<Class<?>> classes = scanner.findClassesInPackage();
        // Then
        assertEquals(5, classes.size());
        assertTrue(classes.containsAll(List.of(Component1.class, Component2.class, Component3.class)),
                "One of the components is missing");
        assertTrue(classes.containsAll(List.of(NonComponent1.class, NonComponent2.class)),
                "One of the non-components is missing");
    }

    @Test
    void loadComponents() throws IOException, URISyntaxException, ClassNotFoundException {
        // Given
        ClassScanner scanner = new ClassScanner(NO_DEP_DUMMY_PACKAGE);
        // When
        List<Class<?>> classes = scanner.findByAnnotation(Component.class);
        // Then
        assertEquals(3, classes.size());
        assertTrue(classes.containsAll(List.of(Component1.class, Component2.class, Component3.class)));
    }
}
