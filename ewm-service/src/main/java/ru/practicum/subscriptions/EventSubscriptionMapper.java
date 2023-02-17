package ru.practicum.subscriptions;

import org.mapstruct.*;
import ru.practicum.event.EventService;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {EventService.class})
public interface EventSubscriptionMapper {

    @Mapping(source = "event", target = "event", qualifiedByName = "getEvent")
    EventSubscription toEntity(EventSubscriptionDto eventSubscriptionDTO);

    @Mapping(source = "event.id", target = "event")
    EventSubscriptionDto toDto(EventSubscription eventSubscription);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "event", target = "event", qualifiedByName = "getEvent")
    EventSubscription partialUpdate(EventSubscriptionDto eventSubscriptionDTO,
            @MappingTarget EventSubscription eventSubscription);

}