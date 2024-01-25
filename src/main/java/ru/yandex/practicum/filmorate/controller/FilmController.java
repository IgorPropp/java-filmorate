package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @Positive int id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) throws ValidationException {
        validateFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film updatedFilm) throws ValidationException {
        validateFilm(updatedFilm);
        return filmService.updateFilm(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        return filmService.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        return filmService.getTopFilms(count);
    }

    public void validateFilm(Film film) throws ValidationException {
        if (!allFilmFieldsAreValid(film)) {
            log.info("Фильм не прошел валидацию");
            throw new ValidationException("Фильм не прошел валидацию");
        }
    }

    private boolean allFilmFieldsAreValid(Film film) {
        return !(film.getName().isBlank() || film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDuration() <= 0);
    }
}
