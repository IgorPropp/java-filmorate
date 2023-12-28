package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        log.info("Добавлен фильм");
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film updatedFilm) throws ValidationException {
        if (hasFilm(updatedFilm.getId())) {
            log.info("Обновлен фильм");
            return filmStorage.updateFilm(updatedFilm);
        } else {
            throw new ValidationException("Фильма с данным id не существует");
        }
    }

    public List<Film> getFilms() {
        log.info("Запрошен список фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        Optional<Film> film = filmStorage.getFilm(id);
        if (film.isPresent()) {
            log.info("Запрошен фильм с id=" + id);
            return film.get();
        } else {
            throw new NoSuchEntityException("Нет фильма с таким id");
        }
    }

    public Film putLike(int id, int userId) {
        if (!filmStorage.hasFilm(id)) {
            throw new NoSuchEntityException("Фильма с таким id не существует");
        } else if (!userStorage.hasUser(userId)) {
            throw new NoSuchEntityException("Пользователя с таким id не существует");
        } else {
            Set<Integer> pastLikes = new HashSet<>(getFilm(id).getLikes());
            pastLikes.add(userId);
            getFilm(id).setLikes(pastLikes);
            log.info("Пользователь userId=" + userId + " поставил лайк фильму id=" + id);
            return getFilm(id);
        }

    }

    public Film deleteLike(int id, int userId) {
        if (filmStorage.hasFilm(id) && userStorage.hasUser(userId)) {
            getFilm(id).getLikes().remove(userId);
            log.info("Пользователь userId=" + userId + " удалил лайк с фильма id=" + id);
            return getFilm(id);
        } else {
            throw new NoSuchEntityException("Пользователя или фильма с таким id не существует");
        }
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Запрошено топ-" + count + " фильмов");
        return filmStorage.getTopFilms(count);
    }

    private boolean hasFilm(int id) {
        return filmStorage.hasFilm(id);
    }

}
