package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    public List<Film> getFilms(Optional<Integer> id) {
        return id.map(integer -> List.of(films.get(integer))).orElseGet(() -> new ArrayList<>(films.values()));
    }

    public Film createFilm(Film film) throws ValidationException {
        validateFilm(film);
        film.setId(filmId);
        films.put(filmId, film);
        filmId++;
        log.info("Добавлен пользователь");
        return film;
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException {
        validateFilm(updatedFilm);
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
        } else {
            throw new ValidationException("Фильма с данным id не существует");
        }
        log.info("Обновлен фильм");
        return updatedFilm;
    }

    public boolean hasFilm(int id) {
        return films.containsKey(id);
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
