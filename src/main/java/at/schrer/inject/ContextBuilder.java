package at.schrer.inject;

import at.schrer.inject.annotations.Component;
import at.schrer.inject.blueprints.BeanBluePrint;
import at.schrer.inject.blueprints.BeanDescriptor;
import at.schrer.inject.blueprints.ComponentBluePrint;
import at.schrer.inject.blueprints.FunctionBluePrint;
import at.schrer.inject.constructors.BeanConstructor;
import at.schrer.inject.exceptions.ComponentInstantiationException;
import at.schrer.inject.exceptions.ContextException;
import at.schrer.inject.structures.SomeAcyclicGraph;
import at.schrer.inject.structures.Tuple;
import at.schrer.inject.utils.BeanUtils;
import at.schrer.inject.utils.ClassScanner;
import at.schrer.inject.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
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

    private final Map<BeanDescriptor<?>, Object> componentInstances;

    private final SomeAcyclicGraph<BeanBluePrint<?>> componentGraph;

    private ContextBuilder(Set<String> packagePaths) throws ContextException {
        this.componentInstances = new HashMap<>();
        this.componentGraph = new SomeAcyclicGraph<>();


        final Set<BeanBluePrint<?>> bluePrints = createBluePrints(packagePaths);

        final List<BeanBluePrint<?>> noArgBluePrints = bluePrints.stream()
                .filter(BeanBluePrint::canBeDependencyLess)
                .toList();

        for (BeanBluePrint<?> bluePrint : noArgBluePrints) {
            this.componentGraph.addNode(bluePrint);
            bluePrints.remove(bluePrint);
        }

        int lastRunResolved = this.componentGraph.size();
        while (!bluePrints.isEmpty() && lastRunResolved > 0) {
            Set<BeanBluePrint<?>> addedToGraph = new HashSet<>();
            for (BeanBluePrint<?> bluePrint : bluePrints) {
                Optional<List<? extends BeanBluePrint<?>>> dependencyNodes =
                        getSatisfiableDependencies(bluePrint);
                if (dependencyNodes.isPresent()) {
                    componentGraph.addNode(bluePrint, (List<BeanBluePrint<?>>) dependencyNodes.get());
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

    private Set<BeanBluePrint<?>> createBluePrints(Set<String> packagePaths){
        List<Class<?>> components = new LinkedList<>();
        List<Tuple<String, Method>> sourceMethods = new LinkedList<>();

        for (String packagePath: packagePaths) {
            try {
                final ClassScanner classScanner = new ClassScanner(packagePath);
                components.addAll(classScanner.findByAnnotation(Component.class));
                sourceMethods.addAll(classScanner.findSourceFunctions());
            } catch (IOException | URISyntaxException | ClassNotFoundException e) {
                throw new ContextException("Unable to load classes for package " + packagePath, e);
            }
        }

        final Set<BeanBluePrint<?>> bluePrints = new HashSet<>();
        for (Class<?> componentClass : components) {
            bluePrints.add(new ComponentBluePrint<>(componentClass));
        }

        for (Tuple<String, Method> sourceMethod : sourceMethods) {
            if(StringUtils.isBlank(sourceMethod.left())
                    && BeanUtils.isMandatoryNameType(sourceMethod.right().getReturnType())) {
                throw new ContextException("Component defined in " + sourceMethod.right().getName() + " in class " + sourceMethod.left() + " needs a name. Basic types are required to have one.");
            }
            bluePrints.add(new FunctionBluePrint(sourceMethod.left(), sourceMethod.right()));
        }
        return bluePrints;
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
        return getComponentInternal(new BeanDescriptor<>(null, componentClass));
    }

    public <T> T getComponent(String beanAlias, Class<T> componentClass) throws ContextException {
        return getComponentInternal(new BeanDescriptor<>(beanAlias, componentClass));
    }

    private <T> T getComponentInternal(BeanDescriptor<T> beanDescriptor) throws ContextException {
        Optional<Object> match = findMatchingInstance(beanDescriptor);
        if (match.isPresent()) {
            return (T) match.get();
        }

        Optional<BeanBluePrint<?>> bluePrintOptional = componentGraph
                .find(it -> it.satisfiesDescriptor(beanDescriptor));
        if (bluePrintOptional.isEmpty()) {
            throw new ContextException("Class not found in context: " + beanDescriptor);
        }

        BeanBluePrint<?> bluePrint = bluePrintOptional.get();
        createInstanceFromBlueprint(bluePrint);

        // Store the resolved instance for the requested class
        return (T) findMatchingInstance(bluePrint.getBeanDescriptor()).get();
    }

    /**
     * Creates an instance of a class from a blueprint and adds it to the available loaded instances. All direct and transitive dependencies are built and added to the loaded instances as well.
     *
     * @param bluePrint the blueprint that should be built.
     * @throws ContextException if the class is not a known component within the context or cannot be created for other reasons.
     */
    private void createInstanceFromBlueprint(BeanBluePrint<?> bluePrint) throws ContextException {
        try {
            if (bluePrint.canBeDependencyLess()) {
                Object instance = bluePrint.getNoArgsInstance();
                componentInstances.put(bluePrint.getBeanDescriptor(), instance);
            }

            Deque<BeanBluePrint<?>> stack = new LinkedList<>();

            // Initialize stack with the root blueprint
            stack.push(bluePrint);

            while (!stack.isEmpty()) {
                BeanBluePrint<?> current = stack.peek();

                Optional<Object> currentMatch = findMatchingInstance(current.getBeanDescriptor());
                if (currentMatch.isPresent()) {
                    // Instance of blueprint already available
                    stack.pop();
                    continue;
                }

                // Get dependencies of the current blueprint
                Set<BeanBluePrint<?>> outbounds = componentGraph.getOutbounds(current);
                boolean allDependenciesResolved = true;

                for (BeanBluePrint<?> dependency : outbounds) {
                    Optional<Object> depMatch = findMatchingInstance(dependency.getBeanDescriptor());
                    if (depMatch.isPresent()) {
                        // Dependency already available
                        continue;
                    }

                    if (dependency.canBeDependencyLess()) {
                        Object instance = dependency.getNoArgsInstance();
                        componentInstances.put(dependency.getBeanDescriptor(), instance);
                    } else {
                        Optional<Object> depInstanceOpt = buildIfPossible(dependency);
                        if (depInstanceOpt.isPresent()){
                            Object instance = depInstanceOpt.get();
                            componentInstances.put(dependency.getBeanDescriptor(), instance);
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
                        throw new ContextException("Failed to instantiate " + current.getBeanClass() + ". This is a bug.");
                    }
                    Object instance = instanceOpt.get();

                    componentInstances.put(current.getBeanDescriptor(), instance);
                }
            }
        } catch (ComponentInstantiationException e) {
            throw new ContextException("Failed to create instance of class " + bluePrint.getBeanClass().getName(), e);
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
    private Optional<List<? extends BeanBluePrint<?>>> getSatisfiableDependencies(BeanBluePrint<?> bluePrint){
        if (bluePrint.canBeDependencyLess()) {
            return Optional.of(List.of());
        }

        List<? extends BeanConstructor<?>> constructors = bluePrint.getConstructors();
        for (BeanConstructor<?> constructor : constructors) {
            Optional<List<? extends BeanBluePrint<?>>> result = resolveConstructorDependencies(constructor);
            if(result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    private Optional<List<? extends BeanBluePrint<?>>> resolveConstructorDependencies(
            BeanConstructor<?> constructor
    ){
        List<BeanDescriptor<Object>> dependencies = constructor.getBeanDependencies();
        List<? extends BeanBluePrint<?>> deps = dependencies.stream()
                .map(dep -> componentGraph.find(
                        it -> it.satisfiesDescriptor(dep)
                ).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        if (deps.size() == dependencies.size()) {
            return Optional.of(deps);
        }
        return Optional.empty();
    }

    private Optional<Object> findMatchingInstance(BeanDescriptor<?> lookingFor){
        boolean ignoreNameMatch = StringUtils.isBlank(lookingFor.beanAlias());
        for (Map.Entry<BeanDescriptor<?>, Object> entry : componentInstances.entrySet()) {
            BeanDescriptor<?> descriptor = entry.getKey();
            boolean classMatches = descriptor.descriptorHoldsSubClassOf(lookingFor.beanClass());
            boolean nameMatchesOrIsIgnored = ignoreNameMatch || lookingFor.isMatchingName(descriptor.beanAlias());
            if (classMatches && nameMatchesOrIsIgnored) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    private Optional<Object> buildIfPossible(BeanBluePrint<?> blueprint) throws ComponentInstantiationException {
        if(blueprint.canBeDependencyLess()) {
            return Optional.of(blueprint.getNoArgsInstance());
        }

        List<? extends BeanConstructor<?>> constructors = blueprint.getConstructors();
        for (var constructor : constructors) {
            List<BeanDescriptor<Object>> dependencies = constructor.getBeanDependencies();
            List<Tuple<BeanDescriptor<Object>,Object>> instances = dependencies.stream()
                    .map(it -> new Tuple<>(it, findMatchingInstance(it)))
                    .filter(it -> it.right().isPresent())
                    .map(it -> new Tuple<>(it.left(), it.right().get()))
                    .toList();
            if (instances.size() == dependencies.size()) {
                return Optional.of(constructor.getInstance(instances));
            }
        }
        return Optional.empty();
    }
}
