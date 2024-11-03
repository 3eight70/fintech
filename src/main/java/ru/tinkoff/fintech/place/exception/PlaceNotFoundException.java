package ru.tinkoff.fintech.place.exception;

import java.util.UUID;
import ru.tinkoff.fintech.common.exception.NotFoundException;

public class PlaceNotFoundException extends NotFoundException {
    public PlaceNotFoundException(UUID placeId) {
        super("Место с id: " + placeId + " не существует");
    }
}
