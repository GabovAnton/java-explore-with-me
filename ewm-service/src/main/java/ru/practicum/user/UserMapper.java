package ru.practicum.user;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserDto userDto);

    User fromNewToEntity(NewUserDto newUserRequest);

    UserDto toDto(User user);

}