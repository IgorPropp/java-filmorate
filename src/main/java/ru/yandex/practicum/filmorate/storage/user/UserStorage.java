package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.storage.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getUsers(Optional<Integer> id);
    User createUser(User user) throws ValidationException;
    User updateUser(User updatedUser) throws ValidationException;
}
