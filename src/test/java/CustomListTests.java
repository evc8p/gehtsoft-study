import com.evch.rrm.CustomList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CustomListTests {
    @Test
    void customListConstructorShouldCreateListWithDefaultValues() {
        CustomList list = new CustomList();
        assertTrue(list.isEmpty());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacity() {
        CustomList list = new CustomList(1);
        assertEquals(0, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityOf0() {
        CustomList list = new CustomList(0);
        assertEquals(0, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomList(-1),
                "Initial capacity is less than 0");
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityAndMultiplier() {
        CustomList list = new CustomList(5, 2.0f);
        assertTrue(list.isEmpty());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityAndMultiplierNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomList(-1, 1.0f),
                "Initial capacity is less than 0 or multiplier < 1");

        assertThrows(IllegalArgumentException.class,
                () -> new CustomList(0, 0.9f),
                "Initial capacity is less than 0 or multiplier < 1");
    }

    @Test
    void customListConstructorShouldCreateListWithElementsWithSizeOf5() {
        CustomList list = new CustomList(new String[]{"null", "5", "1", " 9", "7"});
        assertFalse(list.isEmpty());
        assertEquals(5, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithElementsWithSizeOf11() {
        CustomList list = new CustomList(new Integer[]{null, 5, 1, 9, 7, 8, 20, null, null, 10, 11});
        assertFalse(list.isEmpty());
        assertEquals(11, list.size());
    }

    @Test
    void customListConstructorShouldCreateListWithElementsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomList(null),
                "Array is null");
    }

    @Test
    void createdListOf10ElementsShouldHaveSizeEqualTo10() {
        CustomList list = new CustomList(new Integer[]{null, 5, 1, 9, 7, 8, 20, null, null, 10});
        assertFalse(list.isEmpty());
        assertEquals(10, list.size());
    }

    @Test
    void adding20ItemShouldIncreaseSizeOfListBy20() {
        CustomList list = new CustomList(new Object[new Random().nextInt(7)]);
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
        CustomList list = new CustomList(new Object[size]);
        list.add(7, 50);
        assertEquals(50, (int) list.get(7));
    }

    @Test
    void elementSetByIndexShouldBeFoundInItsIndex() {
        int size = new Random().nextInt(8) + 8;
        CustomList list = new CustomList(new Object[size]);
        list.set(7, 50);
        assertEquals(50, (int) list.get(7));
    }

    @Test
    void elementRemovedByIndexShouldBeNotFoundInListAndDecreaseListSize() {
        CustomList list = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10});
        list.remove(0);
        list.remove(7);
        assertEquals(8, list.size());
        assertEquals(-1, list.indexOf(1));
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void elementRemovedByValueNullShouldBeNotFoundInListAndDecreaseListSize() {
        CustomList list = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10});
        list.remove(null);
        assertEquals(9, list.size());
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void clearMethodShouldClearEntireList() {
        CustomList list = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10});
        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfAddMethod() {
        CustomList customList = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        List arrayList = new ArrayList();
        arrayList.addAll(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        customList.add(11);
        arrayList.add(11);
        assertEquals(11, customList.get(10));
        assertEquals(11, arrayList.get(10));
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfGetMethod() {
        CustomList customList = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        List arrayList = new ArrayList();
        arrayList.addAll(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        assertEquals(3, customList.get(2));
        assertEquals(3, arrayList.get(2));
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfSetMethod() {
        CustomList customList = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        List arrayList = new ArrayList();
        arrayList.addAll(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        customList.set(0, 0);
        arrayList.set(0, 0);
        assertEquals(0, customList.get(0));
        assertEquals(10, customList.size());
        assertEquals(0, arrayList.get(0));
        assertEquals(10, arrayList.size());
    }

    @Test
    void customListAndArrayListShouldReturnSameResultsOfRemoveMethod() {
        CustomList customList = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        List arrayList = new ArrayList();
        arrayList.addAll(List.of(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
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
        assertDoesNotThrow(() -> new CustomList(integers));
        assertDoesNotThrow(() -> new CustomList<Integer>());
        CustomList<Integer> customList = new CustomList<>(20, 2.0f);
        customList.add(0, 5);
        customList.add(0, 10);
        customList.add(0, 15);
        customList.set(0, 20);
        customList.remove(1);
        assertEquals(5, customList.get(1));
    }
}
