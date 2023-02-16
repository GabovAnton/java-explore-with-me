package ru.practicum.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SubscriptionNotificationRepository extends JpaRepository<SubscriptionNotification, Long> {

    @Transactional
    @Modifying
    @Query("update SubscriptionNotification s set s.notified = ?1 where s.id = ?2")
    int updateNotifiedById(Boolean notified, Long id);

}