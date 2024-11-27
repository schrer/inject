package at.schrer.inject.utils;

import at.schrer.inject.annotations.BeanSource;
import at.schrer.inject.annotations.Component;
import at.schrer.inject.structures.Tuple;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class ClassScanner {

    private final String scanPackage;
    private final List<Class<?>> foundClasses;

    public ClassScanner(String scanPackage) throws IOException, URISyntaxException, ClassNotFoundException {
        this.scanPackage = scanPackage;
        this.foundClasses = findClassesInPackage();
    }

    public List<Class<?>> findByAnnotation(Class<? extends Annotation> annotationClass) {
        return foundClasses.stream()
                .filter(it -> ReflectionUtils.hasAnnotation(it, annotationClass))
                .toList();
    }

    public List<Tuple<String, Method>> findSourceFunctions(){
        return findByAnnotation(BeanSource.class).stream()
                .flatMap(source -> Arrays.stream(source.getMethods()))
                .filter(this::isComponentSourceMethod)
                .map(this::annotateMethod)
                .toList();
    }

    private Tuple<String, Method> annotateMethod(Method method) {
        Component annotation = method.getAnnotation(Component.class);
        String name = annotation.name();
        return new Tuple<>(name, method);
    }

    private boolean isComponentSourceMethod(Method method){
        return Modifier.isPublic(method.getModifiers())
                && Modifier.isStatic(method.getModifiers())
                && ReflectionUtils.hasAnnotation(method, Component.class);
    }

    // https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
    protected List<Class<?>> findClassesInPackage() throws IOException, URISyntaxException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String substitutedPath = scanPackage.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(substitutedPath);
        ArrayDeque<Path> paths = new ArrayDeque<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            paths.push(Path.of(resource.toURI()));
        }

        List<Class<?>> classes = new ArrayList<>();
        while(!paths.isEmpty()) {
            Path path = paths.pop();
            if (Files.isDirectory(path)) {
                try(Stream<Path> pathStream = Files.list(path)) {
                    pathStream.forEach(paths::push);
                }
            } else if (Files.exists(path) && path.toString().endsWith(".class")) {
                String fileName = path.getFileName().toString();
                String subDir = getSubPackage(path);
                if (!subDir.isEmpty()) {
                    subDir = subDir + ".";
                }
                classes.add(Class.forName(scanPackage + "." + subDir
                        + fileName.substring(0, fileName.length()-6)));
            }
        }
        return List.copyOf(classes);
    }

    private String getSubPackage(Path clazzPath){
        String subDir = clazzPath.getParent().toString();
        int packageEndIndex = subDir.lastIndexOf(scanPackage.replace(".", "/"))
                + scanPackage.length() + 1;
        if (packageEndIndex >= subDir.length()) {
            return "";
        }
        return subDir.substring(packageEndIndex).replace("/", ".");
    }
}
