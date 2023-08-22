package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemResponseWithItemsDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemResponseDto create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemResponseWithItemsDto> getAllByRequesterId(Long userId, int from, int size);

    Collection<ItemResponseWithItemsDto> getAll(Long userId, int from, int size);

    ItemResponseWithItemsDto getById(Long userId, Long requestId);
}
