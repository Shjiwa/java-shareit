package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDtoIn {

    private Long id;

    @NotNull(message = "Error! Start time can't be null.")
    @FutureOrPresent(message = "Error! Start time can't be in past.")
    private LocalDateTime start;

    @NotNull(message = "Error! Start time can't be null.")
    @FutureOrPresent(message = "Error! End time can't be in past.")
    private LocalDateTime end;

    @NotNull(message = "Booking must have an item.")
    private Long itemId;

    private UserDto booker;

    private Status status;
}