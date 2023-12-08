package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody @Valid Film film) throws ValidationException {
        validateFilm(film);
        film.setId(filmId);
        films.put(filmId, film);
        filmId++;
        log.info("Добавлен пользователь");
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film updatedFilm) throws ValidationException {
        validateFilm(updatedFilm);
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
        } else {
            throw new ValidationException("Фильма для обновления не существует");
        }
        log.info("Обновлен фильм");
        return updatedFilm;
    }

    protected void validateFilm(Film film) throws ValidationException {
        if (film.getName().isBlank() || film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDuration() <= 0) {
            log.info("Фильм не прошел валидацию");
            throw new ValidationException("Фильм не прошел валидацию");
        }
    }
}
