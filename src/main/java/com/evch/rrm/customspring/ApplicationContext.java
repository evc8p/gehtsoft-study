package com.evch.rrm.customspring;

import com.evch.rrm.customspring.annotation.*;
import com.evch.rrm.customspring.contract.ServerRequest;
import com.evch.rrm.customspring.model.RouteToController;
import com.evch.rrm.webserver.CustomWebServer;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationContext {
    private Map<Class<?>, Object> beans = new HashMap<>();
    RouteHandler routeHandler = new RouteHandler();
    private static final Pattern COMPILED_PATH_PATTERN = Pattern.compile("\\{([^/]*)}");

    public static void main(String[] args) throws Exception {
        ApplicationContext virtualServerContext = new ApplicationContext();
        CustomWebServer virtualServer = (CustomWebServer) virtualServerContext.scan("com.evch.rrm.webserver", null, new Object[]{8080, 100, true});

        ApplicationContext platformServerContext = new ApplicationContext();
        CustomWebServer platformServer = (CustomWebServer) platformServerContext.scan("com.evch.rrm.webserver", null, new Object[]{8081, 50, true});

        try {
            virtualServer.start();
            platformServer.start();

            System.out.println("Servers started:");
            System.out.println("Virtual thread server: http://localhost:8080");
            System.out.println("Platform thread server: http://localhost:8081");

            // Keep servers running
            Thread.sleep(300000); // Run for ... ms
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                virtualServer.stop();
                platformServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Servers stopped:");
        System.out.println("Virtual thread server: http://localhost:8080");
        System.out.println("Platform thread server: http://localhost:8081");
    }

    public Object scan(String packageName, Properties properties, Object... applicationParameters) throws Exception {
        List<Class<?>> classes = findClasses(packageName);
        Object applicationBean = null;
        int applicationAnnotationCounter = 0;

        for (Class<?> clazz : classes) {
            if (clazz.isAnonymousClass() || clazz.isMemberClass() || clazz.isLocalClass() ||
                    clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) ||
                    clazz.getDeclaredAnnotations().length == 0) {
                continue;
            }

            if (clazz.isAnnotationPresent(CustomApplication.class)) {
                if (applicationAnnotationCounter > 0) {
                    throw new IllegalArgumentException("Only one class in a package can be annotated with @CustomApplication");
                }
                Class<?>[] paramTypes = new Class<?>[applicationParameters.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    paramTypes[i] = applicationParameters[i].getClass();
                }
                applicationBean = clazz.getDeclaredConstructor(paramTypes).newInstance(applicationParameters);
                beans.put(clazz, applicationBean);
                applicationAnnotationCounter++;
                System.out.println("Main application class created: " + clazz.getSimpleName());
            }

            if (clazz.isAnnotationPresent(CustomRestController.class)
                    || clazz.isAnnotationPresent(CustomService.class)
                    || clazz.isAnnotationPresent(CustomRequestMapping.class)) {
                setBeansThroughConstructors(clazz, properties);
            }
        }
        injectDependenciesToBeans();
        if (Objects.isNull(applicationBean)) {
            throw new MissingResourceException("There is no class with the CustomApplication annotation", "CustomApplication", "");
        }
        return applicationBean;
    }

    private void setBeansThroughConstructors(Class<?> clazz, Properties properties) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = getConstructorForAutowiring(clazz);
        Object[] parameters = new Object[constructor.getParameterCount()];
        Class<?>[] paramTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = constructor.getParameters()[i];
            Class<?> paramType = parameter.getType();
            if (paramType.isPrimitive() || paramType == String.class) {
                if (parameter.isAnnotationPresent(CustomValue.class)) {
                    String rawValue = parameter.getAnnotation(CustomValue.class).value();
                    if ((Objects.isNull(rawValue) || rawValue.isEmpty()) && (properties == null || properties.getProperty(clazz.getName() + i) == null)) { // Example: com.evch.rrm.webserver.CustomUserController0 - first parameter in properties
                        throw new IllegalArgumentException("The CustomValue value is missing or is missing from the properties.\nThe parameter: " + paramType + " of the constructor: " + constructor.getName() + " in the " + clazz);
                    }
                    if (rawValue.isEmpty()) {
                        parameters[i] = Helper.convertType(properties.getProperty(clazz.getName() + i), paramType);
                    } else {
                        parameters[i] = Helper.convertType(rawValue, paramType);
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Cannot autowire primitive or String: " + paramType.getName() + " (parameter index: " + i + ") in the constructor of the class " + clazz.getName()
                    );
                }
            }

            paramTypes[i] = paramType;
            if (Objects.isNull(parameters[i])) {
                if (Objects.isNull(beans.get(paramType))) {
                    setBeansThroughConstructors(paramType, properties);
                }
                parameters[i] = beans.get(paramType);
            }
        }
        Object bean = clazz.getDeclaredConstructor(paramTypes).newInstance(parameters);
        beans.put(clazz, bean);
        System.out.println("Class created: " + clazz.getSimpleName());
    }

    private Constructor<?> getConstructorForAutowiring(Class<?> clazz) {
        Constructor<?> constructorWithMinimumNumberOfParameters = null;
        Constructor<?> resultConstructor = null;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        int minNumberOfParameters = constructors[0].getParameterCount();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(CustomAutowired.class)) {
                if (resultConstructor != null) {
                    throw new IllegalArgumentException("There cannot be more than one Autowired annotation for a constructor. Class " + clazz.getName());
                }
                resultConstructor = constructor;
            } else {
                if (constructor.getParameterCount() < minNumberOfParameters) {
                    minNumberOfParameters = constructor.getParameterCount();
                }
                constructorWithMinimumNumberOfParameters = constructor;
            }
        }
        return resultConstructor == null ? constructorWithMinimumNumberOfParameters : resultConstructor;
    }

    private void injectDependenciesToBeans() throws Exception {
        for (Object bean : beans.values()) {
            if (bean.getClass().isAnnotationPresent(CustomRequestMapping.class)) {
                String requestMappingPath = bean.getClass().getDeclaredAnnotation(CustomRequestMapping.class).value();
                for (Method method : bean.getClass().getDeclaredMethods()) {
                    if (!method.isSynthetic()) {
                        boolean hasCustomHttpMethodMappingAnnotation = false;
                        for (Annotation annotation : method.getDeclaredAnnotations()) {
                            CustomHttpMethodMapping meta = annotation.annotationType().getAnnotation(CustomHttpMethodMapping.class);
                            if (Objects.nonNull(meta)) {
                                hasCustomHttpMethodMappingAnnotation = true;
                                if (httpMethodMappingParamsHaveValidAnnotationsAndTypes(method)) {
                                    String[] paths = (String[]) annotation.annotationType().getMethod("value").invoke(annotation);
                                    for (String path : paths) {
                                        registerController(meta.httpMethod(), requestMappingPath + path, bean, method);
                                    }
                                }
                            }
                        }
                        if (!hasCustomHttpMethodMappingAnnotation) {
                            throw new IllegalArgumentException("The method must have annotations of CustomGetMapping, CustomPostMapping and etc.\n" +
                                    "Class: " + method.getDeclaringClass().getSimpleName() + ",\nMethod: " + method.getName() + ")");
                        }
                    }
                }
            }

            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(CustomAutowired.class)) {
                    int modifiers = field.getModifiers();
                    if ((modifiers & Modifier.FINAL) != 0) {
                        throw new IllegalArgumentException("The Autowired annotation cannot be applied to fields with the final access modifier");
                    }
                    if ((modifiers & Modifier.PRIVATE) != 0) {
                        if (RouteHandler.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            field.set(bean, routeHandler);
                            System.out.println("Dependency injected: controller methods in a field " + field.getName() + " of a class " + bean.getClass().getSimpleName());
                            continue;
                        }
                        Class<?> fieldType = field.getType();
                        Object dependency = beans.get(fieldType);
                        if (Objects.nonNull(dependency)) {
                            field.setAccessible(true);
                            field.set(bean, dependency);
                            System.out.println("Dependency injected: field " + field.getName() + " with type " + fieldType.getSimpleName() + " in a class " + bean.getClass().getSimpleName());
                        }
                    } else {
                        throw new IllegalArgumentException("The field " + field.getName() + " in " + bean.getClass().getSimpleName() + " should have private modifier");
                    }
                }
                if (field.isAnnotationPresent(CustomValue.class)) {
                    String rawValue = field.getAnnotation(CustomValue.class).value();
                    if (rawValue.isEmpty() || !(field.getType().isPrimitive() || field.getType() == String.class)) {
                        throw new IllegalArgumentException("The CustomValue value is missing or is not primitive type or type of String.\nThe field: " + field.getName() + " in the " + bean);
                    } else {
                        field.setAccessible(true);
                        field.set(bean, Helper.convertType(rawValue, field.getType())); // a String.class for now
                        System.out.println("Filed " + field.getName() + " = " + rawValue + " (class: " + bean + ")");
                    }
                }
            }
        }
    }

    private void registerController(String httpMethod, String path, Object bean, Method method) {
        if (path.isEmpty()) {
            path = "/";
        }

        List<String> regexpsForPathVariablesAnnotations = getRegexps(method, CustomPathVariable.class);

        Matcher m = COMPILED_PATH_PATTERN.matcher(path);
        StringBuilder regex = new StringBuilder();
        int last = 0;
        while (m.find()) {
            regex.append(Pattern.quote(path.substring(last, m.start())));
            regex.append("(").append(regexpsForPathVariablesAnnotations).append(")");
            last = m.end();
        }
        regex.append(Pattern.quote(path.substring(last)));

        Pattern parametersPattern = Pattern.compile(regex.toString());
        Type[] parameterTypes = method.getGenericParameterTypes();
        Map<String, Map<String, RouteToController>> routesToControllers = routeHandler.getRoutesToControllers();
        RouteToController prev = routesToControllers.computeIfAbsent(httpMethod, r -> new HashMap<>()).
                put(path, new RouteToController(bean, method, parametersPattern, parameterTypes));
        if (prev != null) {
            throw new IllegalStateException("Duplicate route: " + httpMethod + " " + path);
        }
        System.out.println(path + " is assigned with " + method.getName() + " method" + "(" + httpMethod + " " + parametersPattern + ")");
    }

    private List<String> getRegexps(Method method, Class<? extends Annotation> annotation) {
        Map<Class<?>, String> typeRegexps = Map.of(
                Byte.class, "-?\\d+",
                Short.class, "-?\\d+",
                Integer.class, "-?\\d+",
                Long.class, "-?\\d+",
                Float.class, "-?\\d+(?:\\.\\d+)?",
                Double.class, "-?\\d+(?:\\.\\d+)?",
                Boolean.class, "true|false",
                String.class, "[^/]+"
        );
        Map<Class<?>, Class<?>> wrappers = Map.of(
                byte.class, Byte.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                boolean.class, Boolean.class
        );
        List<String> result = new ArrayList<>();
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            if (params[i].isAnnotationPresent(annotation)) {
                Class<?> t = params[i].getType();
                if (t.isPrimitive()) {
                    result.add(typeRegexps.get(wrappers.get(t)));
                } else  {
                    result.add(typeRegexps.get(t));
                }
            }
        }
        return result;
    }

    private List<Class<?>> findClasses(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new Exception("Package not found: " + packageName);
        }
        File directory = new File(resource.toURI());
        if (directory.exists()) {
            findClassesInDirectory(directory, packageName, classes);
        }
        return classes;
    }

    private void findClassesInDirectory(File directory, String packageName, List<Class<?>> classes) throws Exception {
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName + "." + file.getName();
                findClassesInDirectory(file, subPackage, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                String fullClassName = packageName + "." + className;
                Class<?> clazz = Class.forName(fullClassName);
                classes.add(clazz);
            }
        }
    }

    private boolean httpMethodMappingParamsHaveValidAnnotationsAndTypes(Method method) {
        for (Parameter parameter : method.getParameters()) {
            boolean isServerRequest = ServerRequest.class.isAssignableFrom(parameter.getType());
            if (isServerRequest) {
                if (parameter.getDeclaredAnnotations().length > 0) {
                    throw new IllegalArgumentException("A parameter of type ServerRequest cannot have an annotation (class: " +
                            method.getDeclaringClass().getSimpleName() + ", method: " + method.getName() + ")");
                }
                continue;
            }

            Annotation[] annotations = parameter.getAnnotations();
            CustomHttpVariableMapping meta = annotations[0].annotationType().getAnnotation(CustomHttpVariableMapping.class);
            if (Objects.isNull(meta)) {
                throw new IllegalArgumentException("The method must have parameter of class 'ServerRequest', or annotations for path parameters, " +
                        "request parameters, the request body or a custom value.\nClass: " + method.getDeclaringClass().getSimpleName() + ",\nMethod: " +
                        method.getName() + ")");
            }
            if (annotations.length > 1) {
                throw new IllegalArgumentException("A parameter can have only one annotation.\n" +
                        "Class: " + method.getDeclaringClass().getSimpleName() + ".\nMethod: " + method.getName() + ").\nParameter: " + parameter.getName());
            }

            if (parameter.isAnnotationPresent(CustomPathVariable.class)) {
                if (parameter.getType() instanceof Class<?> t) {
                    boolean simple = t.isPrimitive()
                            || t == String.class
                            || t == Integer.class || t == Long.class || t == Short.class || t == Byte.class
                            || t == Double.class || t == Float.class || t == Boolean.class;
                    if (!simple) {
                        throw new IllegalArgumentException("A path variable must be of a primitive type (or the type of its wrapper class) or a String: " +
                                ".\nMethod: " + method.getName() + ").\nParameter: " + parameter.getName());
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported generic type.\nMethod: " + method.getName() + ").\nParameter: " + parameter.getName());
                }
            }
        }
        return true;
    }
}
