package ru.fintech;

class Main {
    public static void main(String[] args) {
        var list = new CustomLinkedList<Integer>();
        list.addAll(1, 2, 3, 4, 5);
        var iterator = list.iterator();
        var value = iterator.next();
        System.out.println(value);
        System.out.println("Оставшиеся:");
        iterator.forEachRemaining(System.out::println);
    }
}
