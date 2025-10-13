import com.evch.rrm.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.State.TERMINATED;
import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentHashMapTests {
    @Test
    void sizeMethodShouldReturn0ForEmptyConcurrentHashMap() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertEquals(0, concurrentHashMap.size());
    }

    @Test
    void sizeMethodShouldReturn5ForConcurrentHashMapWith5Pairs() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(0, 100);
        concurrentHashMap.put(null, 101);
        concurrentHashMap.put(10, 200);
        concurrentHashMap.put(26, 300);
        concurrentHashMap.put(42, null);
        assertEquals(5, concurrentHashMap.size());
    }

    @Test
    void isEmptyMethodShouldReturnTrueForEmptyConcurrentHashMap() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertTrue(concurrentHashMap.isEmpty());
    }

    @Test
    void isEmptyMethodShouldReturnFalseForNotEmptyConcurrentHashMap() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1, 1);
        assertFalse(concurrentHashMap.isEmpty());
    }

    @Test
    void putMethodShouldBeAbleToAddNodeWithNullKey() {
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(null, "one");
        assertFalse(concurrentHashMap.isEmpty());
    }

    @Test
    void putMethodShouldBeAbleToAddNodeWithNullValue() {
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("one", null);
        assertFalse(concurrentHashMap.isEmpty());
    }

    @Test
    void putMethodShouldAddTwoNodesIntoOneBucket() {
        ConcurrentHashMap<Integer, String> concurrentHashMap = new ConcurrentHashMap<>();
        String result = concurrentHashMap.put(0, "one");
        assertNull(result);
        result = concurrentHashMap.put(null, "two");
        assertNull(result);
        assertEquals(2, concurrentHashMap.size());
    }

    @Test
    void putMethodShouldReplaceValue() {
        ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        Integer result = concurrentHashMap.put("one", null);
        assertNull(result);
        result = concurrentHashMap.put("one", 1);
        assertNull(result);
        result = concurrentHashMap.put("one", 0);
        assertEquals(1, result);
        assertEquals(1, concurrentHashMap.size());
    }

    @Test
    void containsKeyMethodShouldReturnTrueForExistingKey() {
        ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("one", 100);
        concurrentHashMap.put("two", 200);
        concurrentHashMap.put("three", 300);
        assertTrue(concurrentHashMap.containsKey("two"));
        assertTrue(concurrentHashMap.containsKey("two"));
        assertTrue(concurrentHashMap.containsKey("three"));
    }

    @Test
    void containsKeyMethodShouldReturnFalseForMissingKey() {
        ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("one", 100);
        concurrentHashMap.put("two", 200);
        concurrentHashMap.put("three", 300);
        assertFalse(concurrentHashMap.containsKey("four"));
    }

    @Test
    void containsValueMethodShouldReturnTrueForExistingValue() {
        ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("one", 100);
        concurrentHashMap.put("two", 200);
        concurrentHashMap.put("three", 300);
        concurrentHashMap.put("four", 0);
        concurrentHashMap.put("five", null);
        assertTrue(concurrentHashMap.containsValue(0));
        assertTrue(concurrentHashMap.containsValue(null));
        assertTrue(concurrentHashMap.containsValue(200));
    }

    @Test
    void containsValueMethodShouldReturnFalseForMissingValue() {
        ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertFalse(concurrentHashMap.containsValue(100));
        concurrentHashMap.put("one", 100);
        concurrentHashMap.put("two", 200);
        concurrentHashMap.put("three", 300);
        assertFalse(concurrentHashMap.containsValue(400));
        assertFalse(concurrentHashMap.containsValue(null));
    }

    @Test
    void getMethodShouldReturnValuesSetForKeys() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertNull(concurrentHashMap.get(5));
        concurrentHashMap.put(0, 100);
        concurrentHashMap.put(null, 101);
        concurrentHashMap.put(10, 200);
        concurrentHashMap.put(26, 300);
        concurrentHashMap.put(42, 400);
        concurrentHashMap.put(58, null);
        assertEquals(100, concurrentHashMap.get(0));
        assertEquals(101, concurrentHashMap.get(null));
        assertEquals(200, concurrentHashMap.get(10));
        assertEquals(300, concurrentHashMap.get(26));
        assertEquals(400, concurrentHashMap.get(42));
        assertNull(concurrentHashMap.get(58));
    }

    @Test
    void removeMethodShouldRemoveOnlyOccurrenceInBucket() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1, 100);
        concurrentHashMap.put(2, 200);
        concurrentHashMap.put(3, 300);
        concurrentHashMap.remove(2);
        assertFalse(concurrentHashMap.containsKey(2));
        assertNull(concurrentHashMap.get(2));
        assertFalse(concurrentHashMap.containsValue(200));
        assertEquals(2, concurrentHashMap.size());
    }

    @Test
    void removeMethodShouldRemoveFirstOfTwoOccurrencesInBucket() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1, 100);
        concurrentHashMap.put(2, 200);
        concurrentHashMap.put(18, 300);
        concurrentHashMap.remove(2);
        assertFalse(concurrentHashMap.containsKey(2));
        assertNull(concurrentHashMap.get(2));
        assertFalse(concurrentHashMap.containsValue(200));
        assertEquals(2, concurrentHashMap.size());
    }

    @Test
    void removeMethodShouldRemoveLastOfTwoOccurrencesInBucket() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1, 100);
        concurrentHashMap.put(2, 200);
        concurrentHashMap.put(18, 300);
        concurrentHashMap.remove(18);
        assertFalse(concurrentHashMap.containsKey(18));
        assertNull(concurrentHashMap.get(18));
        assertFalse(concurrentHashMap.containsValue(300));
        assertEquals(2, concurrentHashMap.size());
    }

    @Test
    void removeMethodShouldRemoveMiddleOneOfThreeOccurrencesInBucket() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1, 100);
        concurrentHashMap.put(2, 200);
        concurrentHashMap.put(18, 300);
        concurrentHashMap.put(34, 400);
        concurrentHashMap.remove(18);
        assertFalse(concurrentHashMap.containsKey(18));
        assertNull(concurrentHashMap.get(18));
        assertFalse(concurrentHashMap.containsValue(300));
        assertEquals(3, concurrentHashMap.size());
    }

    @Test
    void putAllMethodShouldAddAllValuesFromOtherMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(null, "10");
        map.put(0, "20");
        map.put(10, "30");
        map.put(26, "40");
        map.put(42, "50");
        ConcurrentHashMap<Integer, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.putAll(map);
        assertEquals(5, concurrentHashMap.size());
        assertEquals("10", concurrentHashMap.get(null));
        assertEquals("20", concurrentHashMap.get(0));
        assertEquals("30", concurrentHashMap.get(10));
        assertEquals("40", concurrentHashMap.get(26));
        assertEquals("50", concurrentHashMap.get(42));
    }

    @Test
    void getMethodShouldRemoveAllMapValues() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertNull(concurrentHashMap.get(5));
        concurrentHashMap.put(0, 100);
        concurrentHashMap.put(null, 101);
        concurrentHashMap.put(10, 200);
        concurrentHashMap.put(26, 300);
        concurrentHashMap.put(58, null);
        assertEquals(5, concurrentHashMap.size());
        concurrentHashMap.clear();
        assertEquals(0, concurrentHashMap.size());
        concurrentHashMap.put(5, 1000);
        assertNull(concurrentHashMap.get(0));
        assertNull(concurrentHashMap.get(null));
        assertNull(concurrentHashMap.get(10));
        assertNull(concurrentHashMap.get(26));
        assertNull(concurrentHashMap.get(58));
    }

    @Test
    void keySetMethodShouldReturnSetOfAllKeys() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertNull(concurrentHashMap.get(5));
        concurrentHashMap.put(0, 100);
        concurrentHashMap.put(null, 101);
        concurrentHashMap.put(10, 200);
        concurrentHashMap.put(26, 300);
        concurrentHashMap.put(58, null);
        Set<Integer> keys = concurrentHashMap.keySet();
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
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertNull(concurrentHashMap.get(5));
        concurrentHashMap.put(0, 100);
        concurrentHashMap.put(null, 101);
        concurrentHashMap.put(10, 200);
        concurrentHashMap.put(26, 300);
        concurrentHashMap.put(58, null);
        List<Integer> values = (List<Integer>) concurrentHashMap.values();
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
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        assertNull(concurrentHashMap.get(5));
        concurrentHashMap.put(0, 100);
        concurrentHashMap.put(null, 101);
        concurrentHashMap.put(10, 200);
        concurrentHashMap.put(26, 300);
        concurrentHashMap.put(58, null);
        Set<Map.Entry<Integer, Integer>> entrySet = concurrentHashMap.entrySet();
        assertEquals(5, entrySet.size());
    }

    @Test
    void putMethodShouldAddElementsIfThereAreMoreThan12Elements() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>(16, 0.75f);
        concurrentHashMap.putAll(Map.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5));
        concurrentHashMap.putAll(Map.of(17, 17, 7, 7, 8, 8, 20, 20, 36, 21));
        concurrentHashMap.putAll(Map.of(52, 22, 10, 12));
        assertEquals(12, concurrentHashMap.size());
        concurrentHashMap.put(13, 13);
        assertEquals(13, concurrentHashMap.size());
    }

    @Test
    void putMethodShouldBeAbleToAddMillionElements() {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>(16, 0.75f);
        for (int i = 0; i < 1_000_000; i++) {
            concurrentHashMap.put(i, i);
        }
        assertEquals(1_000_000, concurrentHashMap.size());
    }

    @Test
    void twoThreadsShouldBeAbleToAddThousandDifferentItemsEach() throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>(16, 0.75f);
        Thread firstThread = getThreadWithPutAndRangeOfIterations(concurrentHashMap, 0, 1000);
        Thread secondThread = getThreadWithPutAndRangeOfIterations(concurrentHashMap, 1000, 2000);
        firstThread.start();
        secondThread.start();
        int counter = 0;
        while (!(firstThread.getState() == TERMINATED && secondThread.getState() == TERMINATED) && counter++ < 100) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        assertEquals(2000, concurrentHashMap.size());
    }

    @Test
    void twoThreadsShouldNotBeAbleToAddSameValues() throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>(16, 0.75f);
        Thread firstThread = getThreadWithPutAndRangeOfIterations(concurrentHashMap, 0, 1000);
        Thread secondThread = getThreadWithPutAndRangeOfIterations(concurrentHashMap, 0, 1000);
        firstThread.start();
        secondThread.start();
        int counter = 0;
        while (!(firstThread.getState() == TERMINATED && secondThread.getState() == TERMINATED) && counter++ < 100) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        assertEquals(1000, concurrentHashMap.size());
    }

    private Thread getThreadWithPutAndRangeOfIterations(ConcurrentHashMap concurrentHashMap, int from, int till) {
        return new Thread(() -> {
            for (int i = from; i < till; i++) {
                concurrentHashMap.put(i, i);
            }
        });
    }
}
