package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemResponseWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemResponseDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        ItemRequest itemRequest = itemRequestRepository.save(
                ItemRequestMapper.INSTANCE.toItemRequest(itemRequestDto, user));
        log.info("Success! Request: {} successfully created!", itemRequestDto);
        return ItemRequestMapper.INSTANCE.toResponseDto(itemRequest);
    }

    @Override
    public Collection<ItemResponseWithItemsDto> getAllByRequesterId(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdOrderByCreated(userId, PageRequest.of(from / size, size));
        return addItems(itemRequests);
    }

    @Override
    public Collection<ItemResponseWithItemsDto> getAll(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdNot(userId, PageRequest.of(from / size, size));
        return addItems(itemRequests);
    }

    @Override
    public ItemResponseWithItemsDto getById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found."));
        ItemResponseWithItemsDto withItemsDto = ItemRequestMapper.INSTANCE.toResponseWithItems(itemRequest);
        withItemsDto.setItems(ItemMapper.INSTANCE.toItemDtoList(itemRepository.findAllByRequestId(requestId)));
        return withItemsDto;
    }

    private List<ItemResponseWithItemsDto> addItems(List<ItemRequest> itemRequests) {
        List<Long> ids = itemRequests.stream()
                .map(ItemRequest::getRequester)
                .map(User::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestRequesterIdIn(ids);
        return itemRequests.stream().map(itemRequest -> {
            List<Item> items1 = items.stream()
                    .filter(item -> item.getRequest().getRequester().getId().equals(itemRequest.getRequester().getId()))
                    .collect(Collectors.toList());
            ItemResponseWithItemsDto withItemsDto = ItemRequestMapper.INSTANCE.toResponseWithItems(itemRequest);
            withItemsDto.setItems(ItemMapper.INSTANCE.toItemDtoList(items1));
            return withItemsDto;
        }).collect(Collectors.toList());
    }
}
