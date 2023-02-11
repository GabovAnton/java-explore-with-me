package ru.practicum.event;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import ru.practicum.request.Request;
import ru.practicum.user.User;
import ru.practicum.category.Category;
import ru.practicum.eventcompilation.EventCompilation;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "annotation", length = 2000)

    private String annotation;

    @Column(length = 7000, name = "description")
    private String description;

    @Column(nullable = false, name = "event_date")

    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(nullable = false, name = "created_on")
    private LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column
    @ColumnDefault("false")
    private Boolean paid;

    @ColumnDefault("0")
    @Column(name = "participant_limit")
    private Integer participantLimit;

    @ColumnDefault("true")
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(length = 120, name = "title")

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status")
    private StateEvent eventStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToMany(mappedBy = "eventsCompilationGroupEvents")
    private Set<EventCompilation> eventsCompilationGroupEventCompilations;

    @OneToMany(mappedBy = "event")
    private Set<Request> eventRequests;

    @Column(name = "views")
    private Long views;

}
