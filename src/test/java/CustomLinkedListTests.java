import com.evch.rrm.CustomLinkedList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListTests {
    @Test
    void emptyCustomLinkedListShouldReturnSizeOf0() {
        CustomLinkedList<Double> customLinkedList = new CustomLinkedList<>();
        assertEquals(0, customLinkedList.size());
    }

    @Test
    void emptyCustomLinkedListShouldReturnTrueForIsEmpty() {
        CustomLinkedList<Float> customLinkedList = new CustomLinkedList<>();
        assertTrue(customLinkedList.isEmpty());
    }

    @Test
    void nonEmptyCustomLinkedListShouldReturnFalseForIsEmpty() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>();
        customLinkedList.add("First element in the linked list");
        assertFalse(customLinkedList.isEmpty());
    }

    @Test
    void customLinkedListShouldAddNewItemAndIncreaseSizeBy1() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>();
        customLinkedList.add("First element in the linked list");
        assertEquals(1, customLinkedList.size());
        customLinkedList.add("Second element in the linked list");
        assertEquals(2, customLinkedList.size());
    }

    @Test
    void addAllMethodShouldAddCollectionToList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = Arrays.stream(new Integer[]{1, 2, 3, 4, 5, null, 7, 8, 9, 10}).toList();
        customLinkedList.add(100);
        customLinkedList.addAll(integers);
        assertEquals(11, customLinkedList.size());
        assertEquals(100, customLinkedList.get(0));
        assertEquals(1, customLinkedList.get(1));
        assertEquals(10, customLinkedList.get(10));
    }

    @Test
    void clearMethodShouldReturnSizeEqualTo0() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertEquals(10, customLinkedList.size());
        customLinkedList.clear();
        assertEquals(0, customLinkedList.size());
    }

    @Test
    void indexOfMethodShouldReturn9For10thMatchingSearchItem() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertEquals(9, customLinkedList.indexOf(Integer.valueOf(10)));
    }

    @Test
    void indexOfMethodShouldReturnMinus1ForMissingElement() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertEquals(-1, customLinkedList.indexOf(Integer.valueOf(0)));
        assertEquals(-1, customLinkedList.indexOf(Integer.valueOf(11)));
    }

    @Test
    void getMethodOf0ShouldThrowsIndexOutOfBoundsExceptionForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.get(0));
    }

    @Test
    void getMethodShouldGenerateErrorIndexIsOutOfRangeForIndexOutsideListSizeLimit() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.get(10));
    }

    @Test
    void getMethodShouldReturnValuesOutermostIndexesOfList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertEquals(1, customLinkedList.get(0));
        assertEquals(10, customLinkedList.get(9));
    }

    @Test
    void setMethodShouldGenerateErrorIndexIsOutOfRangeForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.set(0, null));
    }

    @Test
    void setMethodShouldGenerateErrorIndexIsOutOfRangeForIndexOutsideListSizeLimit() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.set(-1, null));
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.set(10, null));
    }

    @Test
    void setMethodShouldReplaceValuesOfOutermostIndexesOfList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertEquals(1, customLinkedList.set(0, 10));
        assertEquals(10, customLinkedList.set(9, 0));
        assertEquals(10, customLinkedList.get(0));
        assertEquals(0, customLinkedList.get(9));
    }

    @Test
    void addMethodShouldInsertFirstMiddleAndLastElementsByTheirIndexes() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        customLinkedList.addAll(integers);
        customLinkedList.add(0, null);
        assertEquals(10, customLinkedList.size());
        customLinkedList.add(10, null);
        assertEquals(11, customLinkedList.size());
        customLinkedList.add(4, null);
        assertEquals(12, customLinkedList.size());
        assertNull(customLinkedList.get(0));
        assertEquals(1, customLinkedList.get(1));
        assertEquals(4, customLinkedList.get(5));
        assertNull(customLinkedList.get(11));
    }

    @Test
    void addMethodShouldCreateNewItemInEmptyListAtIndex0() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        customLinkedList.add(0, 10);
        assertEquals(1, customLinkedList.size());
        assertEquals(10, customLinkedList.get(0));
    }

    @Test
    void addAllMethodShouldAddCollectionToListAtIndexPosition() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        customLinkedList.addAll(10, integers);
        assertEquals(20, customLinkedList.size());
        assertEquals(1, customLinkedList.get(0));
        assertEquals(1, customLinkedList.get(10));
    }

    @Test
    void addAllMethodShouldGenerateErrorIndexIsOutOfRangeForIndexOutsideListSizeLimit() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        customLinkedList.addAll(integers);
        assertThrows(IndexOutOfBoundsException.class, () -> customLinkedList.addAll(11, integers));
    }

    @Test
    void removeMethodForObjectShouldRemoveFirstOccurrenceOfObject() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 2);
        customLinkedList.addAll(integers);
        customLinkedList.remove(Integer.valueOf(2));
        assertEquals(2, customLinkedList.size());
        assertEquals(1, customLinkedList.get(0));
        assertEquals(2, customLinkedList.get(1));
    }

    @Test
    void removeMethodForFirstObjectShouldRemoveIt() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2);
        customLinkedList.addAll(integers);
        customLinkedList.remove(customLinkedList.getFirst());
        assertEquals(1, customLinkedList.size());
        assertEquals(2, customLinkedList.get(0));
        assertEquals(2, customLinkedList.getFirst());
        assertEquals(2, customLinkedList.getLast());
    }

    @Test
    void removeMethodForLastObjectShouldReturnListWithoutIt() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2);
        customLinkedList.addAll(integers);
        customLinkedList.remove(customLinkedList.getLast());
        assertEquals(1, customLinkedList.size());
        assertEquals(1, customLinkedList.get(0));
        assertEquals(1, customLinkedList.getFirst());
        assertEquals(1, customLinkedList.getLast());
    }

    @Test
    void removeFirstMethodShouldBeRemovedAndReturnOldValue() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2);
        customLinkedList.addAll(integers);
        assertEquals(1, customLinkedList.removeFirst());
        assertEquals(1, customLinkedList.size());
        assertEquals(2, customLinkedList.get(0));
        assertEquals(2, customLinkedList.getFirst());
        assertEquals(2, customLinkedList.getLast());
    }

    @Test
    void removeLastMethodShouldBeRemovedAndReturnOldValue() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2);
        customLinkedList.addAll(integers);
        assertEquals(2, customLinkedList.removeLast());
        assertEquals(1, customLinkedList.size());
        assertEquals(1, customLinkedList.get(0));
        assertEquals(1, customLinkedList.getFirst());
        assertEquals(1, customLinkedList.getLast());
    }

    @Test
    void removeFirstMethodShouldThrowsNoSuchElementExceptionForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertThrows(NoSuchElementException.class, customLinkedList::removeLast);
    }

    @Test
    void removeFirstMethodShouldRemoveHeadElement() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(List.of("one", "two"));
        assertEquals("one", customLinkedList.removeFirst());
        assertEquals("two", customLinkedList.getFirst());
    }

    @Test
    void removeLastMethodShouldThrowsNoSuchElementExceptionForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertThrows(NoSuchElementException.class, customLinkedList::removeLast, "The list is empty.");
    }

    @Test
    void removeLastMethodShouldRemoveTailElement() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(List.of("one", "two"));
        assertEquals("two", customLinkedList.removeLast());
        assertEquals("one", customLinkedList.getLast());
    }

    @Test
    void removeMethodByIndexShouldReturnListWithoutFirstOccurrenceOfObject() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2, 2);
        customLinkedList.addAll(integers);
        customLinkedList.remove(1);
        assertEquals(2, customLinkedList.size());
        assertEquals(1, customLinkedList.get(0));
        assertEquals(2, customLinkedList.get(1));
    }

    @Test
    void removeMethodByIndexOfFirstObjectShouldReturnListWithoutIt() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2);
        customLinkedList.addAll(integers);
        customLinkedList.remove(0);
        assertEquals(1, customLinkedList.size());
        assertEquals(2, customLinkedList.get(0));
        assertEquals(2, customLinkedList.getFirst());
        assertEquals(2, customLinkedList.getLast());

    }

    @Test
    void removeMethodByIndexOfLastObjectShouldReturnListWithoutIt() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        List<Integer> integers = List.of(1, 2);
        customLinkedList.addAll(integers);
        customLinkedList.remove(1);
        assertEquals(1, customLinkedList.size());
        assertEquals(1, customLinkedList.get(0));
        assertEquals(1, customLinkedList.getFirst());
        assertEquals(1, customLinkedList.getLast());
    }

    @Test
    void lastIndexOfMethodShouldReturnMinus1IfObjectIsMissingFromListOrInEmptyList() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>();
        assertEquals(-1, customLinkedList.lastIndexOf(Integer.valueOf(1)));
        List<String> strings = List.of("one", "two", "three", "four", "five", "one", "six", "seven");
        customLinkedList.addAll(strings);
        assertEquals(-1, customLinkedList.lastIndexOf("ten"));
    }

    @Test
    void lastIndexOfMethodShouldReturn5ForObjectWithValueOne() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>();
        List<String> strings = List.of("one", "two", "three", "four", "five", "one", "six", "seven");
        customLinkedList.addAll(strings);
        assertEquals(5, customLinkedList.lastIndexOf("one"));
    }

    @Test
    void containsMethodShouldReturnTrueForObjectPresentInList() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        assertTrue(customLinkedList.contains("seven"));
    }

    @Test
    void containsMethodShouldReturnFalseForObjectMissingInList() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        assertFalse(customLinkedList.contains("ten"));
    }

    @Test
    void containsAllMethodShouldReturnTrueForListWithElementsPresent() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        List<String> customLinkedList1 = List.of("one", "two", "three", "four", "five", "one", "six");
        assertTrue(customLinkedList.containsAll(customLinkedList1));
    }

    @Test
    void containsAllMethodShouldReturnFalseForListWithMissingElements() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "one", "six", "seven")
        );
        List<String> customLinkedList1 = List.of("one", "TWO", "three", "four", "five", "one", "six");
        assertFalse(customLinkedList.containsAll(customLinkedList1));
    }

    @Test
    void removeAllMethodShouldRemoveAllItemsFromListThatAreInCollection() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
        );
        List<String> customLinkedList1 = List.of("two", "nine", "eleven");
        customLinkedList.removeAll(customLinkedList1);
        assertEquals(8, customLinkedList.size());
        assertEquals(-1, customLinkedList.indexOf("two"));
        assertEquals(-1, customLinkedList.indexOf("nine"));
    }

    @Test
    void retainAllMethodShouldOnlyRetainItemsFromListThatAreInCollection() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "six", "nine", "seven", "eight", "nine", "ten", "one")
        );
        List<String> customLinkedList1 = List.of("two", "nine", "eleven");
        customLinkedList.retainAll(customLinkedList1);
        assertEquals(3, customLinkedList.size());
        assertEquals(0, customLinkedList.indexOf("two"));
        assertEquals(1, customLinkedList.indexOf("nine"));
        assertEquals(2, customLinkedList.lastIndexOf("nine"));
    }

    @Test
    void offerMethodShouldAddShouldAddElementToTailOfQueue() {
        CustomLinkedList<String> customLinkedList = new CustomLinkedList<>(
                List.of("one", "two", "three", "four", "five", "six", "seven")
        );
        customLinkedList.offer("eight");
        assertEquals(8, customLinkedList.size());
    }

    @Test
    void pollMethodShouldReturnNullForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        assertNull(customLinkedList.poll());
    }

    @Test
    void reversedMethodShouldReturnNullForEmptyList() {
        CustomLinkedList<Integer> customLinkedList = new CustomLinkedList<>();
        for (int i = 0; i < 100; i++) {
            customLinkedList.add(i);
        }
        CustomLinkedList<Integer> customLinkedList1 = customLinkedList.reversed();
        assertEquals(100, customLinkedList1.size());
        assertEquals(99, customLinkedList1.getFirst());
        assertEquals(0, customLinkedList1.getLast());
    }
}
