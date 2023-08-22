package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.constant.State.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    void getByIdTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L, owner);

        Booking booking = getBooking(100L, booker, item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));

        BookingDtoOut responseDto = bookingService.getBookingById(owner.getId(), booking.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getByIdTest_UnrelatedUser() {
        User owner = getUser(1L);
        User booker = getUser(2L);
        User unrelated = getUser(3L);

        Item item = getItem(10L, owner);

        Booking booking = getBooking(100L, booker, item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));

        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(unrelated.getId(), booking.getId());
        });

        assertThat(e.getMessage(), equalTo("Booking not found."));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllByStateTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item1 = getItem(10L, owner);
        Item item2 = getItem(11L, owner);

        Booking booking1 = getBooking(100L, booker, item1);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(9));
        Booking booking2 = getBooking(101L, booker, item2);
        booking2.setStart(LocalDateTime.now().minusDays(8));
        booking2.setEnd(LocalDateTime.now().minusDays(7));

        List<Booking> bookingList = Arrays.asList(
                booking1,
                booking2
        );

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findByBooker_IdOrderByEndDesc(eq(booker.getId()), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findByBooker_IdAndEndIsBefore(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findByBooker_IdAndStartIsAfter(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(eq(booker.getId()), eq(Status.WAITING), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findByBooker_IdAndStatusOrderByEndDesc(eq(booker.getId()), eq(Status.REJECTED), any(Pageable.class))).thenReturn(bookingList);

        List<BookingDtoOut> responseDtoList;

        responseDtoList = bookingService.getAllBookingsByUser(booker.getId(), ALL, 0, 10);
        bookingService.getAllBookingsByUser(booker.getId(), PAST, 0, 10);
        bookingService.getAllBookingsByUser(booker.getId(), FUTURE, 0, 10);
        bookingService.getAllBookingsByUser(booker.getId(), CURRENT, 0, 10);
        bookingService.getAllBookingsByUser(booker.getId(), WAITING, 0, 10);
        bookingService.getAllBookingsByUser(booker.getId(), REJECTED, 0, 10);

        assertThat(responseDtoList.get(0).getId(), equalTo(booking1.getId()));
        assertThat(responseDtoList.get(1).getId(), equalTo(booking2.getId()));

        verify(userRepository, times(6)).findById(eq(booker.getId()));
        verify(bookingRepository, times(1)).findByBooker_IdOrderByEndDesc(eq(booker.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findByBooker_IdAndEndIsBefore(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfter(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findByBooker_IdAndStatusOrderByEndDesc(eq(booker.getId()), eq(Status.WAITING), any(Pageable.class));
        verify(bookingRepository, times(1)).findByBooker_IdAndStatusOrderByEndDesc(eq(booker.getId()), eq(Status.REJECTED), any(Pageable.class));

        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void getAllByStateForOwnerTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item1 = getItem(10L, owner);
        Item item2 = getItem(11L, owner);

        Booking booking1 = getBooking(100L, booker, item1);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(9));
        Booking booking2 = getBooking(101L, booker, item2);
        booking2.setStart(LocalDateTime.now().minusDays(8));
        booking2.setEnd(LocalDateTime.now().minusDays(7));

        List<Booking> bookingList = Arrays.asList(
                booking1,
                booking2
        );

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findAllByItem_OwnerId(eq(owner.getId()), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsAfterAndEndIsAfterOrderByEndDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItem_OwnerIdAndStatus(eq(owner.getId()), eq(Status.WAITING), any(Pageable.class))).thenReturn(bookingList);
        when(bookingRepository.findAllByItem_OwnerIdAndStatus(eq(owner.getId()), eq(Status.REJECTED), any(Pageable.class))).thenReturn(bookingList);

        List<BookingDtoOut> responseDtoList;

        responseDtoList = bookingService.getAllBookingsForAllItemsByOwner(owner.getId(), ALL, 0, 10);
        bookingService.getAllBookingsForAllItemsByOwner(owner.getId(), PAST, 0, 10);
        bookingService.getAllBookingsForAllItemsByOwner(owner.getId(), FUTURE, 0, 10);
        bookingService.getAllBookingsForAllItemsByOwner(owner.getId(), CURRENT, 0, 10);
        bookingService.getAllBookingsForAllItemsByOwner(owner.getId(), WAITING, 0, 10);
        bookingService.getAllBookingsForAllItemsByOwner(owner.getId(), REJECTED, 0, 10);

        assertThat(responseDtoList.get(0).getId(), equalTo(booking1.getId()));
        assertThat(responseDtoList.get(1).getId(), equalTo(booking2.getId()));

        verify(userRepository, times(6)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).findAllByItem_OwnerId(eq(owner.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStartIsAfterAndEndIsAfterOrderByEndDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStatus(eq(owner.getId()), eq(Status.WAITING), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItem_OwnerIdAndStatus(eq(owner.getId()), eq(Status.REJECTED), any(Pageable.class));

        verifyNoMoreInteractions(userRepository, bookingRepository);
    }

    @Test
    void createTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L, owner);

        Booking booking = getBooking(100L, booker, item);

        LocalDateTime now = LocalDateTime.now();
        BookingDtoIn requestDto = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(5))
                .build();

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut responseDto = bookingService.addBooking(requestDto, booker.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest_NotAvailableItem() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L, owner);
        item.setAvailable(false);

        LocalDateTime now = LocalDateTime.now();
        BookingDtoIn requestDto = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(5))
                .build();

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));

        BadRequestException e = assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(requestDto, booker.getId());
        });

        assertThat(e.getMessage(), equalTo("Item is not available."));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest_BookOwnItem() {
        User owner = getUser(1L);

        Item item = getItem(10L, owner);

        LocalDateTime now = LocalDateTime.now();
        BookingDtoIn requestDto = BookingDtoIn.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(5))
                .build();

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));

        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(requestDto, owner.getId());
        });

        assertThat(e.getMessage(), equalTo("You can't rent your own item."));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, bookingRepository);
    }

    @Test
    void approveTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L, owner);

        Booking booking = getBooking(100L, booker, item);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut responseDto = bookingService.updateBooking(booking.getId(), true, owner.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveTest_ByNotOwner() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L, owner);

        Booking booking = getBooking(100L, booker, item);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.updateBooking(booking.getId(), true, booker.getId()));

        assertThat(e.getMessage(), equalTo("Error! You don't have permission to access this option." +
                " Only the owner of the item can update booking with it."));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void approveTest_ForNotWaitingBooking() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L, owner);

        Booking booking = getBooking(100L, booker, item);
        booking.setStatus(Status.APPROVED);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));

        BadRequestException e = assertThrows(BadRequestException.class, () -> {
            bookingService.updateBooking(booking.getId(), true, owner.getId());
        });

        assertThat(e.getMessage(), equalTo("This booking has already been updated to: " + booking.getStatus()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    private User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@user.com");
        return user;
    }

    private Item getItem(Long id, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("ItemDescr " + id);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(null);
        return item;
    }

    private Booking getBooking(Long id, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}