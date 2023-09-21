package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.constant.State;

import java.util.List;

public interface BookingService {

    BookingDtoOut addBooking(Long userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut getBookingById(Long userId, Long bookingId);

    List<BookingDtoOut> getAllBookingsByUser(Long userId, State state, int from, int size);

    List<BookingDtoOut> getAllBookingsForAllItemsByOwner(Long userId, State state, int from, int size);

    BookingDtoOut updateBooking(Long userId, Long bookingId, Boolean status);
}