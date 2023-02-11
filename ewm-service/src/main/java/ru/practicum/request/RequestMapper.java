package ru.practicum.request;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.event.EventService;
import ru.practicum.user.UserService;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {UserService.class, EventService.class})
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "requester", target = "requester", qualifiedByName = "getUser")
    @Mapping(source = "event", target = "event", qualifiedByName = "getEvent")
    Request toEntity(ParticipationRequestDto participationRequestDto);

    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "event.id", target = "event")
    ParticipationRequestDto toDto(Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "requester", target = "requester", qualifiedByName = "getUser")
    @Mapping(source = "event", target = "event", qualifiedByName = "getEvent")
    Request partialUpdate(ParticipationRequestDto participationRequestDto, @MappingTarget Request request);

}