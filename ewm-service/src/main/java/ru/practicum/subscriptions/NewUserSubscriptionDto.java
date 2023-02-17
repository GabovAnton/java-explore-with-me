package ru.practicum.subscriptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserSubscriptionDto {

    private Long userId;

    private Long subscribedToUserId;

    private Boolean notifyByEmail;

    private Boolean notifyByPortal;

    private Boolean subscribeNewEvents;

    private Boolean subscribeChangeEvents;

    private Boolean subscribeDeleteEvents;

}
