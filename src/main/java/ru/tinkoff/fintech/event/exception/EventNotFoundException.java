package ru.tinkoff.fintech.event.exception;

import java.util.UUID;
import ru.tinkoff.fintech.common.exception.NotFoundException;

public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException(UUID eventId){
        super("Событие с id: " + eventId + " не найдено");
    }
}
