package at.schrer.inject;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.blueprints.ComponentBluePrint;
import at.schrer.inject.exceptions.ComponentInstantiationException;
import at.schrer.inject.exceptions.ContextException;
import at.schrer.inject.structures.SomeAcyclicGraph;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ContextBuilder class is used to scan a package for available components (annotated with @Component) and create and provide instances of them.
 * A contextbuilder instance can be made through the getContextInstance function. If a contextbuilder for a specific package was already created,
 * the same instance will be returned again when a getContextInstance is called with that same package name again.
 * So only one contextbuilder will be created per package and is then reused.
 *
 * Instances are created as singletons. This means an instance will be created only once. If the same component class is requested again, the previously created instance is returned again.
 * The ContextBuilder create instances of components lazily, only once they are requested (or needed as dependency for other components).
 *
 * If a package contains components with unsatisfiable dependencies (circular dependencies, no components without dependencies), getContextInstance method throws a ContextException.
 *
 */
public class ContextBuilder {

    private static final Map<Set<String>, ContextBuilder> loadedBuilders = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> componentInstances;

    private final SomeAcyclicGraph<ComponentBluePrint<Class<?>>> componentGraph;

    private ContextBuilder(Set<String> packagePaths) throws ContextException {
        this.componentInstances = new HashMap<>();
        this.componentGraph = new SomeAcyclicGraph<>();

        List<Class<?>> components = new LinkedList<>();

        for (String packagePath: packagePaths) {
            final ClassScanner classScanner = new ClassScanner(packagePath);
            try {
                components.addAll(classScanner.findByAnnotation(Component.class));
            } catch (IOException | URISyntaxException | ClassNotFoundException e) {
                throw new ContextException("Unable to load classes for package " + packagePath, e);
            }
        }

        final Set<ComponentBluePrint<Class<?>>> bluePrints = new HashSet<>();
        for (Class<?> componentClass : components) {
            bluePrints.add((ComponentBluePrint<Class<?>>) new ComponentBluePrint<>(componentClass));
        }

        final List<ComponentBluePrint<Class<?>>> noArgBluePrints = bluePrints.stream()
                .filter(ComponentBluePrint::canBeDependencyLess)
                .toList();

        for (ComponentBluePrint<Class<?>> bluePrint : noArgBluePrints) {
            this.componentGraph.addNode(bluePrint);
            bluePrints.remove(bluePrint);
        }

        int lastRunResolved = this.componentGraph.size();
        while (!bluePrints.isEmpty() && lastRunResolved > 0) {
            Set<ComponentBluePrint<Class<?>>> addedToGraph = new HashSet<>();
            for (ComponentBluePrint<Class<?>> bluePrint : bluePrints) {
                Optional<List<ComponentBluePrint<Class<?>>>> dependencyNodes =
                        getSatisfiableDependencies(bluePrint);
                if (dependencyNodes.isPresent()) {
                    componentGraph.addNode(bluePrint, dependencyNodes.get());
                    addedToGraph.add(bluePrint);
                }
            }

            lastRunResolved = addedToGraph.size();

            addedToGraph.forEach(bluePrints::remove);
            addedToGraph.clear();
        }

        if (!bluePrints.isEmpty()) {
            throw new ContextException("Unable to resolve dependencies for " + bluePrints.size() + " components.");
        }
    }

    /**
     * Get an instance of a component class.
     * Either a new instance is created, or if an instance of the same component was requested previously, the same instance is returned again.
     *
     * @param componentClass the class of the component
     * @return an instance of the component
     * @param <T> the type of the component
     * @throws ContextException if the class is not a known component within the context or cannot be created for other reasons.
     */
    public <T> T getComponent(Class<T> componentClass) throws ContextException {
        // TODO support names
        Optional<Object> match = findMatchingInstance(componentClass);
        if (match.isPresent()) {
            return (T) match.get();
        }

        Optional<ComponentBluePrint<Class<?>>> bluePrintOptional = componentGraph.find(it -> it.isMatchingClass(componentClass));
        if (bluePrintOptional.isEmpty()) {
            throw new ContextException("Class not found in context: " + componentClass.getName());
        }

        ComponentBluePrint<Class<?>> bluePrint = bluePrintOptional.get();
        createInstanceFromBlueprint(bluePrint);

        // Store the resolved instance for the requested class
        return (T) findMatchingInstance(bluePrint.getComponentClass()).get();
    }

