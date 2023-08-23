package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemResponseWithItemsDto {

    private Long id;

    @NotNull(message = "Error! Description can't be null!")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
