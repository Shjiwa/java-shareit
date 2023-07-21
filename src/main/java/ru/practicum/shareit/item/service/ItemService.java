package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto getItemInfo(Long itemId);

    Collection<ItemDto> getOwnerItems(Long userId);

    Collection<ItemDto> getItemsByKeyword(String text);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);
}
