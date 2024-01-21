package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.NoSuchEntityException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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

    public Film updateFilm(Film updatedFilm) {
        if (hasFilm(updatedFilm.getId())) {
            log.info("Обновлен фильм");
            return filmStorage.updateFilm(updatedFilm);
        } else {
            throw new NoSuchEntityException("Фильма с данным id не существует");
        }
    }

    public List<Film> getFilms() {
        log.info("Запрошен список фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        if (filmStorage.hasFilm(id)) {
            Optional<Film> film = filmStorage.getFilm(id);
            if (film.isPresent()) {
                log.info("Запрошен фильм с id=" + id);
                return film.get();
            } else {
                throw new NoSuchEntityException("Нет фильма с таким id");
            }
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
            log.info("Пользователь userId=" + userId + " поставил лайк фильму id=" + id);
            return filmStorage.putLike(id, userId);
        }

    }

    public Film deleteLike(int id, int userId) {
        if (filmStorage.hasFilm(id) && userStorage.hasUser(userId)) {
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

    public Set<Mpa> getRatings() {
        log.info("Запрошен список рейтингов");
        return filmStorage.getRatings();
    }

    public Mpa getRatingById(int id) {
        if (hasRating(id)) {
            log.info("Запрошен рейтинг id= " + id);
            return filmStorage.getRatingById(id);
        } else {
            throw new NoSuchEntityException("Рейтинга с таким ID нет");
        }
    }

    public Set<Genre> getGenres() {
        log.info("Запрошен список жанров");
        return filmStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        if (hasGenre(id)) {
            log.info("Запрошен жанр id= " + id);
            return filmStorage.getGenreById(id);
        } else {
            throw new NoSuchEntityException("Жанра с таким ID нет");
        }
    }

    private boolean hasGenre(int id) {
        return filmStorage.hasGenre(id);
    }

    private boolean hasFilm(int id) {
        return filmStorage.hasFilm(id);
    }

    private boolean hasRating(int id) {
        return filmStorage.hasRating(id);
    }

}
