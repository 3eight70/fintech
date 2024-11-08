package ru.tinkoff.fintech.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "place_id", nullable = false)
    private UUID placeId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public Event(UUID placeId, String name, LocalDateTime date) {
        this.placeId = placeId;
        this.name = name;
        this.date = date;
    }
}
