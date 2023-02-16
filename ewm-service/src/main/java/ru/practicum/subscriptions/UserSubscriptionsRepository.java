package ru.practicum.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionsRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findBySubscribedTo_IdAndUserId(Long subscribedToId, Long userId);

    Long deleteByUserIdAndId(Long userId, Long subscribedTo);

    List<UserSubscription> findByUserId(Long userId);

}