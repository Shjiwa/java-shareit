package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;
import static ru.practicum.shareit.constant.State.*;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Validated
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
                                                    @RequestParam(defaultValue = ALL) String state) {
        log.info("Поступил запрос на просмотр {} аренд от пользователя с id: {}", state, userId);
        return bookingService.getAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllBookingsForAllItemsByOwner(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                                @RequestParam(defaultValue = ALL) String state) {
        log.info("Поступил запрос на просмотр {} аренд вещей владельца с id: {}", state, userId);
        return bookingService.getAllBookingsForAllItemsByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateBooking(@PathVariable Long bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Поступил запрос на обновление статуса аренды с id: {}", bookingId);
        return bookingService.updateBooking(bookingId, approved, userId);
    }
}