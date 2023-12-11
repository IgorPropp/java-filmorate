package ru.yandex.practicum.filmorate.controller;

import org.junit.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class ValidationTest {
    FilmController filmController = new FilmController();
    UserController userController = new UserController();

    @Test
    public void filmValidationTest() {
        Film noNameFilm = new Film(1, "", "description", LocalDate.of(2020, 2, 20), 90);
        assertThrows(ValidationException.class,() -> filmController.validateFilm(noNameFilm));
        Film tooEarlyFilm = new Film(2, "name", "description", LocalDate.of(1000, 1, 1), 100);
        assertThrows(ValidationException.class,() -> filmController.validateFilm(tooEarlyFilm));
        Film negativeDurationFilm = new Film(3, "name", "description", LocalDate.of(2020, 2, 20), -50);
        assertThrows(ValidationException.class,() -> filmController.validateFilm(negativeDurationFilm));
    }

    @Test
    public void userValidationTest() throws ValidationException {
        User blankEmailUser = new User(1, " ", "login", "name", LocalDate.of(1999, 8, 5));
        assertThrows(ValidationException.class,() -> userController.validateUser(blankEmailUser));
        User noDogEmailUser = new User(2, "email", "login", "name", LocalDate.of(1999, 8, 5));
        assertThrows(ValidationException.class,() -> userController.validateUser(noDogEmailUser));
        User blankLoginUser = new User(3, "email@email.com", " ", "name", LocalDate.of(1999, 8, 5));
        assertThrows(ValidationException.class,() -> userController.validateUser(blankLoginUser));
        User spaceLoginUser = new User(4, "email@email.com", "l o g i n", "name", LocalDate.of(1999, 8, 5));
        assertThrows(ValidationException.class,() -> userController.validateUser(spaceLoginUser));
        User noNameUser = new User(5, "email@email.com", "login", "", LocalDate.of(1999, 8, 5));
        userController.validateUser(noNameUser);
        assertEquals(noNameUser.getLogin(), noNameUser.getName());
        User notYetBornUser = new User(6, "email@email.com", "login", "name", LocalDate.of(2222, 2, 22));
        assertThrows(ValidationException.class,() -> userController.validateUser(notYetBornUser));
    }
}
