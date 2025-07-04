import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomTestRunner {
    public static void main(String[] args) {
        CustomTestRunner customListTests = new CustomTestRunner();
        customListTests.runTests(CustomListTests.class, DemonstrationTests.class, PerformanceAndAnalysisTests.class);
    }

    private void runTests(Class<?>... testClasses) {
        Method beforeAll;
        Method beforeEach;
        Method afterAll;
        Method afterEach;
        int classesCounter = 0;
        int totalTests = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
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
                if (invokeMethod(beforeAll, testClassInstance) == -1) {
                    failed++;
                    break;
                }

                for (Method method : methods) {
                    if (!method.isAnnotationPresent(Disabled.class)) {
                        if (method.isAnnotationPresent(Test.class) && !method.isAnnotationPresent(ParameterizedTest.class)) {
                            totalTests++;
                            if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                failed++;
                                break;
                            }
                            int i = invokeMethod(method, testClassInstance) == 0 ? passed++ : failed++;
                            if (invokeMethod(afterEach, testClassInstance) == -1) {
                                failed++;
                            }
                        }
                        if (method.isAnnotationPresent(ParameterizedTest.class)) {
                            System.out.println("--- Parameterized test started ---");
                            if (method.isAnnotationPresent(ValueSource.class)) {
                                ValueSource valueSource = method.getAnnotation(ValueSource.class);
                                for (short value : valueSource.shorts()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (byte value : valueSource.bytes()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (int value : valueSource.ints()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (long value : valueSource.longs()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (float value : valueSource.floats()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (double value : valueSource.doubles()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (char value : valueSource.chars()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (boolean value : valueSource.booleans()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (String value : valueSource.strings()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                }
                                for (Class<?> value : valueSource.classes()) {
                                    totalTests++;
                                    if (invokeMethod(beforeEach, testClassInstance) == -1) {
                                        failed++;
                                        break;
                                    }
                                    System.out.print("With value: ");
                                    int i = invokeMethod(method, testClassInstance, value) == 0 ? passed++ : failed++;
                                    if (invokeMethod(afterEach, testClassInstance) == -1) {
                                        failed++;
                                    }
                                }
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
                if (invokeMethod(afterAll, testClassInstance) == -1) {
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

    private int invokeMethod(Method method, Object obj, Object... args) {
        if (method != null) {
            long time = System.currentTimeMillis();
            try {
                Object object = args == null ? method.invoke(obj) : method.invoke(obj, args);
                System.out.printf("✓ %s (%d ms)\n", method.getName(), (System.currentTimeMillis() - time));
                return 0;
            } catch (Exception e) {
                System.out.printf("✗ %s (%d ms)\n", method.getName(), (System.currentTimeMillis() - time));
                e.printStackTrace(System.out);
                return -1;
            }
        }
        return 1;
    }
}
