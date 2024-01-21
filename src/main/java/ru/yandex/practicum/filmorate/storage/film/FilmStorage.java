package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    List<Film> getFilms();

    Optional<Film> getFilm(int id);

    Film createFilm(Film film);

    Film updateFilm(Film updatedFilm);

    Film putLike(int id, int userId);

    Film deleteLike(int id, int userId);

    List<Film> getTopFilms(int count);

    Set<Mpa> getRatings();

    Mpa getRatingById(int id);

    boolean hasRating(int id);

    Set<Genre> getGenres();

    Genre getGenreById(int id);

    boolean hasGenre(int id);

    boolean hasFilm(int id);
}
