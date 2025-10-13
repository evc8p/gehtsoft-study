import com.evch.rrm.CustomList;
import com.evch.rrm.decorators.ReentrantReadWriteLockListDecorator;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ReentrantReadWriteLockCustomListTests {
    @Test
    void customListConstructorShouldCreateListWithDefaultValues() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(new CustomList<>());
        assertTrue(list.isEmpty());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacity() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(1));
        assertEquals(0, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityOf0() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(0));
        assertEquals(0, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReentrantReadWriteLockListDecorator<>(new CustomList<Integer>(-1)),
                "Initial capacity is less than 0");
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityAndMultiplier() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(5, 2.0f));
        assertTrue(list.isEmpty());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityAndMultiplierNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReentrantReadWriteLockListDecorator<>(new CustomList<>(-1, 1.0f)),
                "Initial capacity is less than 0 or multiplier < 1");

        assertThrows(IllegalArgumentException.class,
                () -> new ReentrantReadWriteLockListDecorator<>(new CustomList<>(0, 0.9f)),
                "Initial capacity is less than 0 or multiplier < 1");
    }

    @Test
    void customListConstructorShouldCreateListWithElementsWithSizeOf5() {
        ReentrantReadWriteLockListDecorator<String> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"null", "5", "1", " 9", "7"}));
        assertEquals(5, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithElementsWithSizeOf11() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{null, 5, 1, 9, 7, 8, 20, null, null, 10, 11}));
        assertEquals(11, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithElementsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReentrantReadWriteLockListDecorator<>(new CustomList<>(null)),
                "Array is null");
    }

    @Test
    void createdListOf10ElementsShouldHaveSizeEqualTo10() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{null, 5, 1, 9, 7, 8, 20, null, null, 10}));
        assertEquals(10, list.size());
    }

    @Test
    void adding20ItemShouldIncreaseSizeOfListBy20() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[new Random().nextInt(7)]));
        int oldSize = list.size();
        for (int i = 0; i < 20; i++) {
            list.add(1);
        }
        assertEquals(oldSize + 20, list.size());

        oldSize = list.size();
        for (int i = 0; i < 50; i++) {
            list.add(0, 50);
        }
        assertEquals(oldSize + 50, list.size());
    }

    @Test
    void elementAddedByIndexShouldBeFoundInItsIndex() {
        int size = new Random().nextInt(8) + 8;
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(new Integer[size]));
        list.add(7, 50);
        assertEquals(50, (int) list.get(7));
    }

    @Test
    void elementSetByIndexShouldBeFoundInItsIndex() {
        int size = new Random().nextInt(8) + 8;
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(new Integer[size]));
        list.set(7, 50);
        assertEquals(50, (int) list.get(7));
    }

    @Test
    void elementRemovedByIndexShouldBeNotFoundInListAndDecreaseListSize() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10}));
        list.remove(0);
        list.remove(7);
        assertEquals(8, list.size());
        assertEquals(-1, list.indexOf(1));
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void elementRemovedByValueNullShouldBeNotFoundInListAndDecreaseListSize() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10}));
        list.remove(null);
        assertEquals(9, list.size());
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void clearMethodShouldClearEntireList() {
        ReentrantReadWriteLockListDecorator<Integer> list = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10}));
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfAddMethod() {
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        List<Integer> arrayList = new ArrayList<>(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        customList.add(11);
        arrayList.add(11);
        assertEquals(11, customList.get(10));
        assertEquals(11, arrayList.get(10));
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfGetMethod() {
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        List<Integer> arrayList = new ArrayList<>(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        assertEquals(3, customList.get(2));
        assertEquals(3, arrayList.get(2));
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfSetMethod() {
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        List<Integer> arrayList = new ArrayList<>(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        customList.set(0, 0);
        arrayList.set(0, 0);
        assertEquals(0, customList.get(0));
        assertEquals(10, customList.size());
        assertEquals(0, arrayList.get(0));
        assertEquals(10, arrayList.size());
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfRemoveMethod() {
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        List<Integer> arrayList = new ArrayList<>(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        customList.remove(9);
        arrayList.remove(9);
        assertEquals(-1, customList.indexOf(10));
        assertEquals(9, customList.size());
        assertEquals(-1, arrayList.indexOf(10));
        assertEquals(9, arrayList.size());
    }

    @Test
    void customListShouldAcceptTypeInteger() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertDoesNotThrow(() -> new CustomList<>(integers));
        assertDoesNotThrow(() -> new CustomList<Integer>());
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(20, 2.0f));
        customList.add(0, 5);
        customList.add(0, 10);
        customList.add(0, 15);
        customList.set(0, 20);
        customList.remove(1);
        assertEquals(5, customList.get(1));
    }

    @Test
    void customListShouldReturnTrueIfListDoesNotContainElement() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(integers));
        assertTrue(customList.contains(5));
    }

    @Test
    void customListShouldReturnFalseIfListDoesNotContainElement() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(integers));
        assertFalse(customList.contains(11));
    }

    @Test
    void customListShouldReturnTrueIfListContainsAnotherCollectionElements() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<Integer> collection = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(integers));
        assertTrue(customList.containsAll(collection));
    }

    @Test
    void customListShouldReturnFalseIfListDoesNotContainAllCollectionElements() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<Integer> collection = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(integers));
        assertFalse(customList.containsAll(collection));
    }

    @Test
    void customListShouldAddItemsFromAnotherCollection() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<Integer> collection = List.of(11, 12, 13, 14, 15);
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(integers));
        assertTrue(customList.addAll(collection));
        assertTrue(customList.containsAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
        assertEquals(15, customList.size());
    }

    @Test
    void customListShouldInsertItemsFromAnotherCollectionIntoSpecificIndex() {
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        List<Integer> collection = List.of(11, 12, 13, 14, 15);
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>(integers));
        assertTrue(customList.addAll(9, collection));
        assertEquals(15, customList.size());
        assertTrue(customList.containsAll(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
        assertEquals(9, customList.get(8));
        assertEquals(11, customList.get(9));
        assertEquals(10, customList.get(14));
    }

    @Test
    void customListShouldReturnLastIndexOfItemInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "1", " 1", "1", "5"}));
        assertEquals(4, customList.lastIndexOf("5"));
        assertEquals(3, customList.lastIndexOf("1"));
        assertEquals(-1, customList.lastIndexOf("7"));
    }

    @Test
    void customListShouldReturnSublist() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "2", " 3", "4", "5"}));
        ReentrantReadWriteLockListDecorator<String> subList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList(customList.subList(1, 2).toArray()));
        assertEquals(1, subList.size());
        assertEquals("2", subList.get(0));
    }

    @Test
    void customListIteratorShouldReturnAllValuesWithNextMethod() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "2", " 3", "4", "5"}));
        StringBuilder sb = new StringBuilder();
        ListIterator<String> it = customList.listIterator();
        while (it.hasNext()) {
            sb.append(it.next());
        }
        assertEquals("12 345", sb.toString());
        assertEquals(5, it.nextIndex());
    }

    @Test
    void customListIteratorShouldReturnAllValuesWithPreviousMethod() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "2", " 3", "4", "5"}));
        StringBuilder sb = new StringBuilder();
        ListIterator<String> it = customList.listIterator(5);
        while (it.hasPrevious()) {
            sb.append(it.previous());
        }
        assertEquals("54 321", sb.toString());
        assertEquals(-1, it.previousIndex());
    }

    //To Do more tests for the iterator

    @Test
    void comparingCustomListToNullShouldReturnFalse() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "5"}));
        ReentrantReadWriteLockListDecorator<String> customList1 = null;
        assertFalse(customList.equals(customList1));
    }

    @Test
    void twoIdenticalCustomListsShouldBeEqualInEquals() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "2", "null", "4", "5"}));
        ReentrantReadWriteLockListDecorator<String> customList1 = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "2", "null", "4", "5"}));
        List<String> customList2 = List.of("1", "2", "null", "4", "5");
        assertTrue(customList.equals(customList));
        assertTrue(customList.equals(customList1));
        assertTrue(customList.equals(customList2));
    }

    @Test
    void twoDifferentCustomListsShouldBeNotEqualInEquals() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "5"}));
        ReentrantReadWriteLockListDecorator<String> customList1 = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "4", "3", "null", "5"}));
        assertFalse(customList.equals(customList1));
        assertFalse(customList1.equals(customList));
    }

    @Test
    void twoDifferentCustomListsSizeShouldBeNotEqualInEquals() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "5"}));
        ReentrantReadWriteLockListDecorator<String> customList1 = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "5", ""}));
        assertFalse(customList.equals(customList1));
        assertFalse(customList1.equals(customList));
    }

    @Test
    void comparingCustomListWithMapObjectShouldReturnFalse() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "5"}));
        Map<String, String> map = Map.of("1", "null", "3", "4", "5", "");
        assertFalse(customList.equals(map));
    }

    @Test
    void retainAllMethodShouldRemoveAllElementsOfList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "5"}));
        List<String> list = List.of("one");
        boolean result = customList.retainAll(list);
        assertTrue(result);
        assertTrue(customList.isEmpty());
    }

    @Test
    void retainAllMethodShouldKeepTwoItemsInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        List<String> list = List.of("1");
        boolean result = customList.retainAll(list);
        assertTrue(result);
        assertEquals(2, customList.size());
        assertEquals("1", customList.get(0));
        assertEquals("1", customList.get(1));
    }

    @Test
    void retainAllMethodShouldKeepAllItemsInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        List<String> list = List.of("1", "null", "3", "4");
        boolean result = customList.retainAll(list);
        assertFalse(result);
        assertEquals(5, customList.size());
        assertEquals("1", customList.get(0));
        assertEquals("null", customList.get(1));
        assertEquals("3", customList.get(2));
        assertEquals("4", customList.get(3));
        assertEquals("1", customList.get(4));
    }

    @Test
    void removeAllMethodShouldRemoveAllElementsOfList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"one", "one", "one", "one", "one"}));
        List<String> list = List.of("one");
        boolean result = customList.removeAll(list);
        assertTrue(result);
        assertTrue(customList.isEmpty());
    }

    @Test
    void removeAllMethodShouldKeepTwoItemsInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        List<String> list = List.of("null", "1");
        boolean result = customList.removeAll(list);
        assertTrue(result);
        assertEquals(2, customList.size());
        assertEquals("3", customList.get(0));
        assertEquals("4", customList.get(1));
    }

    @Test
    void removeAllMethodShouldKeepAllItemsInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        List<String> list = List.of("7", "nulls", "30", "40");
        boolean result = customList.removeAll(list);
        assertFalse(result);
        assertEquals(5, customList.size());
        assertEquals("1", customList.get(0));
        assertEquals("null", customList.get(1));
        assertEquals("3", customList.get(2));
        assertEquals("4", customList.get(3));
        assertEquals("1", customList.get(4));
    }

    @Test
    void toArrayMethodShouldReturnNewArrayWithAllElementsInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        Object[] newArray = customList.toArray();
        assertEquals("1", newArray[0]);
        assertEquals("null", newArray[1]);
        assertEquals("3", newArray[2]);
    }

    @Test
    void toArrayObjectsMethodShouldReturnNewArrayWithAllElementsInList() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        Object[] newArray = customList.toArray(new String[0]);
        assertEquals("1", newArray[0]);
        assertEquals("null", newArray[1]);
        assertEquals("3", newArray[2]);
    }

    @Test
    void toArrayObjectsMethodShouldReturnNewArrayWithAllElementsInListAndNullsInTail() {
        ReentrantReadWriteLockListDecorator<String> customList = new ReentrantReadWriteLockListDecorator<>(
                new CustomList<>(new String[]{"1", "null", "3", "4", "1"}));
        Object[] newArray = customList.toArray(new String[10]);
        assertEquals("1", newArray[0]);
        assertEquals("null", newArray[1]);
        assertEquals("3", newArray[2]);
        assertNull(newArray[5]);
        assertNull(newArray[6]);
        assertNull(newArray[7]);
        assertNull(newArray[8]);
        assertNull(newArray[9]);
    }

    @RepeatedTest(100)
    void addMethodShouldAddMillionElementsWhenTheyAreAddedByTwoThreadsOf500_000Each() throws InterruptedException {
        ReentrantReadWriteLockListDecorator<Integer> customList = new ReentrantReadWriteLockListDecorator<>(new CustomList<>());
        Thread thread1 = Thread.startVirtualThread(addElementTask(customList, 500_000));
        Thread thread2 = Thread.startVirtualThread(addElementTask(customList, 500_000));
        thread1.join();
        thread2.join();
        assertEquals(1_000_000, customList.size());
    }

    private Runnable addElementTask(List<Integer> list, int quantity) {
        return new Runnable() {
            @Override
            public void run() {
                IntStream.range(0, quantity).parallel().forEach(n -> list.add(1));
            }
        };
    }
}
