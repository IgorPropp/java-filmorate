package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

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

    public Film updateFilm(Film updatedFilm) {
        if (filmStorage.getFilm(updatedFilm.getId()).isPresent()) {
            log.info("Обновлен фильм");
            return filmStorage.updateFilm(updatedFilm);
        } else {
            throw new NoSuchEntityException("Фильма с данным id=" + updatedFilm.getId() + " не существует");
        }
    }

    public List<Film> getFilms() {
        log.info("Запрошен список фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        log.info("Запрошен фильм с id=" + id);
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NoSuchEntityException("Не найден фильм с id: " + id));
    }

    public Film putLike(int id, int userId) {
        if (filmStorage.getFilm(id).isEmpty()) {
            throw new NoSuchEntityException("Фильма с таким id не существует");
        } else if (userStorage.getUser(userId).isEmpty()) {
            throw new NoSuchEntityException("Пользователя с таким id не существует");
        } else {
            log.info("Пользователь userId=" + userId + " поставил лайк фильму id=" + id);
            return filmStorage.putLike(id, userId);
        }

    }

    public Film deleteLike(int id, int userId) {
        if (filmStorage.getFilm(id).isPresent() && userStorage.getUser(userId).isPresent()) {
            log.info("Пользователь userId=" + userId + " удалил лайк с фильма id=" + id);
            return filmStorage.deleteLike(id, userId);
        } else {
            throw new NoSuchEntityException("Пользователя или фильма с таким id не существует");
        }
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Запрошено топ-" + count + " фильмов");
        return filmStorage.getTopFilms(count);
    }
}