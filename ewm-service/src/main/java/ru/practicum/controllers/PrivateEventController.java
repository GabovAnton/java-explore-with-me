package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.*;
import ru.practicum.request.EventRequestStatusUpdateRequest;
import ru.practicum.request.EventRequestStatusUpdateResult;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    private final RequestService requestService;

    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return eventService.getEventsPrivate(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addEvent(@PathVariable("userId") Long userId, @RequestBody NewEventDto newEventDto) {

        return eventService.addEventPrivate(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {

        return eventService.getEventPrivate(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto updateEvent(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {

        return eventService.updateEventPrivate(userId, eventId, updateEventUserRequestDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getEventParticipantsRequests(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {

        return requestService.getEventParticipantRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResult changeRequestStatus(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        return requestService.changeRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
