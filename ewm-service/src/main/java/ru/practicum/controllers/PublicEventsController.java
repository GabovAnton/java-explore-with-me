package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventService;
import ru.practicum.event.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventsController {

    private final EventService eventService;

    @GetMapping()
    List<EventShortDto> getEventsPublic(@RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        return eventService.getEventsPublic(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request.getRemoteAddr(),
                request.getRequestURI());
    }

    @GetMapping("/{id}")
    EventFullDto getEventPublic(@PathVariable("id") Long id, HttpServletRequest request) {

        return eventService.getEventPublic(id, request.getRemoteAddr(), request.getRequestURI());
    }

}
