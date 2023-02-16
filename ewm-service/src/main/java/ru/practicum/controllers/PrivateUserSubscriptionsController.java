package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.subscriptions.UserSubscriptionDto;
import ru.practicum.subscriptions.UserSubscriptionsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrivateUserSubscriptionsController {

    private final UserSubscriptionsService userSubscriptionsService;

    @GetMapping("/users/{userId}/userSubscription/{subscribedToUserId}")
    UserSubscriptionDto getSubscription(@PathVariable("userId") Long userId,
            @PathVariable("subscribedToUserId") Long subscribedToUserId) {

        return userSubscriptionsService.get(userId, subscribedToUserId);
    }

    @GetMapping("/users/{userId}/userSubscription/all")
    List<UserSubscriptionDto> getAllSubscriptions(@PathVariable("userId") Long userId) {

        return userSubscriptionsService.findAllSubscriptionByUserId(userId);
    }

    @PostMapping("/users/{userId}/userSubscription/{subscribedToUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    UserSubscriptionDto addSubscription(@PathVariable("userId") Long userId,
            @PathVariable Long subscribedToUserId,
            @RequestParam(value = "notifyByEmail") Boolean notifyByEmail,
            @RequestParam(value = "notifyByPortal",
                    required = false,
                    defaultValue = "false") Boolean notifyByPortal,
            @RequestParam(value = "subscribeNewEvents",
                    required = false,
                    defaultValue = "false") Boolean subscribeNewEvents,
            @RequestParam(value = "subscribeChangeEvents",
                    required = false,
                    defaultValue = "false") Boolean subscribeChangeEvents,
            @RequestParam(value = "subscribeDeleteEvents",
                    required = false,
                    defaultValue = "false") Boolean subscribeDeleteEvents) {

        return userSubscriptionsService.create(userId,
                subscribedToUserId,
                notifyByEmail, notifyByPortal,
                subscribeNewEvents,
                subscribeChangeEvents,
                subscribeDeleteEvents);
    }

    @PatchMapping("/users/{userId}/userSubscription")
    UserSubscriptionDto updateSubscription(@PathVariable("userId") Long userId,
            @RequestBody UserSubscriptionDto userSubscriptionDTO) {

        return userSubscriptionsService.update(userId, userSubscriptionDTO);
    }

    @DeleteMapping("/users/{userId}/userSubscription/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSubscription(@PathVariable("userId") Long userId, @PathVariable("subscriptionId") Long subscriptionId) {

        userSubscriptionsService.delete(userId, subscriptionId);
    }

}
