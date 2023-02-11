package ru.practicum.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column
    private Double lat;

    @Column
    private Double lon;

    @OneToMany(mappedBy = "location", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Event> locationEvents;

}
