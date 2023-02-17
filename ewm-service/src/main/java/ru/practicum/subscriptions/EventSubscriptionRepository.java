package ru.practicum.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventSubscriptionRepository extends JpaRepository<EventSubscription, Long> {

    Optional<EventSubscription> findByEvent_IdAndSubscriberId(Long eventId, Long subscriberId);

    List<EventSubscription> findByEvent_Id(Long id);

    List<EventSubscription> findBySubscriberIdAndEventInitiatorId(Long subscriberId, Long eventInitiatorId);

    long deleteBySubscriberIdAndEventInitiatorId(Long subscriberId, Long eventInitiatorId);

}
