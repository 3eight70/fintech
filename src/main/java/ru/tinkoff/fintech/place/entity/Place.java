package ru.tinkoff.fintech.place.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tinkoff.fintech.event.entity.Event;

@Entity
@Table(name = "places")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Place {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "title", unique = true, nullable = false)
    private String title;
    @Column(name = "slug", nullable = false)
    private String slug;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToMany(mappedBy = "placeId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Event> events;

    public Place(String title, String slug, String description) {
        this.title = title;
        this.slug = slug;
        this.description = description;
    }
}