    /**
     * Creates an instance of a class from a blueprint and adds it to the available loaded instances. All direct and transitive dependencies are built and added to the loaded instances as well.
     *
     * @param bluePrint the blueprint that should be built.
     * @throws ContextException if the class is not a known component within the context or cannot be created for other reasons.
     */
    private void createInstanceFromBlueprint(ComponentBluePrint<Class<?>> bluePrint) throws ContextException {
        try {
            if (bluePrint.canBeDependencyLess()) {
                Object instance = bluePrint.getNoArgsInstance();
                componentInstances.put(instance.getClass(), instance);
            }

            Deque<ComponentBluePrint<Class<?>>> stack = new LinkedList<>();

            // Initialize stack with the root blueprint
            stack.push(bluePrint);

            while (!stack.isEmpty()) {
                ComponentBluePrint<Class<?>> current = stack.peek();

                Class<?> currentClass = current.getComponentClass();
                Optional<Object> currentMatch = findMatchingInstance(currentClass);
                if (currentMatch.isPresent()) {
                    // Instance of blueprint already available
                    stack.pop();
                    continue;
                }

                // Get dependencies of the current blueprint
                Set<ComponentBluePrint<Class<?>>> outbounds = componentGraph.getOutbounds(current);
                boolean allDependenciesResolved = true;

                for (ComponentBluePrint<Class<?>> dependency : outbounds) {
                    Class<?> depClass = dependency.getComponentClass();
                    Optional<Object> depMatch = findMatchingInstance(depClass);
                    if (depMatch.isPresent()) {
                        // Dependency already available
                        continue;
                    }

                    if (dependency.canBeDependencyLess()) {
                        Object instance = dependency.getNoArgsInstance();
                        componentInstances.put(instance.getClass(), instance);
                    } else {
                        Optional<Object> depInstanceOpt = buildIfPossible(dependency);
                        if (depInstanceOpt.isPresent()){
                            Object instance = depInstanceOpt.get();
                            componentInstances.put(instance.getClass(), instance);
                        } else {
                            stack.push(dependency); // Push unresolved dependency onto the stack
                            allDependenciesResolved = false;
                        }
                    }
                }

                if (allDependenciesResolved) {
                    // Create instance for the current blueprint
                    Optional<Object> instanceOpt = buildIfPossible(current);
                    if (instanceOpt.isEmpty()) {
                        throw new ContextException("Failed to instantiate " + current.getComponentClass() + ". This is a bug.");
                    }
                    Object instance = instanceOpt.get();

                    componentInstances.put(instance.getClass(), instance);
                }
            }
        } catch (ComponentInstantiationException e) {
            throw new ContextException("Failed to create instance of class " + bluePrint.getComponentClass().getName(), e);
        }
    }

    /**
     * Get a ContextBuilder instance for the given set of packages.
     * At least one needs to be provided, all package paths must be unique.
     *
     * @param packagePaths the packages to scan for components.
     * @return a ContextBuilder instance, initialized on the given packages.
     * @throws ContextException if an error happens while trying to load the components of the given package.
     */
    public static ContextBuilder getContextInstance(String... packagePaths) throws ContextException {
        return getContextInstance(Set.of(packagePaths));
    }

