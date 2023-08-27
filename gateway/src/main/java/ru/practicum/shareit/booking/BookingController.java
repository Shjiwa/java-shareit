package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.constant.State;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                             @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Поступил запрос на оформление аренды: {} от пользователя с id: {}", bookingDtoIn, userId);
        return bookingClient.create(userId, bookingDtoIn);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Поступил запрос на просмотр аренды с id: {} от пользователя с id: {}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUser(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL")
                                                           @Valid State state,
                                                       @RequestParam(required = false, defaultValue = "0")
                                                           @Min(0) int from,
                                                       @RequestParam(required = false, defaultValue = "20")
                                                           @Min(1) int size) {
        log.info("Поступил запрос на просмотр {} аренд от пользователя с id: {}", state, userId);
        return bookingClient.getAllByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForAllItemsByOwner(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                                   @RequestParam(required = false, defaultValue = "ALL")
                                                                       @Valid State state,
                                                                   @RequestParam(required = false, defaultValue = "0")
                                                                       @Min(0) int from,
                                                                   @RequestParam(required = false, defaultValue = "20")
                                                                       @Min(1) int size) {
        log.info("Поступил запрос на просмотр {} аренд вещей владельца с id: {}", state, userId);
        return bookingClient.getAllByStateOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean isApproved) {
        log.info("Поступил запрос на обновление статуса аренды с id: {}", bookingId);
        return bookingClient.update(userId, bookingId, isApproved);
    }
}