package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        if (isValid(userDto)) {
            try {
                User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
                log.info("Success! User: {} successfully added!", user);
                return UserMapper.INSTANCE.toUserDto(user);
            } catch (DataIntegrityViolationException e) {
                throw new ConflictException(e.getMessage());
            }
        } else {
            throw new BadRequestException("Data is not valid.");
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.INSTANCE.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."))
        );
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userFromStorage = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        userDto.setId(userId);
        userDto.setName(userDto.getName() == null ? userFromStorage.getName() : userDto.getName());
        userDto.setEmail(userDto.getEmail() == null ? userFromStorage.getEmail() : userDto.getEmail());
        if (isValid(userDto)) {
            try {
                User user = userRepository.save(UserMapper.INSTANCE.toUserWithId(userDto));
                log.info("Success! User: {} successfully updated!", user);
                return UserMapper.INSTANCE.toUserDto(user);
            } catch (Exception e) {
                throw new ConflictException(e.getMessage());
            }
        } else {
            throw new BadRequestException("Invalid data to update.");
        }
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
        log.info("User with id: {} successfully deleted!", userId);
    }

    private boolean isValid(UserDto userDto) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        return violations.isEmpty();
    }
}