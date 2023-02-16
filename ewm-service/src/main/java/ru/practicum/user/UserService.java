package ru.practicum.user;

import org.mapstruct.Named;

import javax.validation.Valid;
import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto registerUser(@Valid NewUserDto newUserRequest);

    void delete(Long id);

    @Named("getUser")
    User getUser(Long id);

}
