package ru.practicum.event;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.user.User;
import ru.practicum.user.UserShortDto;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.CategoryServiceImpl;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {CategoryServiceImpl.class, LocationMapper.class})
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Named("initiatorToShortDto")
    static UserShortDto initiatorToShortDto(User initiator) {

        return new UserShortDto(initiator.getId(), initiator.getName());
    }

    @Named("mapCategoryToCategoryDto")
    static CategoryDto mapCategoryToCategoryDto(Category category) {

        return new CategoryDto(category.getId(), category.getName());
    }

    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryToCategoryDto")
    EventShortDto toShortDto(Event event);

    @Mapping(source = "category", target = "category", qualifiedByName = "getCategory")
    Event fromNewEventDtoToEntity(NewEventDto newEventDto);

    Event toEntity(EventFullDto eventFullDto);

    @Mapping(source = "initiator", target = "initiator", qualifiedByName = "initiatorToShortDto")
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryToCategoryDto")
    @Mapping(source = "eventStatus", target = "state")
    EventFullDto toDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "category", target = "category", qualifiedByName = "getCategory")
    Event partialUpdateAdmin(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "category", target = "category", qualifiedByName = "getCategory")
    Event partialUpdateUser(UpdateEventUserRequestDto updateEventUserRequestDto, @MappingTarget Event event);

}