package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.Set;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Set<Mpa> getRatings() {
        return filmService.getRatings();
    }

    @GetMapping(value = "/{id}")
    public Mpa getRatingById(@PathVariable @Positive int id) {
        return filmService.getRatingById(id);
    }
}