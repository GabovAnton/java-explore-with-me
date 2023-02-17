package ru.practicum.notification;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.subscriptions.EventSubscription;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SubscriptionNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column
    private Long userId;

    @Column
    private Boolean notified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private EventSubscription subscription;

    @Column(nullable = false)
    private LocalDateTime notificationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationMethod notificationMethod;

    @Column(nullable = false)
    private String payload;

}
