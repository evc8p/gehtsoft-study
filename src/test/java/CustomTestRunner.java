import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTestRunner {
    public static void main(String[] args) {
        CustomTestRunner customListTests = new CustomTestRunner();
        customListTests.runTests(CustomListTests.class, DemonstrationTests.class, PerformanceAndAnalysisTests.class);
    }

    private void runTests(Class<?>... testClasses) {
        Map<Annotation, Map<Method, Annotation[]>> allMethods = new HashMap<>();
        Method beforeAll = null;
        Method beforeEach = null;
        Method afterAll = null;
        Method afterEach = null;
        int classesCounter = 0;
        int totalTests = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long executionTime = System.currentTimeMillis();
        for (Class<?> testClass : testClasses) {
            allMethods = new HashMap<>();
            beforeAll = null;
            beforeEach = null;
            afterAll = null;
            afterEach = null;
            classesCounter++;
            System.out.printf("Run tests for %s\n", testClass.getSimpleName());
            try {
                Class[] classesWithAnnotations = new Class[]{
                        org.junit.jupiter.api.Test.class,
                        org.junit.jupiter.params.ParameterizedTest.class,
                        org.junit.jupiter.api.BeforeAll.class,
                        org.junit.jupiter.api.BeforeEach.class,
                        org.junit.jupiter.api.AfterEach.class,
                        org.junit.jupiter.api.AfterAll.class
                };
                Object testClassInstance = testClass.getDeclaredConstructor().newInstance();
                Method[] methods = testClassInstance.getClass().getDeclaredMethods();

                for (Method method : methods) {
                    for (Class classWithAnnotation : classesWithAnnotations) {
                        Annotation[] preciseAnnotation = method.getAnnotationsByType(classWithAnnotation);
                        if (preciseAnnotation.length > 0) {
                            // parse BeforeAll, BeforeEach, AfterAll, AfterEach first
                            String annotations = List.of(method.getAnnotations()).toString();
                            if (!annotations.contains("org.junit.jupiter.api.Disabled")) {
                                if (annotations.contains("org.junit.jupiter.api.BeforeAll")) {
                                    beforeAll = method;
                                    break;
                                }
                                if (annotations.contains("org.junit.jupiter.api.BeforeEach")) {
                                    beforeEach = method;
                                    break;
                                }
                                if (annotations.contains("org.junit.jupiter.api.AfterAll")) {
                                    afterAll = method;
                                    break;
                                }
                                if (annotations.contains("org.junit.jupiter.api.AfterEach")) {
                                    afterEach = method;
                                    break;
                                }
                            }

                            // parse the remaining methods
                            if (allMethods.containsKey(preciseAnnotation[0])) {
                                Map<Method, Annotation[]> map = allMethods.get(preciseAnnotation[0]);
                                map.put(method, method.getAnnotations());
                            } else {
                                Map<Method, Annotation[]> map = new HashMap<>();
                                map.put(method, method.getAnnotations());
                                allMethods.put(preciseAnnotation[0], map);
                            }
                            break;
                        }
                    }
                }

                // invoke the BeforeAll first
                if (beforeAll != null) {
                    try {
                        System.out.println("BeforeAll is executed");
                        beforeAll.invoke(testClassInstance);
                        System.out.println("BeforeAll is done");
                    } catch (Exception e) {
                        System.err.println("Error when invoke BeforeAll: " + e.getMessage());
                        e.printStackTrace(System.out);
                        break;
                    }
                }

                for (Annotation annotation : allMethods.keySet()) {
                    for (Method method : allMethods.get(annotation).keySet()) {
                        totalTests++;
                        String annotations = List.of(allMethods.get(annotation).get(method)).toString();
                        if (!annotations.contains("org.junit.jupiter.api.Disabled")) {
                            if (beforeEach != null) {
                                try {
                                    beforeEach.invoke(testClassInstance);
                                } catch (Exception e) {
                                    failed++;
                                    System.out.printf("✗ %s\n", method.getName());
                                    System.err.println("Error when invoke BeforeEach: " + e.getMessage());
                                    e.printStackTrace(System.out);
                                    break;
                                }
                            }
                            if (annotations.contains("org.junit.jupiter.api.Test")
                                    || annotations.contains("org.junit.jupiter.params.ParameterizedTest")) {
                                long time = System.currentTimeMillis();
                                try {
                                    if (beforeEach != null) {
                                        beforeEach.invoke(testClassInstance);
                                    }
                                    method.invoke(testClassInstance);
                                    passed++;
                                    if (afterEach != null) {
                                        afterEach.invoke(testClassInstance);
                                    }
                                    System.out.printf("✓ %s (%d ms)\n", method.getName(), (System.currentTimeMillis() - time));
                                } catch (Exception e) {
                                    failed++;
                                    System.out.printf("✗ %s (%d ms)\n", method.getName(), (System.currentTimeMillis() - time));
                                }
                            }
                            if (afterEach != null) {
                                try {
                                    afterEach.invoke(testClassInstance);
                                } catch (Exception e) {
                                    System.err.println("Error when invoke AfterEach: " + e.getMessage());
                                    e.printStackTrace(System.out);
                                    break;
                                }
                            }
                        } else {
                            skipped++;
                            System.out.println("- " + method.getName());
                        }
                    }
                }

                // invoke the AfterAll method last
                if (afterAll != null) {
                    try {
                        System.out.println("AfterAll is executed");
                        afterAll.invoke(testClassInstance);
                        System.out.println("AfterAll is done");
                    } catch (Exception e) {
                        System.err.println("Error when invoke AfterAll: " + e.getMessage());
                        e.printStackTrace(System.out);
                    }
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\n=== Custom Test Runner Results ===");
        System.out.println("Classes scanned\t" + classesCounter);
        System.out.println("PASSED tests\t" + passed);
        System.out.println("FAILED tests\t" + failed);
        System.out.println("SKIPPED tests\t" + skipped);
        System.out.println("Total tests\t\t" + totalTests);
        System.out.printf("Total execution time %d ms\n", (System.currentTimeMillis() - executionTime));
        System.out.printf("Success rate\t%.1f %%\n", (passed / (float) (totalTests - skipped) * 100.0F));
    }
}
