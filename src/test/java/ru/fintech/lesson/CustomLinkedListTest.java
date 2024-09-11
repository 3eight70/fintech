package ru.fintech.lesson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListTest {
    private CustomLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    public void testAdd() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(3, list.size());
        assertEquals(1, list.getHead());
        assertEquals(3, list.getTail());
    }

    @Test
    public void testGetHeadEmptyList() {
        assertThrows(NoSuchElementException.class, () -> list.getHead());
    }

    @Test
    public void testGetTailEmptyList() {
        assertThrows(NoSuchElementException.class, () -> list.getTail());
    }

    @Test
    public void testGet() {
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }

    @Test
    public void testRemove() {
        list.add(1);
        list.add(2);
        list.add(3);

        list.remove(1);

        assertEquals(2, list.size());
        assertEquals(1, list.getHead());
        assertEquals(3, list.getTail());
    }

    @Test
    public void testContains() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertTrue(list.contains(2));
        assertFalse(list.contains(4));
    }

    @Test
    public void testAddAllCollection() {
        List<Integer> collection = Arrays.asList(4, 5, 6);
        list.addAll(collection);

        assertEquals(3, list.size());
        assertTrue(list.contains(4));
        assertTrue(list.contains(5));
        assertTrue(list.contains(6));
    }

    @Test
    public void testAddAllCustomList() {
        CustomLinkedList<Integer> customList = new CustomLinkedList<>();
        customList.add(1);
        customList.add(2);
        customList.add(3);

        list.addAll(customList);

        assertEquals(3, list.size());
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
    }

    @Test
    public void testStreamToCustomLinkedList() {
        Stream<Integer> stream = Stream.of(7, 8, 9);
        CustomLinkedList<Integer> result = CustomLinkedList.toCustomLinkedList(stream);

        assertEquals(3, result.size());
        assertEquals(7, result.getHead());
        assertEquals(9, result.getTail());
    }

    @Test
    public void testToCustomLinkedListEmptyStream() {
        Stream<Integer> stream = Stream.empty();
        CustomLinkedList<Integer> result = CustomLinkedList.toCustomLinkedList(stream);

        assertEquals(0, result.size());
    }
}
