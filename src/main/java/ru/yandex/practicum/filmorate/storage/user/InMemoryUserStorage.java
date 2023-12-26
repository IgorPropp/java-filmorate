package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 1;

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getUser(int id) {
        if (users.containsKey(id)) {
            return Optional.ofNullable(users.get(id));
        } else {
            return Optional.empty();
        }
    }

    public User createUser(User user) {
        user.setId(userId);
        users.put(userId, user);
        userId++;
        log.info("Добавлен пользователь");
        return user;
    }

    public User updateUser(User updatedUser) {
        users.put(updatedUser.getId(), updatedUser);
        log.info("Обновлен пользователь");
        return updatedUser;
    }

    public boolean hasUser(int id) {
        return users.containsKey(id);
    }
}
