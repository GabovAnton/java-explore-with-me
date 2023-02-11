package ru.practicum.category;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.Event;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "categories")

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)

    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Event> categoryEvents;

}