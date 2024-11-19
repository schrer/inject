package at.schrer.inject;

import at.schrer.inject.dummyclasses.safe.depless.Component1;
import at.schrer.inject.dummyclasses.safe.depless.Component2;
import at.schrer.inject.dummyclasses.safe.depless.NonComponent1;
import at.schrer.inject.dummyclasses.safe.interdep.pack1.Pack1Class1;
import at.schrer.inject.dummyclasses.safe.interdep.pack1.Pack1Class2;
import at.schrer.inject.dummyclasses.safe.interdep.pack2.Pack2Class1;
import at.schrer.inject.dummyclasses.safe.interdep.pack2.Pack2Class2;
import at.schrer.inject.dummyclasses.safe.interfaces.*;
import at.schrer.inject.dummyclasses.safe.name.*;
import at.schrer.inject.dummyclasses.safe.yesdeps.DepComp1;
import at.schrer.inject.dummyclasses.safe.yesdeps.DepComp2;
import at.schrer.inject.dummyclasses.safe.yesdeps.DepComp4;
import at.schrer.inject.exceptions.ContextException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static at.schrer.inject.TestConstants.Packages.*;
import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest {

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

    @Test
    void severalContextsWithInterdep(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(
                INTERDEP_PACK1_PACKAGE, INTERDEP_PACK2_PACKAGE
        );
        // When
        Pack1Class1 p1c1 = contextBuilder.getComponent(Pack1Class1.class);
        Pack1Class2 p1c2 = contextBuilder.getComponent(Pack1Class2.class);
        Pack2Class1 p2c1 = contextBuilder.getComponent(Pack2Class1.class);
        Pack2Class2 p2c2 = contextBuilder.getComponent(Pack2Class2.class);
        // Then
        assertNotNull(p1c1);
        assertNotNull(p1c2);
        assertNotNull(p2c1);
        assertNotNull(p2c2);
    }

    @Test
    void nullElementInPackageSet(){
        // Given
        Set<String> packages = new HashSet<>();
        packages.add("at.schrer.util");
        packages.add(null);
        packages.add("at.schrer.inject");

        // When/Then
        assertThrows(ContextException.class, () -> ContextBuilder.getContextInstance(packages));
    }

    @Test
    void blankElementInPackageSet(){
        // Given
        Set<String> packages = Set.of("at.schrer.util", "   ", "at.schrer.inject");
        // When/Then
        assertThrows(ContextException.class, () -> ContextBuilder.getContextInstance(packages));
    }

    @Test
    void emptyPackageSet(){
        // Given
        Set<String> packages = Set.of();
        // When/Then
        assertThrows(ContextException.class, () -> ContextBuilder.getContextInstance(packages));
    }

    @Test
    void emptyPackageArray(){
        assertThrows(ContextException.class, ContextBuilder::getContextInstance);
    }

    @Test
    void bigContext(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(SAFE_PACKAGE);
        // When
        var comp1 = contextBuilder.getComponent(Component2.class);
        var comp2 = contextBuilder.getComponent(Pack1Class2.class);
        var comp3 = contextBuilder.getComponent(Pack1Class2.class);
        var comp4 = contextBuilder.getComponent(NamedDummy.class);
        // Then
        assertNotNull(comp1);
        assertNotNull(comp2);
        assertNotNull(comp3);
        assertNotNull(comp4);
    }

    @Test
    @DisplayName("Calling getContextInstance with same package should return the same builder")
    void severalContextBuilders(){
        // Given
        Set<String> packSet1 = Set.of(YES_DEP_DUMMY_PACKAGE);
        Set<String> packSet2 = Set.of(YES_DEP_DUMMY_PACKAGE, INTERDEP_PACKAGE);
        Set<String> packSet3 = Set.of("com.example");
        // When
        var builder1Set1 = ContextBuilder.getContextInstance(packSet1);
        var builder2Set1 = ContextBuilder.getContextInstance(packSet1);
        var builder1Set2 = ContextBuilder.getContextInstance(packSet2);
        var builder2Set2 = ContextBuilder.getContextInstance(packSet2);
        var builder1Set3 = ContextBuilder.getContextInstance(packSet3);
        var builder2Set3 = ContextBuilder.getContextInstance(packSet3);
        // Then
        assertNotNull(builder1Set1);
        assertNotNull(builder1Set2);
        assertNotNull(builder1Set3);
        assertSame(builder1Set1, builder2Set1);
        assertSame(builder1Set2, builder2Set2);
        assertSame(builder1Set3, builder2Set3);
        assertNotSame(builder1Set1, builder1Set2);
        assertNotSame(builder1Set1, builder1Set3);
        assertNotSame(builder1Set2, builder1Set3);
    }

    @Test
    void getComponentByName(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(NAMED_PACKAGE);

        // When
        var randomInstance = contextBuilder.getComponent(NamingInterface.class);
        var namedDummy = contextBuilder.getComponent(NamingInterface.NAMEDDUMMY,NamingInterface.class);
        var moreNamedDummy = contextBuilder.getComponent(NamingInterface.MORENAMEDDUMMY, NamingInterface.class);
        var evenMoreNamedDummy = contextBuilder.getComponent(NamingInterface.EVENMORENAMEDDUMMY, NamingInterface.class);
        // Then
        assertNotNull(randomInstance);
        assertNotNull(namedDummy);
        assertNotNull(moreNamedDummy);
        assertNotNull(evenMoreNamedDummy);

        assertEquals(NamedDummy.class, namedDummy.getClass());
        assertEquals(MoreNamedDummy.class, moreNamedDummy.getClass());
        assertEquals(EvenMoreNamedDummy.class, evenMoreNamedDummy.getClass());
    }

    @Test
    void exceptionOnUnknownName(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(NAMED_PACKAGE);

        // When/Then
        assertThrows(ContextException.class, () -> contextBuilder.getComponent("wrongName", NamingInterface.class));
        assertThrows(ContextException.class, () -> contextBuilder.getComponent("wrongName", NoNameDummy.class));
        assertThrows(ContextException.class, () -> contextBuilder.getComponent("wrongName", NamedDummy.class));
    }

    @Test
    void injectByName(){
        // Given
        ContextBuilder contextBuilder = ContextBuilder.getContextInstance(NAMED_PACKAGE);

        // When
        var usesNamedDummies = contextBuilder.getComponent(UsesNamedDummies.class);
        var namedDummy = usesNamedDummies.getNamedDummy();
        var moreNamedDummy = usesNamedDummies.getMoreNamedDummy();
        var evenMoreNamedDummy = usesNamedDummies.getEvenMoreNamedDummy();
        // Then
        assertNotNull(usesNamedDummies);
        assertNotNull(namedDummy);
        assertNotNull(moreNamedDummy);
        assertNotNull(evenMoreNamedDummy);

        assertEquals(NamedDummy.class, namedDummy.getClass());
        assertEquals(MoreNamedDummy.class, moreNamedDummy.getClass());
        assertEquals(EvenMoreNamedDummy.class, evenMoreNamedDummy.getClass());
    }
}
