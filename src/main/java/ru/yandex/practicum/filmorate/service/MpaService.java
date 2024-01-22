package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Set;

@Slf4j
@Service
public class MpaService {

    private final MpaDbStorage mpaStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Set<Mpa> getRatings() {
        log.info("Запрошен список рейтингов");
        return mpaStorage.getRatings();
    }

    public Mpa getRatingById(int id) {
        if (hasRating(id)) {
            log.info("Запрошен рейтинг id= " + id);
            return mpaStorage.getRatingById(id);
        } else {
            throw new NoSuchEntityException("Рейтинга с таким ID нет");
        }
    }

    private boolean hasRating(int id) {
        return mpaStorage.hasRating(id);
    }
}
