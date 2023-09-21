package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemResponseWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.Collection;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemResponseDto createRequest(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Поступил запрос на создание запроса от пользоватля с id: {}, текст запроса: {}",
                userId, itemRequestDto.getDescription());
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemResponseWithItemsDto> getAllByRequesterId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                              @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Поступил запрос на получение запросов, созданых пользователем с id: {}", userId);
        return itemRequestService.getAllByRequesterId(userId, from, size);
    }

    @GetMapping("/all")
    public Collection<ItemResponseWithItemsDto> getAll(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                              @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Поступил запрос на получение запросов от пользователя с id: {}", userId);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemResponseWithItemsDto getById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                            @PathVariable Long requestId) {
        log.info("Поступил запрос на получение запроса с id {} от пользователя с id: {}", requestId, userId);
        return itemRequestService.getById(userId, requestId);
    }
}
