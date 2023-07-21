package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос на добавление вещи от пользователя: {}", userId);
        return itemService.add(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getInfo(@PathVariable Long itemId) {
        log.info("Поступил запрос на получение информации о вещи с id: {}", itemId);
        return itemService.getItemInfo(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил запрос на получение списка всех вещей пользователя с id: {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsByKeyWord(@RequestParam String text) {
        log.info("Поступил запрос на поиск по тексту: {}", text);
        return itemService.getItemsByKeyword(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userID,
                          @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на обновление/изменение вещи: {} от пользователя с id: {}", itemDto, userID);
        return itemService.update(itemId, userID, itemDto);
    }
}
