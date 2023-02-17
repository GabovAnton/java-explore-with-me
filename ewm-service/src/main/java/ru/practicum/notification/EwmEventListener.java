package ru.practicum.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.event.Event;
import ru.practicum.event.EventService;
import ru.practicum.subscriptions.EventSubscription;
import ru.practicum.subscriptions.EventSubscriptionService;
import ru.practicum.subscriptions.UserSubscriptionDto;
import ru.practicum.subscriptions.UserSubscriptionsService;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Service
@Slf4j
public class EwmEventListener {

    private final UserSubscriptionsService userSubscriptionsService;

    private final EventService eventService;

    private final EventSubscriptionService eventSubscriptionService;

    private final SubscriptionNotificationRepository subscriptionNotificationRepository;

    private final EmailSenderService emailSenderService;

    private final UserService userService;

    private void createNotification(String payload,
            Long recipientId,
            NotificationType type,
            NotificationMethod method,
            Long eventId,
            EventSubscription eventSubscription) {

        SubscriptionNotification sn = new SubscriptionNotification();
        sn.setPayload(payload);
        sn.setNotificationDate(LocalDateTime.now());
        sn.setUserId(recipientId);
        sn.setNotificationMethod(method);
        if (method.equals(NotificationMethod.EMAIL)) {
            sn.setNotified(true);
        } else {
            sn.setNotified(false);
        }
        sn.setSubscription(eventSubscription);
        sn.setNotificationType(type);
        SubscriptionNotification savedNotification = subscriptionNotificationRepository.save(sn);
        log.debug("new notification for userId: {}  and eventId {}  by {} created: {}",
                recipientId,
                eventId,
                method,
                savedNotification);
    }

    @EventListener
    public void handleEwmContext(EwmServiceEventInformation cse) {

        if (cse.getRecipientIds() != null) {

            for (Long recipientId : cse.getRecipientIds()) {

                EventSubscription eventSubscription = eventSubscriptionService.getEntity(recipientId, cse.getEventId());
                if (eventSubscription.getNotifyChangeEvents() && cse.getEventType().equals(NotificationType.CHANGE) ||
                    eventSubscription.getNotifyNewEvents() && cse.getEventType().equals(NotificationType.NEW) ||
                    eventSubscription.getNotifyDeleteEvents() && cse.getEventType().equals(NotificationType.DELETE)) {

                    Event event = eventService.getEvent(cse.getEventId());
                    UserSubscriptionDto userSubscriptionDto = userSubscriptionsService.get(recipientId,
                            event.getInitiator().getId());

                    if (userSubscriptionDto.getNotifyByPortal() && userSubscriptionDto.getNotifyByEmail()) {

                        createNotification(cse.getMessage(),
                                recipientId,
                                cse.getEventType(),
                                NotificationMethod.PORTAL,
                                event.getId(),
                                eventSubscription);

                        createNotification(cse.getMessage(),
                                recipientId,
                                cse.getEventType(),
                                NotificationMethod.EMAIL,
                                event.getId(),
                                eventSubscription);
                        emailSenderService.sendSimpleEmail(userService.getUser(recipientId).getEmail(),
                                "Уведомление о" + " мероприятии:" + " " + cse.getEventType() + ":" + event.getTitle(),
                                cse.getMessage());

                    } else if (userSubscriptionDto.getNotifyByPortal()) {
                        createNotification(cse.getMessage(),
                                recipientId,
                                cse.getEventType(),
                                NotificationMethod.PORTAL,
                                event.getId(),
                                eventSubscription);
                    } else {
                        createNotification(cse.getMessage(),
                                recipientId,
                                cse.getEventType(),
                                NotificationMethod.EMAIL,
                                event.getId(),
                                eventSubscription);
                    }
                }

            }

        }

    }

}
