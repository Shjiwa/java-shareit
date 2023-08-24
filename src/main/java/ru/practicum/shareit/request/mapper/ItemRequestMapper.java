package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemResponseWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemResponseDto toResponseDto(ItemRequest itemRequest);

    ItemResponseWithItemsDto toResponseWithItems(ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", source = "requester")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester);

    List<ItemResponseDto> toItemResponseDtoList(List<ItemRequest> itemRequests);
}
