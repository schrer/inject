package at.schrer.inject.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
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

    public List<Class<?>> findByAnnotation(Class<? extends Annotation> annotationClass) throws IOException, URISyntaxException, ClassNotFoundException {
        List<Class<?>> classesInPackage = findClassesInPackage();
        return classesInPackage.stream().filter(it -> ReflectionUtils.hasAnnotation(it, annotationClass)).toList();
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
