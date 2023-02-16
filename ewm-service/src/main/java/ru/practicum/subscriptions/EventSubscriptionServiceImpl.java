package ru.practicum.subscriptions;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.notification.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventSubscriptionServiceImpl implements EventSubscriptionService {

    private final SubscriptionNotificationRepository subscriptionNotificationRepository;

    private final EventSubscriptionMapper eventSubscriptionMapper;

    private final SubscriptionNotificationMapper subscriptionNotificationMapper;

    private final EventSubscriptionRepository eventSubscriptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventSubscriptionDto> findAllByEventId(Long eventId) {

        final List<EventSubscription> eventSubscriptions = eventSubscriptionRepository.findByEvent_Id(eventId);
        return eventSubscriptions.stream().map(eventSubscriptionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventSubscriptionDto get(Long userId, Long eventId) {

        return eventSubscriptionRepository
                .findByEvent_IdAndSubscriberId(eventId, userId)
                .map(eventSubscriptionMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "event subscription didn't find for user id: " + userId + " and event id:" + eventId));
    }

    @Override
    public EventSubscriptionDto create(@Valid EventSubscriptionDto eventSubscriptionDTO) {

        EventSubscription eventSubscription = eventSubscriptionMapper.toEntity(eventSubscriptionDTO);
        return eventSubscriptionMapper.toDto(eventSubscriptionRepository.save(eventSubscription));
    }

    @Override
    public EventSubscriptionDto update(Long id, @Valid EventSubscriptionDto eventSubscriptionDTO) {

        final EventSubscription
                eventSubscription =
                eventSubscriptionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                        "event subscription didn't find with id: " + id));
        EventSubscription UpdatedEventSubscription = eventSubscriptionMapper.partialUpdate(eventSubscriptionDTO,
                eventSubscription);
        return eventSubscriptionMapper.toDto(eventSubscriptionRepository.save(UpdatedEventSubscription));
    }

    @Override
    public EventSubscription getEntity(Long userId, Long eventId) {

        return eventSubscriptionRepository
                .findByEvent_IdAndSubscriberId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "event subscription didn't find for user id: " + userId + " and event id:" + eventId));
    }

    @Override
    @Transactional
    public void deleteEventSubscriptionsByUserAndFriend(Long userId, Long friendId) {

        eventSubscriptionRepository.deleteBySubscriberIdAndEventInitiatorId(userId, friendId);
    }

    @Override
    public void delete(final Long id) {

        eventSubscriptionRepository.deleteById(id);
    }

    @Override
    public List<SubscriptionNotificationDto> searchNotifications(Long userId,
            Long eventId,
            String start,
            String end,
            NotificationMethod method,
            NotificationType type,
            boolean notified,
            Integer from,
            Integer size) {

        QSubscriptionNotification qNotification = QSubscriptionNotification.subscriptionNotification;
        JPAQuery<SubscriptionNotification> query = new JPAQuery<>(entityManager);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qNotification.userId.eq(userId));
        builder.and(qNotification.notified.eq(notified));
        if (eventId != null) {
            builder.and(qNotification.subscription.event.id.eq(eventId));
        }
        if (StringUtils.isNoneBlank(start)) {
            builder.and(qNotification.notificationDate.after(stringToDate(start)));
        }
        if (StringUtils.isNoneBlank(end)) {
            builder.and(qNotification.notificationDate.before(stringToDate(end)));
        }
        if (method != null) {
            builder.and(qNotification.notificationMethod.eq(method));
        }
        if (type != null) {
            builder.and(qNotification.notificationType.eq(type));
        }
        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = subscriptionNotificationRepository.count() + 1;
        List<SubscriptionNotificationDto> notifications = query
                .from(qNotification)
                .where(builder)
                .limit(size != null ? size : totalItems)
                .offset(offset)
                .fetch()
                .stream()
                .map(subscriptionNotificationMapper::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        log.debug("<private> query for notifications successfully executed, result {}", notifications);

        return notifications;
    }

    @Override
    public List<SubscriptionNotificationDto> getNotificationsForSubscription(Long userId, Long friendId) {

        QSubscriptionNotification qNotification = QSubscriptionNotification.subscriptionNotification;
        JPAQuery<SubscriptionNotification> query = new JPAQuery<>(entityManager);
        List<SubscriptionNotification> all = subscriptionNotificationRepository.findAll();
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qNotification.userId.eq(userId));
        builder.and(qNotification.subscription.eventInitiatorId.eq(friendId));
        List<SubscriptionNotificationDto> notifications = query.from(qNotification).where(builder).fetch().stream().map(
                subscriptionNotificationMapper::toDto).collect(Collectors.collectingAndThen(Collectors.toList(),
                Collections::unmodifiableList));
        changeNotificationStatus(notifications
                .stream()
                .map(SubscriptionNotificationDto::getId)
                .collect(Collectors.toList()));
        log.debug("<private> query for notifications by userId: {} and friendId: {} successfully executed, result {}",
                userId,
                friendId,
                notifications);
        return notifications;

    }

    @Override
    public List<EventSubscription> findBySubscriberIdAndEventInitiatorId(Long userId, Long friendId) {

        return eventSubscriptionRepository.findBySubscriberIdAndEventInitiatorId(userId, friendId);
    }

    private void changeNotificationStatus(List<Long> ids) {

        for (Long id : ids) {
            if (subscriptionNotificationRepository.updateNotifiedById(true, id) > 0) {
                log.debug("<private>  notifications with id {} successfully updated to status: notified=true", id);
            } else {
                throw new DataIntegrityViolationException("can't update status in notification id: " + id);
            }

        }
    }

    private LocalDateTime stringToDate(String dateTime) {

        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        } catch (DateTimeParseException e) {
            throw new BadRequestException("can't parse dateTime from string: " + dateTime);
        }

    }

}
