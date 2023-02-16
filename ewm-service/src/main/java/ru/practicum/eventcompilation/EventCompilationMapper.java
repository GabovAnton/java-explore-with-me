package ru.practicum.eventcompilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventShortDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {EventMapper.class})
public interface EventCompilationMapper {

    @Named("eventsToEventShortDto")
    static List<EventShortDto> eventsToEventShortDto(Set<Event> events) {

        return events.stream().map(EventMapper.INSTANCE::toShortDto).collect(Collectors.toList());

    }

    @Mapping(source = "eventsCompilationGroupEvents", target = "events", qualifiedByName = "eventsToEventShortDto")
    CompilationDto compilationToDto(EventCompilation eventCompilation);

}





