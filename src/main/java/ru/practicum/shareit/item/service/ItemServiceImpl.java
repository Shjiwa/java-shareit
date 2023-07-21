package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        User user = userStorage.getById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Item item = ItemMapper.toItem(itemDto, user, null);
        Item newItem = itemStorage.add(item);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto getItemInfo(Long itemId) {
        return itemStorage.getItemInfo(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found."));
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Long userId) {
        return itemStorage.getAllItems().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public Collection<ItemDto> getItemsByKeyword(String text) {
        return itemStorage.getItemsByKeyword(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()
                );
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Item itemFromStorage = itemStorage.getItemInfo(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found."));
        itemDto.setId(itemId);
        itemDto.setName(itemDto.getName() == null ?
                itemFromStorage.getName() :
                itemDto.getName()
        );
        itemDto.setDescription(itemDto.getDescription() == null ?
                itemFromStorage.getDescription() :
                itemDto.getDescription()
        );
        itemDto.setAvailable(itemDto.getAvailable() == null ?
                itemFromStorage.getAvailable() :
                itemDto.getAvailable()
        );
        if (isValid(itemDto)) {
            Item item = ItemMapper.toItem(itemDto, user, null);
            return ItemMapper.toItemDto(itemStorage.update(itemId, userId, item));
        } else {
            throw new BadRequestException("Invalid data to update.");
        }
    }

    private boolean isValid(ItemDto itemDto) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        return violations.isEmpty();
    }
}
