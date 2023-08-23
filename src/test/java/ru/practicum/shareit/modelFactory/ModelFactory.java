package ru.practicum.shareit.modelFactory;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ModelFactory {

    private static volatile ModelFactory factory;

    private ModelFactory() {

    }

    public static ModelFactory getInstance() {
        ModelFactory result = factory;

        if (result != null) {
            return result;
        }
        synchronized (ModelFactory.class) {
            if (factory == null) {
                factory = new ModelFactory();
            }
            return factory;
        }
    }

    public BookingDtoOut getBookingResponseDto(Long id, LocalDateTime time) {
        BookingDtoOut dtoOut = new BookingDtoOut();
        dtoOut.setId(id);
        dtoOut.setStart(time.plusDays(1));
        dtoOut.setEnd(time.plusDays(2));
        return dtoOut;
    }

    public Booking getBooking(Long id, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        return booking;
    }

    public BookingDtoIn getBookingDtoIn(LocalDateTime time) {
        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(time.plusDays(1));
        bookingDtoIn.setEnd(time.plusDays(2));
        return bookingDtoIn;
    }

    public UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("user@mail.ru");
        return userDto;
    }

    public User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User" + id);
        user.setEmail("user" + id + "@mail.ru");
        return user;
    }

    public ItemDto getItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("ItemDescription");
        itemDto.setAvailable(true);
        return itemDto;
    }

    public Item getItem(Long id, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item" + id);
        item.setDescription("ItemDescription" + id);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(null);
        return item;
    }
}