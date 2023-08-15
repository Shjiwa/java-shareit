package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * два пользователя не могут
     * иметь одинаковый адрес электронной почты
     */
    @Column(nullable = false, unique = true)
    private String email;
}