import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class CustomTestRunner {
    int classesCounter = 0;
    int totalTests = 0;
    int passed = 0;
    int failed = 0;
    int skipped = 0;

    public static void main(String[] args) {
        CustomTestRunner customListTests = new CustomTestRunner();
        customListTests.runTests(CustomListTests.class, DemonstrationTests.class, PerformanceAndAnalysisTests.class);
    }

    private void runTests(Class<?>... testClasses) {
        Method beforeAll;
        Method beforeEach;
        Method afterAll;
        Method afterEach;
        long executionTime = System.currentTimeMillis();
        for (Class<?> testClass : testClasses) {
            beforeAll = null;
            beforeEach = null;
            afterAll = null;
            afterEach = null;
            System.out.printf("Run tests for %s\n", testClass.getSimpleName());
            try {
                Object testClassInstance = testClass.getDeclaredConstructor().newInstance();
                if (testClassInstance.getClass().isAnnotationPresent(Disabled.class)) {
                    System.out.printf("Test class %s is disabled and skipped.", testClass);
                    break;
                }
                classesCounter++;

                Method[] methods = testClassInstance.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(Disabled.class)) {
                        // parse BeforeAll, BeforeEach, AfterAll, AfterEach first
                        if (method.isAnnotationPresent(BeforeAll.class)) {
                            beforeAll = method;
                            continue;
                        }
                        if (method.isAnnotationPresent(BeforeEach.class)) {
                            beforeEach = method;
                            continue;
                        }
                        if (method.isAnnotationPresent(AfterAll.class)) {
                            afterAll = method;
                            continue;
                        }
                        if (method.isAnnotationPresent(AfterEach.class)) {
                            afterEach = method;
                        }
                    }
                }

                // invoke the BeforeAll first
                if (!invokeMethod(beforeAll, testClassInstance)) {
                    failed++;
                    break;
                }

                for (Method method : methods) {
                    if (!method.isAnnotationPresent(Disabled.class)) {
                        if (method.isAnnotationPresent(Test.class) && !method.isAnnotationPresent(ParameterizedTest.class)) {
                            totalTests++;
                            if (!invokeMethod(beforeEach, testClassInstance)) {
                                failed++;
                                break;
                            }
                            int i = invokeMethod(method, testClassInstance) ? passed++ : failed++;
                            if (!invokeMethod(afterEach, testClassInstance)) {
                                failed++;
                            }
                        }
                        if (method.isAnnotationPresent(ParameterizedTest.class)) {
                            System.out.println("--- Parameterized test started ---");
                            if (method.isAnnotationPresent(ValueSource.class)) {
                                ValueSource valueSource = method.getAnnotation(ValueSource.class);
                                processVolumeSource(valueSource, method, testClassInstance, beforeEach, afterEach);
                            }
                            System.out.println("--- Parameterized test completed ---");
                        }
                    } else {
                        totalTests++;
                        skipped++;
                        System.out.println("- " + method.getName());
                    }
                }

                // invoke the AfterAll method last
                if (!invokeMethod(afterAll, testClassInstance)) {
                    failed++;
                    break;
                }
            } catch (InstantiationException e) {
                e.printStackTrace(System.out);
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.out);
            } catch (InvocationTargetException e) {
                e.printStackTrace(System.out);
            } catch (NoSuchMethodException e) {
                e.printStackTrace(System.out);
            }
        }
        System.out.println("\n=== Custom Test Runner Results ===");
        System.out.println("Classes scanned\t" + classesCounter);
        System.out.println("PASSED tests\t" + passed);
        System.out.println("FAILED tests\t" + failed);
        System.out.println("SKIPPED tests\t" + skipped);
        System.out.println("Total tests\t\t" + totalTests);
        System.out.printf("Total execution time %d ms\n", (System.currentTimeMillis() - executionTime));
        System.out.printf("Success rate\t%.1f %%\n", (100.0f * (float) passed / (float) totalTests));
    }

    private boolean invokeMethod(Method method, Object obj, Object... args) {
        if (method != null) {
            long time = System.currentTimeMillis();
            try {
                Object object = args == null ? method.invoke(obj) : method.invoke(obj, args);
                System.out.printf("✓ %s (%d ms)\n", method.getName(), (System.currentTimeMillis() - time));
            } catch (Exception e) {
                System.out.printf("✗ %s (%d ms)\n", method.getName(), (System.currentTimeMillis() - time));
                e.printStackTrace(System.out);
                return false;
            }
        }
        return true;
    }

    private void processVolumeSource(ValueSource valueSource, Method method, Object testClassInstance, Method beforeEach, Method afterEach) throws InvocationTargetException, IllegalAccessException {
        Method[] valueSourceMethods = ValueSource.class.getDeclaredMethods();
        for (Method valueSourceMethod : valueSourceMethods) {
            Object values = null;
            values = valueSourceMethod.invoke(valueSource);
            if (values != null && values.getClass().isArray()) {
                int length = Array.getLength(values);
                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        Object value = Array.get(values, i);
                        totalTests++;
                        if (!invokeMethod(beforeEach, testClassInstance)) {
                            failed++;
                            break;
                        }
                        System.out.print("With value: ");
                        int s = invokeMethod(method, testClassInstance, value) ? passed++ : failed++;
                        if (!invokeMethod(afterEach, testClassInstance)) {
                            failed++;
                            break;
                        }
                    }
                }
            }
        }
    }
}
