package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NoSuchEntityException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.info("Добавлен пользователь");
        return userStorage.createUser(user);
    }

    public User updateUser(User updatedUser) {
        if (hasUser(updatedUser.getId())) {
            log.info("Обновлен пользователь");
            return userStorage.updateUser(updatedUser);
        } else {
            throw new NoSuchEntityException("Пользователя с данным id не существует");
        }
    }

    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return userStorage.getUsers();
    }

    public User getUser(int id) {
        Optional<User> user = userStorage.getUser(id);
        if (user.isPresent()) {
            log.info("Запрошен пользватель с id=" + id);
            return user.get();
        } else {
            throw new NoSuchEntityException("Нет пользователя с таким id");
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
            log.info("Пользователь id=" + id + " добавил друга friendId=" + friendId);
            return getUser(friendId);
        } else {
            throw new NoSuchEntityException("Пользователя с данным id не существует");
        }
    }

    public User deleteFriend(int id, int friendId) {
        if (hasUser(id) && hasUser(friendId)) {
            getUser(id).getFriends().remove(friendId);
            log.info("Пользователь id=" + id + " удалил друга friendId=" + friendId);
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
            log.info("Запрошен список друзей пользователя id=" + id);
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
            log.info("Запрошен список общих друзей пользователей id=" + id + " и otherId=" + otherId);
            return commonFriends;
        } else {
            throw new NoSuchEntityException("Нет пользвателя с таким id");
        }
    }

    private boolean hasUser(int id) {
        return userStorage.hasUser(id);
    }
}