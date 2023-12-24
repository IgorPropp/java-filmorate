package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ValidationException;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getFilms(Optional<Integer> id);
    Film createFilm(Film film) throws ValidationException;
    Film updateFilm(Film updatedFilm) throws ValidationException;
}
