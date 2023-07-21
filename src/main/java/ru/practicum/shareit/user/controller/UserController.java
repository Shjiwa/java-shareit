package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ExceptionService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ExceptionService exceptionService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            exceptionService.throwBadRequest("Email is blank/null.");
        }
        log.info("Поступил запрос на добавление пользователя: {}", userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Поступил запрос на обновление пользователя с id = {}, данные на обновление: {}", userId, userDto);
        return userService.update(userId, userDto);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Поступил запрос на получение списка всех пользоваетелей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Поступил запрос на получение пользователя с  id = {}", userId);
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Поступил запрос на удаление пользователя с id = {}", userId);
        userService.deleteById(userId);
    }
}
