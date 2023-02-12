package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.eventcompilation.CompilationDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {

    private final EventService eventService;

    @GetMapping()
    List<CompilationDto> getCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return eventService.getCompilationsPublic(pinned, from, size);
    }

    @GetMapping("{compId}")
    CompilationDto getCompilation(@PathVariable("compId") Long compId) {

        return eventService.getCompilationPublic(compId);
    }

}
