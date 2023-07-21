package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.add(UserMapper.toUser(userDto)));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."))
        );
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userFromStorage = userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        userDto.setId(userId);
        userDto.setName(userDto.getName() == null ? userFromStorage.getName() : userDto.getName());
        userDto.setEmail(userDto.getEmail() == null ? userFromStorage.getEmail() : userDto.getEmail());
        if (isValid(userDto)) {
            return UserMapper.toUserDto(userStorage.update(userId, UserMapper.toUser(userDto)));
        } else {
            throw new BadRequestException("Invalid data to update.");
        }
    }

    @Override
    public void deleteById(Long userId) {
        userStorage.deleteById(userId);
    }

    private boolean isValid(UserDto userDto) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        return violations.isEmpty();
    }
}
