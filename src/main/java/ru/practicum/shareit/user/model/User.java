package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank
    private String name;

    /**
     * два пользователя не могут
     * иметь одинаковый адрес электронной почты
     */
    @Email
    @NotNull
    private String email;
}
