package ru.tinkoff.fintech.event.specification;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import ru.tinkoff.fintech.event.entity.Event;

public final class EventSpecification {
    public static Specification<Event> nameLike(String name){
        if (name == null || name.isEmpty()){
            return null;
        }

        return (((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%")));
    }

    public static Specification<Event> timeLessOrEqualThan(LocalDateTime timeTo){
        if (timeTo != null){
            return (((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("date"), timeTo)));
        }

        return null;
    }

    public static Specification<Event> timeGreaterOrEqualThan(LocalDateTime timeFrom){
        if (timeFrom != null){
            return (((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("date"), timeFrom)));
        }

        return null;
    }

    public static Specification<Event> themeIdEquals(UUID placeId){
        if (placeId == null){
            return null;
        }

        return (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("placeId"), placeId)));
    }
}
