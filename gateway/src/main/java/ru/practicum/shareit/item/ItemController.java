package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.validation.Valid;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на добавление вещи {} от пользователя: {}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Поступил запрос на добавление отзыва к товару с id: {}, от пользователя с id: {}, comment: {}",
                itemId, userId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getInfo(@PathVariable Long itemId, @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на получение информации о вещи с id: {} от пользователя с id: {}", itemId, userId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на получение списка всех вещей пользователя с id: {}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByKeyWord(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                    @RequestParam String text) {
        log.info("Поступил запрос на поиск по тексту: {}", text);
        return itemClient.search(userId, text);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(OWNER_ID_HEADER) Long userID,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на обновление/изменение вещи: {} от пользователя с id: {}", itemDto, userID);
        return itemClient.update(userID, itemId, itemDto);
    }
}