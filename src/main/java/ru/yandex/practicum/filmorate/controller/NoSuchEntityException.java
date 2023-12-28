package ru.yandex.practicum.filmorate.controller;

public class NoSuchEntityException extends RuntimeException {

    public NoSuchEntityException(String message) {
        super(message);
    }
}
