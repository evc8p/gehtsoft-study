import com.evch.rrm.benchmarks.CustomStack;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomStackTests {
    @Test
    void customStackConstructorShouldBeAbleToPutElementsOfCollectionOnStack() {
        List<String> someCollection = List.of("One", "Two", "Three");
        CustomStack<String> customStack = new CustomStack<>(someCollection);
        assertEquals("Three", customStack.pop());
        assertEquals("Two", customStack.pop());
        assertEquals("One", customStack.pop());
    }

    @Test
    void pushMethodShouldAddElementToHeadOfStack() {
        CustomStack<String> customStack = new CustomStack<>();
        customStack.push("First element in the linked list");
        assertEquals("First element in the linked list", customStack.peek());
        customStack.push("Second element in the linked list");
        assertEquals("Second element in the linked list", customStack.peek());
        assertEquals(2, customStack.size());
    }

    @Test
    void popMethodShouldTakeAwayHeadElementOfStack() {
        CustomStack<String> customStack = new CustomStack<>();
        customStack.push("First element in the linked list");
        customStack.push("Second element in the linked list");
        assertEquals("Second element in the linked list", customStack.pop());
        assertEquals(1, customStack.size());
    }

    @Test
    void popMethodShouldThrowsEmptyStackExceptionForEmptyStack() {
        CustomStack<Integer> customStack = new CustomStack<>();
        assertThrows(EmptyStackException.class, customStack::pop);
    }

    @Test
    void peekMethodShouldReturnHeadElementOfStack() {
        CustomStack<String> customStack = new CustomStack<>();
        assertEquals("First element in the linked list", customStack.push("First element in the linked list"));
        customStack.push("Second element in the linked list");
        assertEquals("Second element in the linked list", customStack.peek());
        assertEquals(2, customStack.size());
    }

    @Test
    void peekMethodShouldThrowsEmptyStackExceptionForEmptyStack() {
        CustomStack<Integer> customStack = new CustomStack<>();
        assertThrows(EmptyStackException.class, customStack::peek);
    }

    @Test
    void emptyAndIsEmptyMethodsShouldReturn0ForEmptyStackAndFalseForNotEmpty() {
        CustomStack<String> customStack = new CustomStack<>();
        assertTrue(customStack.empty());
        assertTrue(customStack.isEmpty());
        customStack.push("First element in the linked list");
        assertFalse(customStack.empty());
        assertFalse(customStack.isEmpty());
    }

    @Test
    void searchMethodShouldReturnSequenceNumberOfFoundElementRelativeToStackHead() {
        CustomStack<String> customStack = new CustomStack<>();
        customStack.push("One");
        customStack.push(null);
        assertEquals(2, customStack.search("One"));
        assertEquals(1, customStack.search(null));
    }

    @Test
    void searchMethodShouldReturnMinus1ForEmptyStackOrMissingObject() {
        CustomStack<String> customStack = new CustomStack<>();
        assertEquals(-1, customStack.search("One"));
        assertEquals(-1, customStack.search(null));
        customStack.push("One");
        customStack.push("Two");
        assertEquals(-1, customStack.search("Three"));
    }
}
