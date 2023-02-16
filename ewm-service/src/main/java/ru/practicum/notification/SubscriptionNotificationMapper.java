package ru.practicum.notification;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SubscriptionNotificationMapper {

    @Mapping(source = "subscription.id", target = "subscription")
    SubscriptionNotificationDto toDto(SubscriptionNotification subscriptionNotification);

}