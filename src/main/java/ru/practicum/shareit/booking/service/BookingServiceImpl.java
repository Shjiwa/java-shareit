package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.constant.State;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDtoOut addBooking(BookingDtoIn bookingDtoIn, Long userId) {
        validateBooking(bookingDtoIn);
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found."));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("You can't rent your own item.");
        }
        if (item.getAvailable().equals(false)) {
            throw new BadRequestException("Item is not available.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        bookingDtoIn.setStatus(Status.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDtoIn, user, item));
        log.info("Booking successfully added!");
        return BookingMapper.INSTANCE.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));
        if ((!booking.getBooker().getId().equals(userId))
                && (!booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Booking not found.");
        }
        log.info("Success! Your booking is: {}", booking);
        return BookingMapper.INSTANCE.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getAllBookingsByUser(Long userId, State state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByEndDesc(userId,
                        PageRequest.of(from / size, size));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        userId, now, now,
                        PageRequest.of(from / size, size));
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start")));
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(userId, Status.WAITING,
                        PageRequest.of(from / size, size));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(userId, Status.REJECTED,
                        PageRequest.of(from / size, size));
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        log.info("Success! Your booking list by state: {} are : {}", state, bookings);
        return BookingMapper.INSTANCE.toBookingDtoOutList(bookings);
    }

    @Override
    public List<BookingDtoOut> getAllBookingsForAllItemsByOwner(Long userId, State state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_OwnerId(
                        userId, PageRequest.of(from / size, size,
                                Sort.by(Sort.Direction.DESC, "start")));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        userId, now, now, PageRequest.of(from / size, size,
                                Sort.by(Sort.Direction.DESC, "start")));
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(
                        userId, now, now, PageRequest.of(from / size, size,
                                Sort.by(Sort.Direction.DESC, "start")));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsAfterAndEndIsAfterOrderByEndDesc(
                        userId, now, now, PageRequest.of(from / size, size,
                                Sort.by(Sort.Direction.DESC, "start")));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(
                        userId, Status.WAITING, PageRequest.of(from / size, size,
                                Sort.by(Sort.Direction.DESC, "start")));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(
                        userId, Status.REJECTED, PageRequest.of(from / size, size,
                                Sort.by(Sort.Direction.DESC, "start")));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Success! Your booking list by state: {} are : {}", state, bookings);
        return BookingMapper.INSTANCE.toBookingDtoOutList(bookings);
    }

    @Transactional
    @Override
    public BookingDtoOut updateBooking(Long bookingId, Boolean status, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Error! You don't have permission to access this option." +
                    " Only the owner of the item can update booking with it.");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("This booking has already been updated to: " + booking.getStatus());
        }
        if (status) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking updated = bookingRepository.save(booking);
        log.info("Success! Updated booking: {}", updated);
        return BookingMapper.INSTANCE.toBookingDtoOut(updated);
    }

    private void validateBooking(BookingDtoIn bookingDtoIn) {
        if (bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())) {
            throw new BadRequestException("Error! Booking end time can't be before start time.");
        }
        if (bookingDtoIn.getEnd().isEqual(bookingDtoIn.getStart())) {
            throw new BadRequestException("Error! Booking end time and start time can't be equal.");
        }
    }
}