package ru.yandex.practicum.filmorate.controller;

public class NoSuchEntityException extends Exception {

    public NoSuchEntityException(String message) {
        super(message);
    }
}
