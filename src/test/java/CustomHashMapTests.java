import com.evch.rrm.CustomHashMap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CustomHashMapTests {
    @Test
    void sizeMethodShouldReturn0ForEmptyCustomHashMap() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertEquals(0, customHashMap.size());
    }

    @Test
    void sizeMethodShouldReturn5ForCustomHashMapWith5Pairs() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put(0, 100);
        customHashMap.put(null, 101);
        customHashMap.put(10, 200);
        customHashMap.put(26, 300);
        customHashMap.put(42, null);
        assertEquals(5, customHashMap.size());
    }

    @Test
    void isEmptyMethodShouldReturnTrueForEmptyCustomHashMap() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertTrue(customHashMap.isEmpty());
    }

    @Test
    void isEmptyMethodShouldReturnFalseForNotEmptyCustomHashMap() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put(1, 1);
        assertFalse(customHashMap.isEmpty());
    }

    @Test
    void putMethodShouldBeAbleToAddNodeWithNullKey() {
        CustomHashMap<String, String> customHashMap = new CustomHashMap<>();
        customHashMap.put(null, "one");
        assertFalse(customHashMap.isEmpty());
    }

    @Test
    void putMethodShouldBeAbleToAddNodeWithNullValue() {
        CustomHashMap<String, String> customHashMap = new CustomHashMap<>();
        customHashMap.put("one", null);
        assertFalse(customHashMap.isEmpty());
    }

    @Test
    void putMethodShouldAddTwoNodesIntoOneBucket() {
        CustomHashMap<Integer, String> customHashMap = new CustomHashMap<>();
        String result = customHashMap.put(0, "one");
        assertNull(result);
        result = customHashMap.put(null, "two");
        assertNull(result);
        assertEquals(2, customHashMap.size());
    }

    @Test
    void putMethodShouldReplaceValue() {
        CustomHashMap<String, Integer> customHashMap = new CustomHashMap<>();
        Integer result = customHashMap.put("one", null);
        assertNull(result);
        result = customHashMap.put("one", 1);
        assertNull(result);
        result = customHashMap.put("one", 0);
        assertEquals(1, result);
        assertEquals(1, customHashMap.size());
    }

    @Test
    void containsKeyMethodShouldReturnTrueForExistingKey() {
        CustomHashMap<String, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put("one", 100);
        customHashMap.put("two", 200);
        customHashMap.put("three", 300);
        assertTrue(customHashMap.containsKey("two"));
        assertTrue(customHashMap.containsKey("two"));
        assertTrue(customHashMap.containsKey("three"));
    }

    @Test
    void containsKeyMethodShouldReturnFalseForMissingKey() {
        CustomHashMap<String, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put("one", 100);
        customHashMap.put("two", 200);
        customHashMap.put("three", 300);
        assertFalse(customHashMap.containsKey("four"));
    }

    @Test
    void containsValueMethodShouldReturnTrueForExistingValue() {
        CustomHashMap<String, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put("one", 100);
        customHashMap.put("two", 200);
        customHashMap.put("three", 300);
        customHashMap.put("four", 0);
        customHashMap.put("five", null);
        assertTrue(customHashMap.containsValue(0));
        assertTrue(customHashMap.containsValue(null));
        assertTrue(customHashMap.containsValue(200));
    }

    @Test
    void containsValueMethodShouldReturnFalseForMissingValue() {
        CustomHashMap<String, Integer> customHashMap = new CustomHashMap<>();
        assertFalse(customHashMap.containsValue(100));
        customHashMap.put("one", 100);
        customHashMap.put("two", 200);
        customHashMap.put("three", 300);
        assertFalse(customHashMap.containsValue(400));
        assertFalse(customHashMap.containsValue(null));
    }

    @Test
    void getMethodShouldReturnValuesSetForKeys() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertNull(customHashMap.get(5));
        customHashMap.put(0, 100);
        customHashMap.put(null, 101);
        customHashMap.put(10, 200);
        customHashMap.put(26, 300);
        customHashMap.put(42, 400);
        customHashMap.put(58, null);
        assertEquals(100, customHashMap.get(0));
        assertEquals(101, customHashMap.get(null));
        assertEquals(200, customHashMap.get(10));
        assertEquals(300, customHashMap.get(26));
        assertEquals(400, customHashMap.get(42));
        assertNull(customHashMap.get(58));
    }

    @Test
    void removeMethodShouldRemoveOnlyOccurrenceInBucket() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put(1, 100);
        customHashMap.put(2, 200);
        customHashMap.put(3, 300);
        customHashMap.remove(2);
        assertFalse(customHashMap.containsKey(2));
        assertNull(customHashMap.get(2));
        assertFalse(customHashMap.containsValue(200));
        assertEquals(2, customHashMap.size());
    }

    @Test
    void removeMethodShouldRemoveFirstOfTwoOccurrencesInBucket() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put(1, 100);
        customHashMap.put(2, 200);
        customHashMap.put(18, 300);
        customHashMap.remove(2);
        assertFalse(customHashMap.containsKey(2));
        assertNull(customHashMap.get(2));
        assertFalse(customHashMap.containsValue(200));
        assertEquals(2, customHashMap.size());
    }

    @Test
    void removeMethodShouldRemoveLastOfTwoOccurrencesInBucket() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put(1, 100);
        customHashMap.put(2, 200);
        customHashMap.put(18, 300);
        customHashMap.remove(18);
        assertFalse(customHashMap.containsKey(18));
        assertNull(customHashMap.get(18));
        assertFalse(customHashMap.containsValue(300));
        assertEquals(2, customHashMap.size());
    }

    @Test
    void removeMethodShouldRemoveMiddleOneOfThreeOccurrencesInBucket() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        customHashMap.put(1, 100);
        customHashMap.put(2, 200);
        customHashMap.put(18, 300);
        customHashMap.put(34, 400);
        customHashMap.remove(18);
        assertFalse(customHashMap.containsKey(18));
        assertNull(customHashMap.get(18));
        assertFalse(customHashMap.containsValue(300));
        assertEquals(3, customHashMap.size());
    }

    @Test
    void putAllMethodShouldAddAllValuesFromOtherMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(null, "10");
        map.put(0, "20");
        map.put(10, "30");
        map.put(26, "40");
        map.put(42, "50");
        CustomHashMap<Integer, String> customHashMap = new CustomHashMap<>();
        customHashMap.putAll(map);
        assertEquals(5, customHashMap.size());
        assertEquals("10", customHashMap.get(null));
        assertEquals("20", customHashMap.get(0));
        assertEquals("30", customHashMap.get(10));
        assertEquals("40", customHashMap.get(26));
        assertEquals("50", customHashMap.get(42));
    }

    @Test
    void getMethodShouldRemoveAllMapValues() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertNull(customHashMap.get(5));
        customHashMap.put(0, 100);
        customHashMap.put(null, 101);
        customHashMap.put(10, 200);
        customHashMap.put(26, 300);
        customHashMap.put(58, null);
        assertEquals(5, customHashMap.size());
        customHashMap.clear();
        assertEquals(0, customHashMap.size());
        customHashMap.put(5, 1000);
        assertNull(customHashMap.get(0));
        assertNull(customHashMap.get(null));
        assertNull(customHashMap.get(10));
        assertNull(customHashMap.get(26));
        assertNull(customHashMap.get(58));
    }

    @Test
    void keySetMethodShouldReturnSetOfAllKeys() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertNull(customHashMap.get(5));
        customHashMap.put(0, 100);
        customHashMap.put(null, 101);
        customHashMap.put(10, 200);
        customHashMap.put(26, 300);
        customHashMap.put(58, null);
        Set<Integer> keys = customHashMap.keySet();
        assertEquals(5, keys.size());
        List<Integer> expected = new ArrayList<>();
        expected.add(0);
        expected.add(null);
        expected.add(10);
        expected.add(26);
        expected.add(58);
        assertTrue(keys.containsAll(expected));
    }

    @Test
    void valuesMethodShouldReturnListOfAllValues() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertNull(customHashMap.get(5));
        customHashMap.put(0, 100);
        customHashMap.put(null, 101);
        customHashMap.put(10, 200);
        customHashMap.put(26, 300);
        customHashMap.put(58, null);
        List<Integer> values = (List<Integer>) customHashMap.values();
        assertEquals(5, values.size());
        List<Integer> expected = new ArrayList<>();
        expected.add(100);
        expected.add(101);
        expected.add(200);
        expected.add(300);
        expected.add(null);
        assertTrue(values.containsAll(expected));
    }

    @Test
    void entrySetMethodShouldReturnSetOfEntries() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>();
        assertNull(customHashMap.get(5));
        customHashMap.put(0, 100);
        customHashMap.put(null, 101);
        customHashMap.put(10, 200);
        customHashMap.put(26, 300);
        customHashMap.put(58, null);
        Set<Map.Entry<Integer, Integer>> entrySet = customHashMap.entrySet();
        assertEquals(5, entrySet.size());
    }

    @Test
    void putMethodShouldAddElementsIfThereAreMoreThan12Elements() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>(16, 0.75f);
        customHashMap.putAll(Map.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5));
        customHashMap.putAll(Map.of(17, 17, 7, 7, 8, 8, 20, 20, 36, 21));
        customHashMap.putAll(Map.of(52, 22, 10, 12));
        assertEquals(12, customHashMap.size());
        customHashMap.put(13, 13);
        assertEquals(13, customHashMap.size());
    }

    @Test
    void putMethodShouldBeAbleToAddMillionElements() {
        CustomHashMap<Integer, Integer> customHashMap = new CustomHashMap<>(16, 0.75f);
        for (int i = 0; i < 1_000_000; i++) {
            customHashMap.put(i, i);
        }
        assertEquals(1_000_000, customHashMap.size());
    }

    // Todo тесты на конструкторы, расширение массива, использование дерева.
}
