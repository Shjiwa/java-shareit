package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {

    private Long id;

    private String name;

    /**
     * два пользователя не могут
     * иметь одинаковый адрес электронной почты
     */
    @NotEmpty(message = "Error! Email can't be empty.")
    @Email(message = "Error! Wrong email.")
    private String email;
}