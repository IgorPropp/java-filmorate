package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {

    private final MpaDbStorage mpaStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getRatings() {
        log.info("Запрошен список рейтингов");
        return mpaStorage.getRatings();
    }

    public Mpa getRatingById(int id) {
        return mpaStorage.getRatingById(id).orElseThrow(() -> new NoSuchEntityException("Не найден рейтинг с id=" + id));
    }
}
