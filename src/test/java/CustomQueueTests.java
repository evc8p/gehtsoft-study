import com.evch.rrm.CustomLinkedList;
import com.evch.rrm.CustomQueue;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomQueueTests {
    @Test
    void emptyCustomQueueShouldReturnSizeOf0() {
        CustomQueue<Double> customQueue = new CustomQueue<>();
        assertEquals(0, customQueue.size());
    }

    @Test
    void emptyCustomLinkedListShouldReturnTrueForIsEmpty() {
        CustomQueue<Float> customQueue = new CustomQueue<>();
        assertTrue(customQueue.isEmpty());
    }

    @Test
    void containsMethodShouldReturnTrueForObjectPresentInList() {
        CustomQueue<String> customQueue = new CustomQueue<>(List.of("one", "two", "three", "four", "five", "one", "six", "seven"));
        assertTrue(customQueue.contains("one"));
        assertTrue(customQueue.contains("seven"));
    }

    @Test
    void containsMethodShouldReturnFalseForObjectMissingInList() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        assertFalse(customQueue.contains("ten"));
    }

    @Test
    void addMethodShouldAddShouldAddElementToTailOfQueue() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "six", "seven")
        );
        customQueue.add("eight");
        assertEquals(8, customQueue.size());
    }

    @Test
    void removeMethodForObjectShouldRemoveFirstOccurrenceOfObject() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        List<Integer> integers = List.of(1, 2, 3, 2);
        customQueue.addAll(integers);
        customQueue.remove(Integer.valueOf(2));
        assertEquals(3, customQueue.size());
        assertEquals(1, customQueue.poll());
        assertEquals(3, customQueue.poll());
        assertEquals(2, customQueue.poll());
    }

    @Test
    void removeObjectMethodShouldRemoveHeadOfList() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        List<Integer> integers = List.of(1, 2);
        customQueue.addAll(integers);
        boolean result = customQueue.remove(Integer.valueOf(1));
        assertTrue(result);
        assertEquals(1, customQueue.size());
        assertEquals(2, customQueue.poll());
        assertEquals(0, customQueue.size());
    }

    @Test
    void containsAllMethodShouldReturnTrueForListWithElementsPresent() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        List<String> customLinkedList1 = List.of("one", "two", "three", "four", "five", "one", "six");
        assertTrue(customQueue.containsAll(customLinkedList1));
    }

    @Test
    void containsAllMethodShouldReturnFalseForListWithMissingElements() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        List<String> customLinkedList1 = List.of("one", "TWO", "three", "four", "five", "one", "six");
        assertFalse(customQueue.containsAll(customLinkedList1));
    }

    @Test
    void addAllMethodShouldAddCollectionToList() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        List<Integer> integers = Arrays.stream(new Integer[]{1, 2, 3, 4, 5, null, 7, 8, 9, 10}).toList();
        customQueue.add(100);
        customQueue.addAll(integers);
        assertEquals(11, customQueue.size());
        assertEquals(100, customQueue.poll());
        assertEquals(1, customQueue.poll());
        assertEquals(2, customQueue.poll());
    }

    @Test
    void removeAllMethodShouldRemoveAllItemsFromListThatAreInCollection() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
        );
        List<String> customLinkedList1 = List.of("two", "nine", "eleven");
        customQueue.removeAll(customLinkedList1);
        assertEquals(8, customQueue.size());
        assertEquals("one", customQueue.poll());
        assertEquals("three", customQueue.poll());
        assertEquals("four", customQueue.poll());
        assertEquals("five", customQueue.poll());
        assertEquals("six", customQueue.poll());
        assertEquals("seven", customQueue.poll());
        assertEquals("eight", customQueue.poll());
        assertEquals("ten", customQueue.poll());
        assertEquals(0, customQueue.size());
    }

    @Test
    void retainAllMethodShouldOnlyRetainItemsFromListThatAreInCollection() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "six", "nine", "seven", "eight", "nine", "ten", "one")
        );
        List<String> customLinkedList1 = List.of("two", "nine", "eleven");
        customQueue.retainAll(customLinkedList1);
        assertEquals(3, customQueue.size());
        assertEquals("two", customQueue.poll());
        assertEquals("nine", customQueue.poll());
        assertEquals("nine", customQueue.poll());
        assertEquals(0, customQueue.size());
    }

    @Test
    void clearMethodShouldReturnSizeEqualTo0() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customQueue.addAll(integers);
        assertEquals(10, customQueue.size());
        customQueue.clear();
        assertEquals(0, customQueue.size());
    }

    @Test
    void offerMethodShouldAddShouldAddElementToTailOfQueue() {
        CustomQueue<String> customQueue = new CustomQueue<>(
                List.of("one", "two", "three", "four", "five", "six", "seven")
        );
        customQueue.offer("eight");
        assertEquals(8, customQueue.size());
    }

    @Test
    void removeMethodShouldRemoveHeadElementAndReturnItsOldValue() {
        CustomQueue<Integer> customQueue = new CustomQueue<>(List.of(1, 2));
        assertEquals(1, customQueue.remove());
        assertEquals(1, customQueue.size());
        assertEquals(2, customQueue.poll());
    }

    @Test
    void removeMethodShouldThrowsNoSuchElementExceptionForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertThrows(NoSuchElementException.class, () -> customLinkedList.remove());
    }

    @Test
    void pollMethodShouldRemoveHeadElementAndReturnItsOldValue() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        List<Integer> integers = List.of(1, 2);
        customQueue.addAll(integers);
        assertEquals(1, customQueue.poll());
        assertEquals(1, customQueue.size());
        assertEquals(2, customQueue.poll());
    }

    @Test
    void pollMethodShouldReturnNullForEmptyList() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        assertNull(customQueue.poll());
    }

    @Test
    void elementMethodShouldReturnHeadElement() {
        CustomQueue<Integer> customQueue = new CustomQueue<>(List.of(1, 2));
        assertEquals(1, customQueue.element());
        assertEquals(2, customQueue.size());
    }

    @Test
    void elementMethodShouldThrowsNoSuchElementExceptionForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertThrows(NoSuchElementException.class, () -> customLinkedList.element());
    }

    @Test
    void peekMethodShouldReturnHeadElement() {
        CustomQueue<Integer> customQueue = new CustomQueue<>(List.of(1, 2));
        assertEquals(1, customQueue.peek());
        assertEquals(2, customQueue.size());
    }

    @Test
    void peekMethodShouldReturnNullForEmptyList() {
        CustomQueue<Integer> customQueue = new CustomQueue<>();
        assertNull(customQueue.peek());
    }
}
