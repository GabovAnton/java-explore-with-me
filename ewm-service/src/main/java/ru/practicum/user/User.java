package ru.practicum.user;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.request.Request;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "service_users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @OneToMany(mappedBy = "requester")
    private Set<Request> requests;

}