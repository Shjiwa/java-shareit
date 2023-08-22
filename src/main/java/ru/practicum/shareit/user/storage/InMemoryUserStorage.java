package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public User add(User user) {
        //isEmailBusy(user);
        if (emails.contains(user.getEmail())) {
            throw new ConflictException("Email is busy.");
        }
        Long id = getNextId();
        user.setId(id);
        emails.add(user.getEmail());
        users.put(id, user);
        log.info("Пользователь {} создан!", user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User update(Long id, User user) {
        //isEmailBusy(user);
        User updatedUser = users.get(id);
        if (!user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }
        if (!user.getEmail().isBlank() || user.getEmail() != null) {
            if (!updatedUser.getEmail().equals(user.getEmail())) {
                if (emails.contains(user.getEmail())) {
                    throw new ConflictException("Email is busy.");
                }
                emails.remove(updatedUser.getEmail());
                updatedUser.setEmail(user.getEmail());
                emails.add(updatedUser.getEmail());

            }
        }
        users.put(id, updatedUser);
        log.info("Пользователь {} обновлен", updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    private void isEmailBusy(User user) {
        boolean isBusy = users.values().stream()
                .filter(user1 -> !Objects.equals(user1.getId(), user.getId()))
                .anyMatch(user1 -> Objects.equals(user1.getEmail(), user.getEmail())
                );
        if (isBusy) {
            throw new ConflictException("Email is busy.");
        }
    }
}