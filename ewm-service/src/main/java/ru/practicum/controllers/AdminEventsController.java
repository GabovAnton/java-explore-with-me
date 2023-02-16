package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventService;
import ru.practicum.event.UpdateEventAdminRequest;
import ru.practicum.subscriptions.EventSubscriptionDto;
import ru.practicum.subscriptions.EventSubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventsController {

    private final EventService eventService;

    private final EventSubscriptionService eventSubscriptionService;

    @GetMapping("/notifications/subscriptions/{eventId}")
    List<EventSubscriptionDto> getSubscriptionsByEventId(@PathVariable Long eventId) {

        return eventSubscriptionService.findAllByEventId(eventId);
    }

    @PatchMapping("/notifications/subscriptions/{subscriptionId}")
    EventSubscriptionDto updateSubscription(EventSubscriptionDto eventSubscriptionDTO,
            @PathVariable Long subscriptionId) {

        return eventSubscriptionService.update(subscriptionId, eventSubscriptionDTO);
    }

    @GetMapping()
    List<EventFullDto> searchEvents(@RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<String> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return eventService.searchAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("{eventId}")
    EventFullDto updateAdminEvent(@PathVariable("eventId") Long eventId,
            @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        return eventService.updateAdminEvent(eventId, updateEventAdminRequest);
    }

}
