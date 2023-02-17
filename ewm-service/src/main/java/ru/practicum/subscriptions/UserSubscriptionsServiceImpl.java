package ru.practicum.subscriptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.EntityNotFoundException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserSubscriptionsServiceImpl implements UserSubscriptionsService {

    private final UserSubscriptionsRepository userSubscriptionsRepository;

    private final UserSubscriptionMapper userSubscriptionMapper;

    private final EventRepository eventRepository;

    private final EventSubscriptionService eventSubscriptionService;

    @Override
    public List<UserSubscriptionDto> findAllSubscriptionByUserId(Long userId) {

        final List<UserSubscription> userSubscription = userSubscriptionsRepository.findByUserId(userId);
        List<UserSubscriptionDto>
                userSubscriptionDtos =
                userSubscription.stream().map(userSubscriptionMapper::toDto).collect(Collectors.toList());
        log.debug(" query for subscriptions by userId: {}  successfully executed, result {}",
                userId,
                userSubscriptionDtos);
        return userSubscriptionDtos;

    }

    @Override
    public UserSubscriptionDto get(Long userId, Long subscribedToUserId) {

        UserSubscriptionDto userSubscriptionDto = userSubscriptionsRepository.findBySubscribedTo_IdAndUserId(
                subscribedToUserId,
                userId).map(userSubscriptionMapper::toDto).orElseThrow(() -> new EntityNotFoundException(
                "user subscription go didn't found"));

        log.debug(" query for subscriptions by userId: {} and friendId: {} successfully executed, result {}",
                userId,
                subscribedToUserId,
                userSubscriptionDto);
        return userSubscriptionDto;
    }

    @Override
    @Transactional
    public UserSubscriptionDto create(Long userId,
            Long subscribedToUserId,
            Boolean notifyByEmail,
            Boolean notifyByPortal,
            Boolean subscribeNewEvents,
            Boolean subscribeChangeEvents,
            Boolean subscribeDeleteEvents) {

        NewUserSubscriptionDto newUserSubscriptionDto = new NewUserSubscriptionDto(userId,
                subscribedToUserId,
                notifyByEmail,
                notifyByPortal,
                subscribeNewEvents,
                subscribeChangeEvents,
                subscribeDeleteEvents);

        List<Event> friendEvents = eventRepository.findByInitiatorId(subscribedToUserId);

        UserSubscription userSubscription = userSubscriptionMapper.fromNew(newUserSubscriptionDto);
        UserSubscriptionDto userSubscriptionDto = userSubscriptionMapper.toDto(userSubscriptionsRepository.save(
                userSubscription));

        log.debug(" new subscriptions for userId: {} and friendId: {} successfully created: {}",
                userId,
                subscribedToUserId,
                userSubscriptionDto);

        for (Event event : friendEvents) {
            event.getEventSubscriptions().add(new EventSubscription(null,
                    userId,
                    LocalDateTime.now(),
                    subscribeNewEvents,
                    subscribeChangeEvents,
                    subscribeDeleteEvents,
                    event,
                    null,
                    subscribedToUserId));
            eventRepository.save(event);
            log.debug(" new subscriptions for userId: {} and friendId: {}  and eventId successfully created: {}",
                    userId,
                    subscribedToUserId,
                    event.getId());
        }
        return userSubscriptionDto;
    }

    @Override
    public UserSubscriptionDto update(Long userId, @Valid UserSubscriptionDto userSubscriptionDTO) {

        UserSubscription existedUserSubscription = userSubscriptionsRepository.findBySubscribedTo_IdAndUserId(
                userSubscriptionDTO.getUserId(),
                userId).orElseThrow(() -> new EntityNotFoundException("user subscription go didn't find"));
        UserSubscription updatedUserSubscription = userSubscriptionMapper.partialUpdate(userSubscriptionDTO,
                existedUserSubscription);
        UserSubscriptionDto userSubscriptionDto = userSubscriptionMapper.toDto(userSubscriptionsRepository.save(
                updatedUserSubscription));

        log.debug("  subscriptions for Userid: {}  and friendId: {} successfully updated, result {}",
                userId,
                userSubscriptionDTO.getSubscribedTo(),
                userSubscriptionDto);
        return userSubscriptionDto;

    }

    @Override
    @Transactional
    public void delete(Long userId, Long subscriptionId) {

        UserSubscription
                userSubscription =
                userSubscriptionsRepository.findById(subscriptionId).orElseThrow(() -> new EntityNotFoundException(
                        "can't find userSubscription id: " + subscriptionId));
        Long friendId = userSubscription.getSubscribedTo().getId();

        Long deletedRows = userSubscriptionsRepository.deleteByUserIdAndId(userId, subscriptionId);

        eventSubscriptionService.deleteEventSubscriptionsByUserAndFriend(userId, friendId);

        if (deletedRows != 1) {

            throw new DataIntegrityViolationException(
                    "<private> can't delete subscription on user id: " + subscriptionId + " and subscriber id: " +
                    userId + "  deleted rows: " + deletedRows +
                    (userSubscriptionsRepository.existsById(subscriptionId) ? "subscription is still exist" :
                     "subscription doesn't still exist"));
        } else {
            log.debug("  subscriptions for subscriptionId: {}   successfully deleted", subscriptionId);
        }
    }

}
