package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.constant.State;

import java.util.List;

public interface BookingService {

    BookingDtoOut addBooking(BookingDtoIn bookingDtoIn, Long userId);

    BookingDtoOut getBookingById(Long userId, Long bookingId);

    List<BookingDtoOut> getAllBookingsByUser(Long userId, State state);

    List<BookingDtoOut> getAllBookingsForAllItemsByOwner(Long userId, State state);

    BookingDtoOut updateBooking(Long bookingId, Boolean status, Long userId);
}