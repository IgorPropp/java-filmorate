package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

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
        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        films.put(updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    public Film putLike(int id, int userId) {
        Set<Integer> pastLikes = new HashSet<>(films.get(id).getLikes());
        pastLikes.add(userId);
        films.get(id).setLikes(pastLikes);
        return films.get(id);
    }

    public Film deleteLike(int id, int userId) {
        films.get(id).getLikes().remove(userId);
        return films.get(id);
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

    @Override
    public Set<Mpa> getRatings() {
        return null;
    }

    @Override
    public Mpa getRatingById(int id) {
        return null;
    }

    @Override
    public boolean hasRating(int id) {
        return false;
    }

    @Override
    public Set<Genre> getGenres() {
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        return null;
    }

    @Override
    public boolean hasGenre(int id) {
        return false;
    }

    public boolean hasFilm(int id) {
        return films.containsKey(id);
    }
}
