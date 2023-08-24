package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.State;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut addBooking(@Valid @RequestBody BookingDtoIn bookingDtoIn,
                                    @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на оформление аренды: {} от пользователя с id: {}", bookingDtoIn, userId);
        return bookingService.addBooking(bookingDtoIn, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                        @PathVariable Long bookingId) {
        log.info("Поступил запрос на просмотр аренды с id: {} от пользователя с id: {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllBookingsByUser(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                            @RequestParam(required = false,
                                                    defaultValue = "ALL") @Valid State state,
                                            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                            @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Поступил запрос на просмотр {} аренд от пользователя с id: {}", state, userId);
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllBookingsForAllItemsByOwner(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                            @RequestParam(required = false,
                                                    defaultValue = "ALL") @Valid State state,
                                            @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                            @RequestParam(required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Поступил запрос на просмотр {} аренд вещей владельца с id: {}", state, userId);
        return bookingService.getAllBookingsForAllItemsByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateBooking(@PathVariable Long bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на обновление статуса аренды с id: {}", bookingId);
        return bookingService.updateBooking(bookingId, approved, userId);
    }
}