package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film putLike (int id, int userId) {
        Set<Integer> pastLikes = new HashSet<>(filmStorage.getFilms(Optional.of(id)).get(0).getLikes());
        pastLikes.add(userId);
        filmStorage.getFilms(Optional.of(id)).get(0).setLikes(pastLikes);
        return filmStorage.getFilms(Optional.of(id)).get(0);
    }

    public Film deleteLike (int id, int userId) {
        filmStorage.getFilms(Optional.of(id)).get(0).getLikes().remove(userId);
        return filmStorage.getFilms(Optional.of(id)).get(0);
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> topFilms = filmStorage.getFilms(Optional.empty()).stream()
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



}
