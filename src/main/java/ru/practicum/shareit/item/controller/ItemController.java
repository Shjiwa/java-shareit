package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на добавление вещи {} от пользователя: {}", itemDto, userId);
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId) {
        log.info("Поступил запрос на добавление отзыва к товару с id: {}, от пользователя с id: {}, comment: {}",
                itemId, userId, commentDto);
        return itemService.addComment(commentDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getInfo(@PathVariable Long itemId, @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на получение информации о вещи с id: {} от пользователя с id: {}", itemId, userId);
        return itemService.getItemInfo(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getOwnerItems(@RequestHeader(OWNER_ID_HEADER) Long userId) {
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
                          @RequestHeader(OWNER_ID_HEADER) Long userID,
                          @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на обновление/изменение вещи: {} от пользователя с id: {}", itemDto, userID);
        return itemService.update(itemId, userID, itemDto);
    }
}
