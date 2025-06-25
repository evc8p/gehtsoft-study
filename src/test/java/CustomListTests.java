import com.evch.rrm.CustomList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class CustomListTests {
    @Test
    void customListConstructorShouldCreateListWithDefaultValues() {
        CustomList list = new CustomList();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacity() {
        CustomList list = new CustomList(1);
        Assertions.assertEquals(list.size(),0);
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityOf0() {
        CustomList list = new CustomList(0);
        Assertions.assertEquals(list.size(),0);
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityNegative() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CustomList(-1),
                "Initial capacity is less than 0");
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityAndMultiplier() {
        CustomList list = new CustomList(5, 2.0f);
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void customListConstructorShouldCreateListWithInitialCapacityAndMultiplierNegative() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CustomList(-1, 1.0f),
                "Initial capacity is less than 0 or multiplier < 1");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CustomList(0, 0.9f),
                "Initial capacity is less than 0 or multiplier < 1");
    }

    @Test
    void customListConstructorShouldCreateListWithElementsWithSizeOf5() {
        CustomList list = new CustomList(new Integer[]{null, 5, 1, 9, 7});
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(list.size(), 5);
    }

    @Test
    void customListConstructorShouldCreateListWithElementsWithSizeOf11() {
        CustomList list = new CustomList(new Integer[]{null, 5, 1, 9, 7, 8, 20, null, null, 10, 11});
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(list.size(), 11);
    }

    @Test
    void customListConstructorShouldCreateListWithElementsNegative() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CustomList(null),
                "Array is null");
    }

    @Test
    void createdListOf10ElementsShouldHaveSizeEqualTo10() {
        CustomList list = new CustomList(new Integer[]{null, 5, 1, 9, 7, 8, 20, null, null, 10});
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(list.size(), 10);
    }

    @Test
    void addingOneItemShouldIncreaseSizeOfListBy1() {
        CustomList list = new CustomList(new Object[new Random().nextInt(7)]);
        int oldSize = list.size();
        list.add(1);
        Assertions.assertEquals(list.size(), oldSize + 1);

        oldSize = list.size();
        list.add(0, 50);
        Assertions.assertEquals(list.size(), oldSize + 1);
    }

    @Test
    void elementAddedByIndexShouldBeFoundInItsIndex() {
        int size = new Random().nextInt(8) + 8;
        CustomList list = new CustomList(new Object[size]);
        list.add(7, 50);
        Assertions.assertEquals((int) list.get(7), 50);
    }

    @Test
    void elementSetByIndexShouldBeFoundInItsIndex() {
        int size = new Random().nextInt(8) + 8;
        CustomList list = new CustomList(new Object[size]);
        list.set(7, 50);
        Assertions.assertEquals((int) list.get(7), 50);
    }

    @Test
    void elementRemovedByIndexShouldBeNotFoundInListAndDecreaseListSize() {
        CustomList list = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10});
        list.remove(0);
        list.remove(7);
        Assertions.assertEquals(list.size(), 8);
        Assertions.assertEquals(list.indexOf(1), -1);
        Assertions.assertEquals(list.indexOf(null), -1);
    }

    @Test
    void elementRemovedByValueNullShouldBeNotFoundInListAndDecreaseListSize() {
        CustomList list = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10});
        list.remove(null);
        Assertions.assertEquals(list.size(), 9);
        Assertions.assertEquals(list.indexOf(null), -1);
    }

    @Test
    void clearMethodShouldClearEntireList() {
        CustomList list = new CustomList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, null, 10});
        Assertions.assertFalse(list.isEmpty());
        list.clear();
        Assertions.assertTrue(list.isEmpty());
    }
}
