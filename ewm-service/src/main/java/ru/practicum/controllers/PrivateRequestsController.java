package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.ParticipationRequestDto;
import ru.practicum.request.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrivateRequestsController {

    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") Long userId) {

        return requestService.getUserRequests(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto addParticipationRequest(@PathVariable("userId") Long userId,
            @RequestParam(value = "eventId") Long eventId) {

        return requestService.addParticipationRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId) {

        return requestService.cancelRequest(userId, requestId);
    }

}
