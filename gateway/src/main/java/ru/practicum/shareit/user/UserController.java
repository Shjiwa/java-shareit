package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Поступил запрос на добавление пользователя: {}", userDto);
        return userClient.create(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Поступил запрос на получение пользователя с  id = {}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Поступил запрос на получение списка всех пользователей");
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Поступил запрос на обновление пользователя с id = {}, данные на обновление: {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Поступил запрос на удаление пользователя с id = {}", userId);
        userClient.deleteById(userId);
    }
}
