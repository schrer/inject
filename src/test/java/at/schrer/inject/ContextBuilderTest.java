package at.schrer.inject;

import at.schrer.inject.dummyclasses.depless.Component1;
import at.schrer.inject.dummyclasses.depless.Component2;
import at.schrer.inject.dummyclasses.depless.NonComponent1;
import at.schrer.inject.dummyclasses.interfaces.*;
import at.schrer.inject.dummyclasses.interfaces.*;
import at.schrer.inject.dummyclasses.yesdeps.DepComp1;
import at.schrer.inject.dummyclasses.yesdeps.DepComp2;
import at.schrer.inject.dummyclasses.yesdeps.DepComp4;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest {

    private static final String NO_DEP_DUMMY_PACKAGE = "at.schrer.inject.dummyclasses.depless";
    private static final String NO_DEP_DUMMY_PACKAGE_SUB = "at.schrer.inject.dummyclasses.depless.sub";

    private static final String YES_DEP_DUMMY_PACKAGE = "at.schrer.inject.dummyclasses.yesdeps";
    private static final String MULTISTEP_CYCLE_DEP_DUMMY_PACKAGE = "at.schrer.inject.dummyclasses.multistepcycle";
    private static final String CYCLE_DEP_DUMMY_PACKAGE = "at.schrer.inject.dummyclasses.cycledep";
    private static final String INTERFACE_PACKAGE = "at.schrer.inject.dummyclasses.interfaces";

    @BeforeEach
    void cleanUpComponentLoaders(){
        ContextBuilder.clearContextInstances();
    }

    @Test
    void instantiateContext() {
        Assertions.assertDoesNotThrow(() -> ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE));
    }

    @Test
    void getTwoBuildersOnSamePackage() {
        // When
        ContextBuilder contextBuilder1 = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE);
        ContextBuilder contextBuilder2 = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE);
        // Then
        assertEquals(contextBuilder1, contextBuilder2);
    }

    @Test
    void getTwoBuildersOnDiffPackage() {
        // When
        ContextBuilder contextBuilder1 = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE);
        ContextBuilder contextBuilder2 = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE_SUB);
        // Then
        assertNotEquals(contextBuilder1, contextBuilder2);
    }

    @Test
    void loadComponent() {
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE);
        // When
        Component1 instance1 = contextBuilder.getComponent(Component1.class);
        // Then
        assertNotNull(instance1);
    }

    @Test
    void checkSingletonBehavior() {
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE);
        // When
        Component1 instance1 = contextBuilder.getComponent(Component1.class);
        Component2 instance2 = contextBuilder.getComponent(Component2.class);
        Component1 instance3 = contextBuilder.getComponent(Component1.class);
        Component2 instance4 = contextBuilder.getComponent(Component2.class);
        // Then
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertEquals(instance1, instance3);
        assertEquals(instance2, instance4);
    }

    @Test
    void loadNonComponent() {
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(NO_DEP_DUMMY_PACKAGE);
        // When / Then
        assertThrows(ContextException.class, () -> contextBuilder.getComponent(NonComponent1.class));
    }

    @Test
    void instantiateContext_deps() {
        Assertions.assertDoesNotThrow(() -> ContextBuilder.getContextInstance(YES_DEP_DUMMY_PACKAGE));
    }

    @Test
    void instantiateContext_cycle() {
        assertThrows(ContextException.class, () -> ContextBuilder.getContextInstance(CYCLE_DEP_DUMMY_PACKAGE));
    }

    @Test
    void instantiateContext_multiStepCycle() throws ContextException {
        assertThrows(ContextException.class, () -> ContextBuilder.getContextInstance(MULTISTEP_CYCLE_DEP_DUMMY_PACKAGE));
    }

    @Test
    void loadComponentWithoutDeps_yesdeps(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(YES_DEP_DUMMY_PACKAGE);
        // When
        DepComp1 instance1 = contextBuilder.getComponent(DepComp1.class);
        // Then
        assertNotNull(instance1);
    }

    @Test
    void loadComponentWithOneDep_yesdeps(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(YES_DEP_DUMMY_PACKAGE);
        // When
        DepComp2 instance = contextBuilder.getComponent(DepComp2.class);
        // Then
        assertNotNull(instance);
    }

    @Test
    void loadComponentWith3Deps_yesdeps(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(YES_DEP_DUMMY_PACKAGE);
        // When
        DepComp4 instance = contextBuilder.getComponent(DepComp4.class);
        // Then
        assertNotNull(instance);
    }

    @Test
    void loadInterfaceImplementation(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(INTERFACE_PACKAGE);
        // When
        SomeInterface instance = contextBuilder.getComponent(SomeInterface.class);
        // Then
        assertNotNull(instance);
        assertEquals(SomeIFImplementation.class, instance.getClass());
    }

    @Test
    void loadAbstractClassImplementation(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(INTERFACE_PACKAGE);
        // When
        SomeAbstractClass instance = contextBuilder.getComponent(SomeAbstractClass.class);
        // Then
        assertNotNull(instance);
        assertEquals(SomeACImplementation.class, instance.getClass());
    }

    @Test
    void loadClassDependentOnInterface(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(INTERFACE_PACKAGE);
        // When
        UsesInterface instance = contextBuilder.getComponent(UsesInterface.class);
        // Then
        assertNotNull(instance);
        assertNotNull(instance.getSomeInterface());
        assertEquals(SomeIFImplementation.class, instance.getSomeInterface().getClass());
        assertEquals(UsesInterface.class, instance.getClass());
    }

    @Test
    void loadDepsDeep(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(INTERFACE_PACKAGE);
        // When
        DepsDeep instance = contextBuilder.getComponent(DepsDeep.class);
        // Then
        assertNotNull(instance);
        assertNotNull(instance.getSomeInterface());
        assertNotNull(instance.getSomeAbstractClass());
        assertNotNull(instance.getDepsOnIFAndAC());
        assertNotNull(instance.getDepsOnIFAndAC().getSomeAbstractClass());
        assertNotNull(instance.getDepsOnIFAndAC().getSomeInterface());
    }

    @Test
    void injectEqualsDirectCreation(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(YES_DEP_DUMMY_PACKAGE);
        // When
        DepComp2 depComp2 = contextBuilder.getComponent(DepComp2.class);
        DepComp1 depComp1 = contextBuilder.getComponent(DepComp1.class);
        // Then
        assertEquals(depComp1, depComp2.getDep());
    }

    @Test
    void injectEqualsDirectCreationOtherDirection(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(YES_DEP_DUMMY_PACKAGE);
        // When
        DepComp1 depComp1 = contextBuilder.getComponent(DepComp1.class);
        DepComp2 depComp2 = contextBuilder.getComponent(DepComp2.class);
        // Then
        assertEquals(depComp1, depComp2.getDep());
    }
}
