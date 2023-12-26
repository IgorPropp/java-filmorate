package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> getFilm(int id) {
        if (films.containsKey(id)) {
            return Optional.ofNullable(films.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Film createFilm(Film film) {
        film.setId(filmId);
        films.put(filmId, film);
        filmId++;
        log.info("Добавлен пользователь");
        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Обновлен фильм");
        return updatedFilm;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> topFilms = films.values().stream()
                .sorted((Film film1, Film film2) -> (film2.getLikes().size() - film1.getLikes().size()))
                .collect(Collectors.toList());
        if (count - 1 == 0) {
            return List.of(topFilms.get(0));
        } else if (topFilms.size() > count) {
            return topFilms.subList(0, count - 1);
        } else {
            return topFilms;
        }
    }

    public boolean hasFilm(int id) {
        return films.containsKey(id);
    }
}
