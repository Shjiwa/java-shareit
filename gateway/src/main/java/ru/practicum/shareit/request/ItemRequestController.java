package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Поступил запрос на создание запроса от пользоватля с id: {}, текст запроса: {}",
                userId, itemRequestDto.getDescription());
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequesterId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                      @RequestParam(required = false, defaultValue = "0")
                                                          @Min(0) int from,
                                                      @RequestParam(required = false, defaultValue = "20")
                                                          @Min(1) int size) {
        log.info("Поступил запрос на получение запросов, созданых пользователем с id: {}", userId);
        return itemRequestClient.getAllByRequesterId(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Поступил запрос на получение запросов от пользователя с id: {}", userId);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                          @PathVariable Long requestId) {
        log.info("Поступил запрос на получение запроса с id {} от пользователя с id: {}", requestId, userId);
        return itemRequestClient.getById(userId, requestId);
    }
}
