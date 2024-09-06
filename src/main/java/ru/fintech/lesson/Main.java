package ru.fintech.lesson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        List<Integer> test = new ArrayList<>(Arrays.asList(1, 2, 3));
        list.add(1);
        list.get(0);
        list.remove(0);
        list.contains(1);
        list.addAll(test);
        list.printAll();
    }
}
