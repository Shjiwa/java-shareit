package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto add(UserDto userDto);

    Collection<UserDto> getAllUsers();

    UserDto getById(Long userId);

    UserDto update(Long userId, UserDto userDto);

    void deleteById(Long userId);
}
