package ru.practicum.event;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface LocationMapper {

    Location toEntity(LocationDto locationDto);

    LocationDto toDto(Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Location partialUpdate(LocationDto locationDto, @MappingTarget Location location);

}