package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponseDto {

    private Long id;

    @NotNull(message = "Error! Description can't be null!")
    private String description;

    private LocalDateTime created;
}
