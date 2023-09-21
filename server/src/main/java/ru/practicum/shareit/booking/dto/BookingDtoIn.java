package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDtoIn {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private UserDto booker;

    private Status status;
}