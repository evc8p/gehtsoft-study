import com.evch.rrm.CustomLinkedHashMap;
import com.evch.rrm.CustomLinkedHashMap.Entry;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedHashMapTests {
    @Test
    void putMethodShouldAddItemToEndOfList() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(1, 1);
        customLinkedHashMap.put(2, 10);
        assertEquals(2, customLinkedHashMap.size());
        assertEquals(10, customLinkedHashMap.getLast());
    }

    @Test
    void putLastMethodShouldAddItemToEndOfList() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.putLast(1, 1);
        customLinkedHashMap.putLast(2, 10);
        assertEquals(2, customLinkedHashMap.size());
        assertEquals(1, customLinkedHashMap.getFirst());
        assertEquals(10, customLinkedHashMap.getLast());
    }

    @Test
    void putFirstMethodShouldAddItemToHeadOfList() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.putFirst(1, 1);
        customLinkedHashMap.putFirst(2, 10);
        assertEquals(2, customLinkedHashMap.size());
        assertEquals(10, customLinkedHashMap.getFirst());
        assertEquals(1, customLinkedHashMap.getLast());
    }

    @Test
    void getLinkedKeySetMethodShouldReturnSetOfKeysInOrderFromAdding() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(3, 1);
        customLinkedHashMap.put(1, 10);
        customLinkedHashMap.put(2, 20);
        Set<Integer> set = customLinkedHashMap.getLinkedKeySet();
        Iterator<Integer> iterator = set.iterator();
        assertEquals(3, iterator.next());
        assertEquals(1, iterator.next());
        assertEquals(2, iterator.next());
    }

    @Test
    void getLinkedValuesMethodShouldReturnSetOfValuesInOrderFromAdding() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(3, 100);
        customLinkedHashMap.put(1, 10);
        customLinkedHashMap.put(2, 20);
        Set<Integer> set = customLinkedHashMap.getLinkedValues();
        Iterator<Integer> iterator = set.iterator();
        assertEquals(100, iterator.next());
        assertEquals(10, iterator.next());
        assertEquals(20, iterator.next());
    }

    @Test
    void getLinkedEntriesMethodShouldReturnSetOfEntriesInOrderFromAdding() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(3, 100);
        customLinkedHashMap.put(1, 10);
        customLinkedHashMap.put(2, 20);
        Set<Entry<Integer, Integer>> set = customLinkedHashMap.getLinkedEntries();
        Iterator<Entry<Integer, Integer>> iterator = set.iterator();
        Entry<Integer, Integer> entry = iterator.next();
        assertEquals(3, entry.getKey());
        assertEquals(100, entry.getValue());
        entry = iterator.next();
        assertEquals(1, entry.getKey());
        assertEquals(10, entry.getValue());
        entry = iterator.next();
        assertEquals(2, entry.getKey());
        assertEquals(20, entry.getValue());
    }

    @Test
    void clearMethodShouldRemoveAllValuesOfInternalMapAndList() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(3, 100);
        customLinkedHashMap.put(1, 10);
        customLinkedHashMap.put(2, 20);
        customLinkedHashMap.clear();
        assertTrue(customLinkedHashMap.isEmpty());
        assertEquals(0, customLinkedHashMap.getLinkedValues().size());
    }

    @Test
    void removeMethodShouldRemoveValuesCorrespondingToKeys() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(3, 100);
        customLinkedHashMap.put(null, 500);
        customLinkedHashMap.put(10, null);
        customLinkedHashMap.put(1, 10);
        customLinkedHashMap.put(2, 20);
        customLinkedHashMap.remove(3);
        customLinkedHashMap.remove(null);
        customLinkedHashMap.remove(2);
        assertEquals(2, customLinkedHashMap.size());
        assertNull( customLinkedHashMap.getFirst());
        assertEquals(10, customLinkedHashMap.getLast());
    }

    @Test
    void removeFirstMethodShouldRemoveOnlyHeadOfList() {
        CustomLinkedHashMap<Integer, Integer> customLinkedHashMap = new CustomLinkedHashMap<>();
        customLinkedHashMap.put(3, 100);
        customLinkedHashMap.put(null, 500);
        customLinkedHashMap.put(10, null);
        customLinkedHashMap.put(1, 10);
        customLinkedHashMap.put(2, 20);
        Integer oldValue = customLinkedHashMap.removeFirst();
        assertEquals(100, oldValue);
        assertEquals(4, customLinkedHashMap.size());
        assertNull(customLinkedHashMap.get(3));
        assertEquals(500, customLinkedHashMap.getFirst());
    }
}
