package ru.fintech.lesson;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Кастомный двусвязный список
 */
public class CustomLinkedList<T> {
    private transient ListNode<T> head;
    private transient ListNode<T> tail;
    private int size = 0;

    private static class ListNode<T> {
        ListNode(ListNode<T> prev, ListNode<T> next, T value) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        T value;
        ListNode<T> prev;
        ListNode<T> next;
    }

    /**
     * Получение размера списка
     *
     * @return size
     */
    public int size() {
        return size;
    }

    /**
     * Получение "головы" списка (первого элемента)
     *
     * @return head
     */
    public T getHead() {
        if (head == null) {
            throw new NoSuchElementException("Голову дома забыл");
        }

        return head.value;
    }

    /**
     * Получение "хвоста" списка (последнего элемента)
     *
     * @return tail
     */
    public T getTail() {
        if (tail == null) {
            throw new NoSuchElementException();
        }

        return tail.value;
    }

    /**
     * Добавление элемента в конец списка
     *
     * @param element - элемент, подлежащий добавлению
     */
    public void add(T element) {
        ListNode<T> newNode = new ListNode<>(tail, null, element);

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }

        tail = newNode;
        size++;
    }

    /**
     * Получение элемента по индексу
     *
     * @param index - индекс получаемого элемента
     * @return value - значение элемента по индексу
     */
    public T get(int index) {
        return findNodeByIndex(index).value;
    }

    /**
     * Удаление элемента по указанному индексу
     *
     * @param index - индекс удаляемого элемента
     */
    public void remove(int index) {
        ListNode<T> currentNode = findNodeByIndex(index);
        ListNode<T> prevNode = currentNode.prev;
        ListNode<T> nextNode = currentNode.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
        }

        size--;
    }

    /**
     * Проверка на наличие элемента по значению
     *
     * @param value - искомое значение
     * @return false - значение не найдено, true - значение найдено
     */
    public Boolean contains(T value) {
        ListNode<T> currentNode = head;

        while (currentNode != null) {
            if (currentNode.value.equals(value)) {
                return true;
            }

            currentNode = currentNode.next;
        }

        return false;
    }

    /**
     * Добавляет все элементы коллекции в конец нашего списка
     *
     * @param collection - добавляемая коллекция
     * @return true - элементы добавлены
     */
    public boolean addAll(Collection<? extends T> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection является null");
        }

        for (T element : collection) {
            add(element);
        }

        return true;
    }

    /**
     * Добавляет все элементы списка в конец нашего списка
     * @param list - добавляемый список
     * @return true - элементы добавлены
     */
    public boolean addAll(CustomLinkedList<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("list является null");
        }

        ListNode<T> currentNode = list.head;

        while (currentNode != null) {
            add(currentNode.value);
            currentNode = currentNode.next;
        }

        return true;
    }

    public static <T> CustomLinkedList<T> toCustomLinkedList(Stream<T> stream) {
        CustomLinkedList<T> linkedList = new CustomLinkedList<>();

        stream.reduce(
                linkedList,
                (list, element) -> {
                    list.add(element);
                    return list;
                },
                (list1, list2) -> {
                    list1.addAll(list2);

                    return list1;
                }
        );

        return linkedList;
    }

    /**
     * Позволяет вывести все значения списка через пробел
     * Пример: 1 2 3
     */
    public void printAll() {
        ListNode<T> currentNode = head;

        while (currentNode != null) {
            System.out.print(currentNode.value + " ");
            currentNode = currentNode.next;
        }
    }

    private ListNode<T> findNodeByIndex(int index) {
        if (index >= size || index < 0) {
            throw new IllegalArgumentException("index выходит за пределы списка");
        }

        int count = 0;
        ListNode<T> currentNode;
        boolean tailFlag;

        if (index > size / 2) {
            currentNode = tail;
            tailFlag = true;
            index = size - index - 1;
        } else {
            currentNode = head;
            tailFlag = false;
        }

        while (count < index) {
            currentNode = tailFlag ? currentNode.prev : currentNode.next;
            count++;
        }

        return currentNode;
    }
}
