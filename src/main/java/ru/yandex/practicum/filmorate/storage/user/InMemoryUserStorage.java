package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 1;

    public List<User> getUsers(Optional<Integer> id) {
        return id.map(integer -> List.of(users.get(integer))).orElseGet(() -> new ArrayList<>(users.values()));
    }

    public User createUser(User user) throws ValidationException {
        validateUser(user);
        user.setId(userId);
        users.put(userId, user);
        userId++;
        log.info("Добавлен пользователь");
        return user;
    }

    public User updateUser(User updatedUser) throws ValidationException {
        validateUser(updatedUser);
        if (users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
        } else {
            throw new ValidationException("Пользователя с данным id не существует");
        }
        log.info("Обновлен пользователь");
        return updatedUser;
    }

    public boolean hasUser(int id) {
        return users.containsKey(id);
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
