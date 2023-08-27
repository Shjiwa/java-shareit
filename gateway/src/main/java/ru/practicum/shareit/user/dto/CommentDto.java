package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = "Error! Comment text can't be null.")
    private String text;
    private String authorName;
    private LocalDateTime created;
}