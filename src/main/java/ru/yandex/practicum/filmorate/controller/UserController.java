package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable @Positive int id) {
        return userService.getUser(id);
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        validateUser(user);
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User updatedUser) throws ValidationException {
        validateUser(updatedUser);
        return userService.updateUser(updatedUser);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) throws NoSuchEntityException {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable @Positive int id, @PathVariable @Positive int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    public void validateUser(User user) throws ValidationException {
        if (!allUserFieldsAreValid(user)) {
            log.info("Профиль пользователя не прошел валидацию");
            throw new ValidationException("Профиль пользователя не прошел валидацию");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя заменено на логин");
        }
    }

    private boolean allUserFieldsAreValid(User user) {
        return !(user.getEmail().isBlank() || !user.getEmail().contains("@")
                || user.getLogin().isBlank() || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now()));
    }
}
