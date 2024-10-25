package ru.fintech.kotlin.datasource.observer

class Subject<T> {
    private val observers = mutableListOf<Observer<T>>()

    fun attach(observer: Observer<T>) {
        observers.add(observer)
    }

    fun detach(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun notifyObservers(entity: T) {
        for (observer in observers) {
            observer.update(entity)
        }
    }
}