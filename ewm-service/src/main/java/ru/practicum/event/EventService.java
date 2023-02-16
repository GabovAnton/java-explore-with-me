package ru.practicum.event;

import org.mapstruct.Named;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.eventcompilation.CompilationDto;
import ru.practicum.eventcompilation.NewCompilationDto;
import ru.practicum.eventcompilation.UpdateCompilationRequestDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

public interface EventService {

    @Named("getEvent")
    Event getEvent(Long id);

    List<EventFullDto> searchAdminEvents(@Valid List<Long> users,
            @Valid List<String> states,
            @Valid List<Long> categories,
            @Valid String rangeStart,
            @Valid String rangeEnd,
            @Valid Integer from,
            @Valid Integer size);

    EventFullDto updateEvent(Event event);

    List<EventShortDto> getEventsPrivate(Long userId, @Valid Integer from, @Valid Integer size);

    List<EventShortDto> getEventsPublic(@Valid String text,
            @Valid List<Long> categories,
            @Valid Boolean paid,
            @Valid String rangeStart,
            @Valid String rangeEnd,
            @Valid Boolean onlyAvailable,
            @Valid String sort,
            @Valid Integer from,
            @Valid Integer size,
            @Valid String clientIp,
            @Valid String endpointPath);

    EventFullDto getEventPublic(Long id, String clientIp, String endpointPath);

    EventFullDto updateEventPrivate(Long userId,
            Long eventId,
            @Valid UpdateEventUserRequestDto updateEventUserRequestDto);

    EventFullDto addEventPrivate(Long userId, @Valid NewEventDto newEventDto);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto updateAdminEvent(Long eventId, @Valid UpdateEventAdminRequest updateEventAdminRequest);

    @Named("eventIdsToEvents")
    Set<Event> eventIdsToEvents(Set<Long> ids);

    @Transactional
    CompilationDto saveCompilationAdmin(@Valid NewCompilationDto newCompilationDto);

    void deleteCompilationAdmin(Long compId);

    @Transactional
    CompilationDto updateCompilationAdmin(Long compId, @Valid UpdateCompilationRequestDto updateCompilationRequestDto);

    List<CompilationDto> getCompilationsPublic(@Valid Boolean pinned, @Valid Integer from, @Valid Integer size);

    CompilationDto getCompilationPublic(Long compId);

}
