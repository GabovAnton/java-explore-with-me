package ru.practicum.subscriptions;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserSubscriptionDto {

    private Long id;

    private Long userId;

    private Boolean notifyByEmail;

    private Boolean notifyByPortal;

    private Boolean subscribeNewEvents;

    private Boolean subscribeChangeEvents;

    private Boolean subscribeDeleteEvents;

    @NotNull
    private Long subscribedTo;

}