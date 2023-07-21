package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ExceptionService;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final ExceptionService exceptionService;
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public Item add(Item item) {
        Long id = getNextId();
        item.setId(id);
        items.put(id, item);
        log.info("Предмет {} добавлен!", item);
        return item;
    }

    @Override
    public Optional<Item> getItemInfo(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values();
    }

    @Override
    public Collection<Item> getItemsByKeyword(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        String textToLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(textToLowerCase)) ||
                        (item.getDescription().toLowerCase().contains(textToLowerCase)))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Long itemId, Long userId, Item updateItem) {
        Item item = items.get(itemId);
        if (item == null || !Objects.equals(item.getOwner().getId(), userId)) {
            exceptionService.throwNotFound("Item not found.");
        }
        if (item.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (item.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (item.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        items.put(itemId, item);
        log.info("Предмет {} обновлен!", item);
        return item;
    }
}
