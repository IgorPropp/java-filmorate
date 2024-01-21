package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Genre {
    int id;
    @NotNull
    @NotBlank
    String name;
}
