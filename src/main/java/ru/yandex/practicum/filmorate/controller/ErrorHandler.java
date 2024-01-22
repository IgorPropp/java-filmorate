package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NoSuchEntityException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNullPointerException(final NullPointerException e) {
        log.error("Ошибка: NullPointerException");
        return new ErrorResponse("Не найдено", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка: Валидиация не пройдена");
        return new ErrorResponse("Валидиация не пройдена", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchEntityException(final NoSuchEntityException e) {
        log.error("Ошибка: Сущность не найдена");
        return new ErrorResponse("Не найдено", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final NoSuchElementException e) {
        log.error("Ошибка: Такого элемента не существует");
        return new ErrorResponse("Не найдено", e.getMessage());
    }

    @Getter
    @Setter
    public static class ErrorResponse {

        String error;
        String description;

        public ErrorResponse(String error, String description) {
            this.error = error;
            this.description = description;
        }
    }
}