    /**
     * Get a ContextBuilder instance for the given set of packages.
     * If the set is empty or null an exception is thrown. Same goes for null or empty string elements in the set.
     *
     * @param packagePaths the packages to scan for components.
     * @return a ContextBuilder instance, initialized on the given packages.
     * @throws ContextException if an error happens while trying to load the components of the given package.
     */
    public static ContextBuilder getContextInstance(Set<String> packagePaths) throws ContextException {
        if (packagePaths == null || packagePaths.isEmpty()) {
            throw new ContextException("At least one package path needs to be provided.");
        }
        for (String path : packagePaths) {
            if (path == null || path.isBlank()) {
                throw new ContextException("One of the package paths is null or blank. This is not allowed.");
            }
        }

        if (loadedBuilders.containsKey(packagePaths)) {
            return loadedBuilders.get(packagePaths);
        }

        synchronized (loadedBuilders) {
            if (loadedBuilders.containsKey(packagePaths)) {
                return loadedBuilders.get(packagePaths);
            }
            ContextBuilder newBuilder = new ContextBuilder(packagePaths);
            loadedBuilders.put(packagePaths, newBuilder);
            return newBuilder;
        }
    }

    /**
     * Clears all instances of ContextBuilders, allowing to create new instances for already loaded packages.
     * This is meant for testing only, to allow a clean slate after each test.
     * It does not destroy ContextBuilder instances that are still referenced in other objects/scopes. They continue to function and are not closed.
     */
    public static void clearContextInstances(){
        loadedBuilders.clear();
    }

    /**
     * Searches for a list of classes present in the graph of components that already have all dependencies fulfilled.
     * If the blueprint has a constructor without arguments, an empty list is returned, as there are no dependencies needed.
     * If no constructor can be fully satisfied with the available blueprints in the component graph, an empty Optional is returned.
     *
     * @param bluePrint the component blueprint for which satisfiable dependencies should be found
     * @return an Optional containing a list of dependencies that can satisfy one of the constructors, or an empty Optional if there is no such list
     */
    private Optional<List<ComponentBluePrint<Class<?>>>> getSatisfiableDependencies(ComponentBluePrint<Class<?>> bluePrint){
        if (bluePrint.canBeDependencyLess()) {
            return Optional.of(List.of());
        }

        List<ComponentBluePrint.ComponentConstructor<Class<?>>> constructors = bluePrint.getConstructors();
        for (ComponentBluePrint.ComponentConstructor<Class<?>> constructor : constructors) {
            var result = resolveConstructorDependencies(constructor);
            if(result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    private Optional<List<ComponentBluePrint<Class<?>>>> resolveConstructorDependencies(
            ComponentBluePrint.ComponentConstructor<Class<?>> constructor
    ){
        List<Class<?>> dependencies = constructor.getDependencies();
        List<ComponentBluePrint<Class<?>>> deps = dependencies.stream()
                .map(dep -> componentGraph.find(
                        it -> it.isMatchingClass(dep)
                ).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        if (deps.size() == dependencies.size()) {
            return Optional.of(deps);
        }
        return Optional.empty();
    }

    private Optional<Object> findMatchingInstance(Class<?> clazz){
        return componentInstances.values().stream()
                .filter(it -> clazz.isAssignableFrom(it.getClass()))
                .findFirst();
    }

    private Optional<Object> buildIfPossible(ComponentBluePrint<Class<?>> blueprint) throws ComponentInstantiationException {
        if(blueprint.canBeDependencyLess()) {
            return Optional.of(blueprint.getNoArgsInstance());
        }

        List<ComponentBluePrint.ComponentConstructor<Class<?>>> constructors = blueprint.getConstructors();
        for (var constructor : constructors) {
            List<Class<?>> dependencies = constructor.getDependencies();
            List<Object> instances = dependencies.stream()
                    .map(this::findMatchingInstance)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            if (instances.size() == dependencies.size()) {
                return Optional.of(constructor.getInstance(instances.toArray()));
            }
        }
        return Optional.empty();
    }
}
