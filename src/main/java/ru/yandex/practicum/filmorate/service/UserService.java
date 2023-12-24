package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int id, int friendId) {
        Set<Integer> pastFriends = new HashSet<>(userStorage.getUsers(Optional.of(id)).get(0).getFriends());
        pastFriends.add(friendId);
        userStorage.getUsers(Optional.of(id)).get(0).setFriends(pastFriends);
        pastFriends = new HashSet<>(userStorage.getUsers(Optional.of(friendId)).get(0).getFriends());
        pastFriends.add(id);
        userStorage.getUsers(Optional.of(friendId)).get(0).setFriends(pastFriends);
        return userStorage.getUsers(Optional.of(friendId)).get(0);
    }

    public User deleteFriend(int id, int friendId) {
        userStorage.getUsers(Optional.of(id)).get(0).getFriends().remove(friendId);
        return userStorage.getUsers(Optional.of(friendId)).get(0);
    }

    public List<User> getFriends(int id) {
        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : userStorage.getUsers(Optional.of(id)).get(0).getFriends()) {
            friendsList.add(userStorage.getUsers(Optional.of(friendId)).get(0));
        }
        return friendsList;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<Integer> commonFriendsIds = new ArrayList<>(userStorage.getUsers(Optional.of(id)).get(0).getFriends());
        commonFriendsIds.retainAll(userStorage.getUsers(Optional.of(otherId)).get(0).getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (Integer commonFriendId : commonFriendsIds) {
            commonFriends.add(userStorage.getUsers(Optional.of(commonFriendId)).get(0));
        }
        return commonFriends;
    }
}