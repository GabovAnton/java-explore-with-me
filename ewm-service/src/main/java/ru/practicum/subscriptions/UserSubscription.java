package ru.practicum.subscriptions;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.user.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column
    private Long userId;

    @Column
    private Boolean notifyByEmail;

    @Column(name = "notify_by_portal")
    private Boolean notifyByPortal;

    @Column
    private Boolean subscribeNewEvents;

    @Column
    private Boolean subscribeChangeEvents;

    @Column
    private Boolean subscribeDeleteEvents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_on_user_id", nullable = false)
    private User subscribedTo;

}
