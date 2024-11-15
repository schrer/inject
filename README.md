# inject - a Java dependency injection library

Implementation of a dependency injection library for Java 21.
This is only a pastime, so it is neither complete nor tested. I also did not look up best practices, no guarantees are made, you know the drill.

Basic features and restrictions are:

- Register classes as "Components" for injection via annotation
- Constructor injection of other Components
- No field injection
- Failing early when context is inconsistent or contains cyclic dependencies
- Singleton scope for all components
- Interfaces and abstract classes supported for requesting instances

## Usage

Mark your classes with the annotation [@Component](./src/main/java/at/schrer/inject/annotations/Component.java), then use the class [ContextBuilder](./src/main/java/at/schrer/inject/ContextBuilder.java) to load your Java package and build instances of your classes.

```java
// Create context once
ContextBuilder contextBuilder = ContextBuilder.getContextInstance("at.schrer.inject");

// Request instances
YourService service1 = contextBuilder.getComponent(YourService.class);

// YourOtherService depends on YourService.
// YourOtherService got the same instance of it injected as you already requested.
YourOtherService service2 = contextBuilder.getComponent(YourOtherService.class);
```

## Notable classes

- The [ClassScanner](./src/main/java/at/schrer/inject/ClassScanner.java), which is able to return a list of classes under a provided package name. The classes can also be filtered by annotations.
- The [ContextBuilder](./src/main/java/at/schrer/inject/ContextBuilder.java), which can instantiate classes marked with the annotation [@Component](./src/main/java/at/schrer/inject/annotations/Component.java) from a provided package.
- An implementation of an [acyclic graph](./src/main/java/at/schrer/inject/structures/SomeAcyclicGraph.java). It is used in the ContextBuilder to build the dependency graph between all components.
