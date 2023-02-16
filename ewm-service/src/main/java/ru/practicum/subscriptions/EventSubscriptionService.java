package ru.practicum.subscriptions;

import ru.practicum.notification.NotificationMethod;
import ru.practicum.notification.NotificationType;
import ru.practicum.notification.SubscriptionNotificationDto;

import javax.validation.Valid;
import java.util.List;

public interface EventSubscriptionService {

    List<EventSubscriptionDto> findAllByEventId(Long eventId);

    EventSubscriptionDto get(Long userId, Long eventId);

    EventSubscriptionDto create(@Valid EventSubscriptionDto eventSubscriptionDTO);

    EventSubscriptionDto update(Long id, @Valid EventSubscriptionDto eventSubscriptionDTO);

    EventSubscription getEntity(Long userId, Long eventId);

    void deleteEventSubscriptionsByUserAndFriend(Long userId, Long friendId);

    void delete(Long id);

    List<SubscriptionNotificationDto> searchNotifications(Long userId,
            Long eventId,
            String start,
            String end,
            NotificationMethod method,
            NotificationType type,
            boolean notified,
            Integer from,
            Integer size);

    List<SubscriptionNotificationDto> getNotificationsForSubscription(Long userId, Long friendId);

    List<EventSubscription> findBySubscriberIdAndEventInitiatorId(Long userId, Long friendId);

}
