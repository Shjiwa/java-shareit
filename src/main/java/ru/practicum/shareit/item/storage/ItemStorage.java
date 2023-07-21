package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Item add(Item item);

    Optional<Item> getItemInfo(Long itemId);

    Collection<Item> getAllItems();

    Collection<Item> getItemsByKeyword(String text);

    Item update(Long itemId, Long userId, Item updateItem);
}
