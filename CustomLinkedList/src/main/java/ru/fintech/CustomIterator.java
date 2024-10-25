package ru.fintech;

import java.util.function.Consumer;

public interface CustomIterator<E> {
    Boolean hasNext();

    E next();

    void forEachRemaining(Consumer<? super E> action);
}
