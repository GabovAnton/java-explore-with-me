package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.NewUserDto;
import ru.practicum.user.UserDto;
import ru.practicum.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminUsersController {

    private final UserService userService;

    @GetMapping("/admin/users")
    List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto registerUser(@RequestBody NewUserDto newUserRequest) {

        return userService.registerUser(newUserRequest);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("userId") Long userId) {

        userService.delete(userId);
    }

}
