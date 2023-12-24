package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.ValidationException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userStorage.getUsers(Optional.empty());
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        return userStorage.getUsers(Optional.of(id)).get(0);
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        return userStorage.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User updatedUser) throws ValidationException {
        return userStorage.updateUser(updatedUser);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) throws ValidationException {
        if (id > 0 && friendId > 0) {
            return userService.addFriend(id, friendId);
        } else {
            throw new ValidationException("id должен быть больше 0");
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) throws NoSuchEntityException {
        if (userStorage.hasUser(id) && userStorage.hasUser(friendId)) {
            return userService.deleteFriend(id, friendId);
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable int id) throws NoSuchEntityException {
        if (userStorage.hasUser(id)) {
            return userService.getFriends(id);
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) throws NoSuchEntityException {
        if (userStorage.hasUser(id) && userStorage.hasUser(otherId)) {
            return userService.getCommonFriends(id, otherId);
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }
}
