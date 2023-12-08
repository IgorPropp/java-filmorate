package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        validateUser(user);
        user.setId(userId);
        users.put(userId, user);
        userId++;
        log.info("Добавлен пользователь");
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User updatedUser) throws ValidationException {
        validateUser(updatedUser);
        if (users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
        } else {
            throw new ValidationException("Пользователя для обновления не существует");
        }
        log.info("Обновлен пользователь");
        return updatedUser;
    }

    protected void validateUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")
                || user.getLogin().isBlank() || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Профиль пользователя не прошел валидацию");
            throw new ValidationException("Профиль пользователя не прошел валидацию");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя заменено на логин");
        }
    }
}
