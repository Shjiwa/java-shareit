package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    ItemDto getItemInfo(Long itemId, Long userId);

    Collection<ItemDto> getOwnerItems(Long userId);

    Collection<ItemDto> getItemsByKeyword(String text);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);
}
