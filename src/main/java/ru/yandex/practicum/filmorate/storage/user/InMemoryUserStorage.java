package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
        return user;
    }

    public User updateUser(User updatedUser) {
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    public User addFriend(int id, int friendId) {
        Set<Integer> pastFriends = new HashSet<>(users.get(id).getFriends());
        pastFriends.add(friendId);
        users.get(id).setFriends(pastFriends);
        pastFriends = new HashSet<>((users.get(friendId)).getFriends());
        pastFriends.add(id);
        users.get(friendId).setFriends(pastFriends);
        return users.get(friendId);
    }

    public User deleteFriend(int id, int friendId) {
        users.get(id).getFriends().remove(friendId);
        return users.get(id);
    }

    public boolean hasUser(int id) {
        return users.containsKey(id);
    }
}
