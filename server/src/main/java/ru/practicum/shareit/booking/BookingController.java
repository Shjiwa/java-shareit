package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.State;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut addBooking(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                    @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Поступил запрос на оформление аренды: {} от пользователя с id: {}", bookingDtoIn, userId);
        return bookingService.addBooking(userId, bookingDtoIn);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                        @PathVariable Long bookingId) {
        log.info("Поступил запрос на просмотр аренды с id: {} от пользователя с id: {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllBookingsByUser(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                    @RequestParam(required = false, defaultValue = "ALL") State state,
                                                    @RequestParam(required = false, defaultValue = "0") int from,
                                                    @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("Поступил запрос на просмотр {} аренд от пользователя с id: {}", state, userId);
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllBookingsForAllItemsByOwner(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                                @RequestParam(required = false, defaultValue = "ALL")
                                                                    State state,
                                                                @RequestParam(required = false, defaultValue = "0")
                                                                    int from,
                                                                @RequestParam(required = false, defaultValue = "20")
                                                                    int size) {
        log.info("Поступил запрос на просмотр {} аренд вещей владельца с id: {}", state, userId);
        return bookingService.getAllBookingsForAllItemsByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateBooking(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        log.info("Поступил запрос на обновление статуса аренды с id: {}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }
}