import com.evch.rrm.fibonacci.FibonacciAlgorithms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerformanceAndAnalysisTests {
    @Test
    void allImplementationsOfFibonacciCalculationMethodShouldGiveSameResultsForInputs() {
        FibonacciAlgorithms fibonacciAlgorithms = new FibonacciAlgorithms();
        for (int i = 0; i < 35; i++) {
            assertEquals(fibonacciAlgorithms.fibonacciRecursive(i), fibonacciAlgorithms.fibonacciMemoized(i));
            assertEquals(fibonacciAlgorithms.fibonacciMemoized(i), fibonacciAlgorithms.fibonacciIterative(i));
        }
    }

    @Test
    void measuringExecutionTimeForInputsFor10_20_30_35() {
        FibonacciAlgorithms fibonacciAlgorithms = new FibonacciAlgorithms();
        int[] counters = new int[]{10, 20, 30, 35, 42, 350000, 700000, 1400000, 2800000, 5600000};
        for (int i = 0; i < counters.length; i++) {
            int iterator = i;
            System.out.println("-------------------------------------------------------------");
            if (counters[iterator] < 50) {
                System.out.printf("Fibonacci recursive for\t%d. ", counters[iterator]);
                fibonacciAlgorithms.runAndMeasureTime(() -> fibonacciAlgorithms.fibonacciRecursive(counters[iterator]));
            }
            System.out.printf("Fibonacci memoized for\t%d. ", counters[iterator]);
            fibonacciAlgorithms.runAndMeasureTime(() -> fibonacciAlgorithms.fibonacciMemoized(counters[iterator]));
            System.out.printf("Fibonacci iterative for\t%d. ", counters[iterator]);
            fibonacciAlgorithms.runAndMeasureTime(() -> fibonacciAlgorithms.fibonacciIterative(counters[iterator]));
        }
    }

    @Test
    void compareMemoryConsumption() {
        FibonacciAlgorithms fibonacciAlgorithms = new FibonacciAlgorithms();
        int[] counters = new int[]{10, 20, 30, 35, 42, 350000, 700000, 1400000, 2800000, 5600000};
        for (int i = 0; i < counters.length; i++) {
            int iterator = i;
            System.out.println("-------------------------------------------------------------");
            if (counters[iterator] < 50) {
                System.out.printf("Fibonacci recursive for\t%d. ", counters[iterator]);
                fibonacciAlgorithms.runAndCheckMemory(() -> fibonacciAlgorithms.fibonacciRecursive(counters[iterator]));
            }
            System.out.printf("Fibonacci memoized for\t%d. ", counters[iterator]);
            fibonacciAlgorithms.runAndCheckMemory(() -> fibonacciAlgorithms.fibonacciMemoized(counters[iterator]));
            System.out.printf("Fibonacci iterative for\t%d. ", counters[iterator]);
            fibonacciAlgorithms.runAndCheckMemory(() -> fibonacciAlgorithms.fibonacciIterative(counters[iterator]));
        }
    }
}
