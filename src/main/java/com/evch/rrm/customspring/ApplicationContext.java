package com.evch.rrm.customspring;

import com.evch.rrm.customspring.annotation.*;
import com.evch.rrm.webserver.CustomWebServer;
import com.evch.rrm.webserver.model.Controller;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

public class ApplicationContext {
    private Map<Class<?>, Object> beans = new HashMap<>();
    private final Map<String, Controller> controllers = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ApplicationContext virtualServerContext = new ApplicationContext();
        CustomWebServer virtualServer = (CustomWebServer) virtualServerContext.scan("com.evch.rrm.webserver", null, new Object[]{8080, 100, true});

//        ApplicationContext platformServerContext = new ApplicationContext();
//        CustomWebServer platformServer = (CustomWebServer) platformServerContext.scan("com.evch.rrm.webserver", null, new Object[]{8081, 50, true});

        try {
            virtualServer.start();
//            platformServer.start();

            System.out.println("Servers started:");
            System.out.println("Virtual thread server: http://localhost:8080");
//            System.out.println("Platform thread server: http://localhost:8081");

            // Keep servers running
            Thread.sleep(300000); // Run for ... ms
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                virtualServer.stop();
//                platformServer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Servers stopped:");
        System.out.println("Virtual thread server: http://localhost:8080");
//            System.out.println("Platform thread server: http://localhost:8081");
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

            if (clazz.isAnnotationPresent(CustomRestController.class) || clazz.isAnnotationPresent(CustomService.class)
                    || clazz.isAnnotationPresent(CustomRequestMapping.class)) {
                getBeansThroughConstructors(clazz, properties);
            }
        }
        injectDependenciesToBeans();
        if (Objects.isNull(applicationBean)) {
            throw new MissingResourceException("There is no class with the CustomApplication annotation", "CustomApplication", "");
        }
        return applicationBean;
    }

    private void getBeansThroughConstructors(Class<?> clazz, Properties properties) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
                        parameters[i] = convert(properties.getProperty(clazz.getName() + i), paramType);
                    } else {
                        parameters[i] = convert(rawValue, paramType);
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
                    getBeansThroughConstructors(paramType, properties);
                }
                parameters[i] = beans.get(paramType);
            }
        }
        Object bean = clazz.getDeclaredConstructor(paramTypes).newInstance(parameters);
        beans.put(clazz, bean);
        System.out.println("Class created: " + clazz.getSimpleName());
    }

    private Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == byte.class || targetType == Byte.class)
            return Byte.parseByte(value);
        if (targetType == short.class || targetType == Short.class)
            return Short.parseShort(value);
        if (targetType == int.class || targetType == Integer.class)
            return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class)
            return Long.parseLong(value);
        if (targetType == float.class || targetType == Float.class)
            return Float.parseFloat(value);
        if (targetType == double.class || targetType == Double.class)
            return Double.parseDouble(value);
        if (targetType == boolean.class || targetType == Boolean.class)
            return Boolean.parseBoolean(value);
        if (targetType.isEnum())
            return Enum.valueOf((Class<Enum>) targetType, value);
        throw new RuntimeException("Unsupported @CustomValue type: " + targetType);
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
                    List<String> routes = new ArrayList<>();
                    if (method.isAnnotationPresent(CustomGetMapping.class)) {
                        for (String path : method.getAnnotation(CustomGetMapping.class).value()) {
                            registerController("GET", requestMappingPath + path, bean, method);
                        }
                    }
                    if (method.isAnnotationPresent(CustomPostMapping.class)) {
                        for (String path : method.getAnnotation(CustomPostMapping.class).value()) {
                            registerController("POST", requestMappingPath + path, bean, method);
                        }
                    }
                    if (method.isAnnotationPresent(CustomPutMapping.class)) {
                        for (String path : method.getAnnotation(CustomPutMapping.class).value()) {
                            registerController("PUT", requestMappingPath + path, bean, method);
                        }
                    }
                    if (method.isAnnotationPresent(CustomPatchMapping.class)) {
                        for (String path : method.getAnnotation(CustomPatchMapping.class).value()) {
                            registerController("PATCH", requestMappingPath + path, bean, method);
                        }
                    }
                    if (method.isAnnotationPresent(CustomDeleteMapping.class)) {
                        for (String path : method.getAnnotation(CustomDeleteMapping.class).value()) {
                            registerController("DELETE", requestMappingPath + path, bean, method);
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
                        if (bean.getClass().isAnnotationPresent(CustomRestController.class)) {
                            if (field.getGenericType() instanceof ParameterizedType pt) {
                                Type rawType = pt.getRawType();
                                Type[] args = pt.getActualTypeArguments();

                                boolean isMapStringMethod = rawType == Map.class && args.length == 2 && args[0] == String.class && args[1] == Controller.class;
                                if (isMapStringMethod) {
                                    field.setAccessible(true);
                                    field.set(bean, controllers);
                                    System.out.println("Dependency injected: controller methods in a field " + field.getName() + " of a class " + bean.getClass().getSimpleName());
                                }
                            }
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
                    if (Objects.isNull(rawValue) || rawValue.isEmpty() || !(field.getType().isPrimitive() || field.getType() == String.class)) {
                        throw new IllegalArgumentException("The CustomValue value is missing or is not primitive type or type of String.\nThe field: " + field.getName() + " in the " + bean);
                    } else {
                        field.setAccessible(true);
                        field.set(bean, convert(rawValue, field.getType()));
                        System.out.println("Filed " + field.getName() + " = " + rawValue + " (class: " + bean + ")");
                    }
                }
            }
        }
    }

    private void registerController(String httpMethod, String route, Object bean, Method method) {
        if (route.isEmpty()) {
            route = "/";
        }
        String httpMethodPlusUri = httpMethod + " " + route;
        controllers.put(httpMethodPlusUri, new Controller(bean, method));
        System.out.println(httpMethodPlusUri + " is assigned with " + method.getName() + " method");
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
}
