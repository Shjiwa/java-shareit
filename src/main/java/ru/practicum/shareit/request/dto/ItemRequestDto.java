package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {

    private Long id;

    private String description;

    @NotNull(message = "Error! Requester can't be null.")
    private User requester;
}