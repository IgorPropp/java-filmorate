package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.persistence.Column;

@Data
public class Mpa {
    int id;
    @Column
    String name;

    public Mpa() {

    }
}
