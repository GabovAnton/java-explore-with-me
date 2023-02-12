package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.eventcompilation.CompilationDto;
import ru.practicum.eventcompilation.NewCompilationDto;
import ru.practicum.eventcompilation.UpdateCompilationRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {

    private final EventService eventService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto saveCompilation(@RequestBody NewCompilationDto newCompilationDto) {

        return eventService.saveCompilationAdmin(newCompilationDto);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCompilation(@PathVariable("compId") Long compId) {

        eventService.deleteCompilationAdmin(compId);
    }

    @PatchMapping("{compId}")
    CompilationDto updateCompilation(@PathVariable("compId") Long compId,
            @RequestBody UpdateCompilationRequestDto updateCompilationRequestDto) {

        return eventService.updateCompilationAdmin(compId, updateCompilationRequestDto);
    }

}
