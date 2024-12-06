# inject - a Java dependency injection library

Implementation of a dependency injection library for Java 21.
This is only a pastime, so it is neither complete nor tested. I also did not look up best practices, no guarantees are made, you know the drill.

Basic features and restrictions are:

- Create contexts with a specified package coverage
  - Specify several packages, components can depend on components from other packages
  - Reuse of existing bean contexts if same package coverage is requested again
  - Fail early when context is inconsistent or contains cyclic dependencies
- Bean registration via annotation
  - Register classes as "Components" for injection via annotation
  - Mark methods with a return type as Component sources to create instances from their classes (methods without parameters only for the moment)
  - Specify a name for components to support multiple alternative implementations of an interface or abstract class, as well as basic types like String and Integer
  - Singleton scope for all components within one context
- Constructor injection of other beans/components
  - Mark constructor parameters with the [@ByName](./src/main/java/at/schrer/inject/annotations/ByName.java) annotation for matching by bean/component name
  - No field injection
  - Interfaces and abstract classes supported for injection or explicit instance loading

## Usage

Mark your classes with the annotation [@Component](./src/main/java/at/schrer/inject/annotations/Component.java), then use the class [ContextBuilder](./src/main/java/at/schrer/inject/ContextBuilder.java) to load your Java package and build instances of your classes.

```java
// Create context once
ContextBuilder contextBuilder = ContextBuilder.getContextInstance("at.schrer.inject", "at.schrer.example", "at.schrer.util");

// Request instances
YourService service1 = contextBuilder.getComponent(YourService.class);

// YourOtherService depends on YourService.
// YourOtherService got the same instance of it injected as you already requested.
YourOtherService service2 = contextBuilder.getComponent(YourOtherService.class);
```

## Notable classes

- The [ContextBuilder](./src/main/java/at/schrer/inject/ContextBuilder.java), which can instantiate classes marked with the annotation [@Component](./src/main/java/at/schrer/inject/annotations/Component.java) from a provided package.
- The [ClassScanner](./src/main/java/at/schrer/inject/utils/ClassScanner.java), which is able to return a list of classes under a provided package name. The classes can also be filtered by annotations.
- An implementation of an [acyclic graph](./src/main/java/at/schrer/inject/structures/SomeAcyclicGraph.java). It is used in the ContextBuilder to build the dependency graph between all components.
