package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User add(User user);

    Collection<User> getAllUsers();

    Optional<User> getById(Long userId);

    User update(Long userId, User user);

    void deleteById(Long userId);
}
