package ru.practicum.eventcompilation;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import ru.practicum.event.Event;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
public class EventCompilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(length = 512)
    private String title;

    @Column
    @ColumnDefault("false")
    private Boolean pinned;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "events_compilation_group",
            joinColumns = @JoinColumn(name = "event_compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> eventsCompilationGroupEvents;

}