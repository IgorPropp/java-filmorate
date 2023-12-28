package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getFilms();

    Optional<Film> getFilm(int id);

    Film createFilm(Film film);

    Film updateFilm(Film updatedFilm);

    List<Film> getTopFilms(int count);

    boolean hasFilm(int id);
}
