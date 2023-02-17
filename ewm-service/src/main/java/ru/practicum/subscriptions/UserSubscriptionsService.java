package ru.practicum.subscriptions;

import javax.validation.Valid;
import java.util.List;

public interface UserSubscriptionsService {

    List<UserSubscriptionDto> findAllSubscriptionByUserId(Long userId);

    UserSubscriptionDto get(Long userId, Long subscribedToUserId);

    UserSubscriptionDto create(Long userId,
            Long subscribedToUserId,
            Boolean notifyByEmail,
            Boolean notifyByMessengers,
            Boolean subscribeNewEvents,
            Boolean subscribeChangeEvents,
            Boolean subscribeDeleteEvents);

    UserSubscriptionDto update(Long userId, @Valid UserSubscriptionDto userSubscriptionDTO);

    void delete(Long userId, Long subscribedToUserId);

}
