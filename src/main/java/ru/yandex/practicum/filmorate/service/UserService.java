package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        Optional<User> user = userStorage.getUser(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NoSuchEntityException("Нет пользователя с таким id");
        }
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updatedUser) {
        if (hasUser(updatedUser.getId())) {
            return userStorage.updateUser(updatedUser);
        } else {
            throw new NoSuchEntityException("Пользователя с данным id не существует");
        }
    }

    public User addFriend(int id, int friendId) {
        if (hasUser(id) && hasUser(friendId)) {
            Set<Integer> pastFriends = new HashSet<>(getUser(id).getFriends());
            pastFriends.add(friendId);
            getUser(id).setFriends(pastFriends);
            pastFriends = new HashSet<>((getUser(friendId)).getFriends());
            pastFriends.add(id);
            getUser(friendId).setFriends(pastFriends);
            return getUser(friendId);
        } else {
            throw new NoSuchEntityException("Пользователя с данным id не существует");
        }
    }

    public User deleteFriend(int id, int friendId) {
        if (hasUser(id) && hasUser(friendId)) {
            getUser(id).getFriends().remove(friendId);
            return getUser(friendId);
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }

    public List<User> getFriends(int id) {
        if (hasUser(id)) {
            List<User> friendsList = new ArrayList<>();
            for (Integer friendId : getUser(id).getFriends()) {
                friendsList.add(getUser(friendId));
            }
            return friendsList;
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }

    public List<User> getCommonFriends(int id, int otherId) {
        if (userStorage.hasUser(id) && userStorage.hasUser(otherId)) {
            List<Integer> commonFriendsIds = new ArrayList<>(getUser(id).getFriends());
            commonFriendsIds.retainAll(getUser(otherId).getFriends());
            List<User> commonFriends = new ArrayList<>();
            for (Integer commonFriendId : commonFriendsIds) {
                commonFriends.add(getUser(commonFriendId));
            }
            return commonFriends;
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }

    private boolean hasUser(int id) {
        return userStorage.hasUser(id);
    }
}