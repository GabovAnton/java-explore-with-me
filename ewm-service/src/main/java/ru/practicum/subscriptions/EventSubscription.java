package ru.practicum.subscriptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.event.Event;
import ru.practicum.notification.SubscriptionNotification;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class EventSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private Long subscriberId;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column
    private Boolean notifyNewEvents;

    @Column
    private Boolean notifyChangeEvents;

    @Column
    private Boolean notifyDeleteEvents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private Set<SubscriptionNotification> subscriptionNotifications;

    @Column(nullable = false, name = "event_initiator_id")
    private Long eventInitiatorId;

}