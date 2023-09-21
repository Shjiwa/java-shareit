package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {

    @NotNull(message = "Error! Description can't be null!")
    private String description;
}