package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Set;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Set<Genre> getGenres() {
        log.info("Запрошен список жанров");
        return genreStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        if (hasGenre(id)) {
            log.info("Запрошен жанр id= " + id);
            return genreStorage.getGenreById(id);
        } else {
            throw new NoSuchEntityException("Жанра с таким ID нет");
        }
    }

    private boolean hasGenre(int id) {
        return genreStorage.hasGenre(id);
    }
}
