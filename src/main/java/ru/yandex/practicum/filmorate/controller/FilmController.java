package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class FilmController {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmStorage.getFilms(Optional.empty());
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable @Positive int id) {
        return filmStorage.getFilms(Optional.of(id)).get(0);
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody @Valid Film film) throws ValidationException {
        return filmStorage.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film updatedFilm) throws ValidationException {
        return filmStorage.updateFilm(updatedFilm);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film putLike (@PathVariable int id, @PathVariable int userId) throws NoSuchEntityException {
        if (filmStorage.hasFilm(id) && userStorage.hasUser(userId)) {
            return filmService.putLike(id, userId);
        } else {
            throw new NoSuchEntityException("Пользователя или фильма с таким id не существует");
        }
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film deleteLike (@PathVariable int id, @PathVariable int userId) throws NoSuchEntityException {
        if (filmStorage.hasFilm(id) && userStorage.hasUser(userId)) {
            return filmService.deleteLike(id, userId);
        } else {
            throw new NoSuchEntityException("Пользователя или фильма с таким id не существует");
        }
    }

    @GetMapping(value = {"/films/popular?count={count}", "/films/popular"})
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getTopFilms(count);
    }
}
