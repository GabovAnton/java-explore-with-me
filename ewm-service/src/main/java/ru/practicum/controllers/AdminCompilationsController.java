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
public class AdminCompilationsController {

    private final EventService eventService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto saveCompilation(@RequestBody NewCompilationDto newCompilationDto) {

        return eventService.saveCompilationAdmin(newCompilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCompilation(@PathVariable("compId") Long compId) {

        eventService.deleteCompilationAdmin(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    CompilationDto updateCompilation(@PathVariable("compId") Long compId,
            @RequestBody UpdateCompilationRequestDto updateCompilationRequestDto) {

        return eventService.updateCompilationAdmin(compId, updateCompilationRequestDto);
    }

}
