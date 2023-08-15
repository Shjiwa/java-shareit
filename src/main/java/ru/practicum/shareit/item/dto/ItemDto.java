package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Error! Name can't be null.")
    private String name;

    @NotBlank(message = "Error! Description can't be null.")
    private String description;

    @NotNull(message = "Error! Available can't be null.")
    private Boolean available;
}
