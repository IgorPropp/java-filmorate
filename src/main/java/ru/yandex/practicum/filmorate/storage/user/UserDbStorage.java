package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Primary
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        return getUser(user.getId()).orElseThrow();
    }

    public Optional<User> getUser(int id) {
        String sqlQuery = "SELECT id, email, login, name, birthday, fr.USER_B " +
                "FROM users AS u LEFT OUTER JOIN friends AS fr ON u.id=user_a WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            return  Optional.empty();
        }
    }

    public List<User> getUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday, fr.USER_B" +
                " FROM users AS u LEFT OUTER JOIN friends AS fr ON u.id=user_a";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    public User addFriend(int id, int friendId) {
        String sql = "INSERT INTO friends(USER_A, USER_B, ACCEPTED) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, friendId, Boolean.TRUE);
        return getUser(friendId).orElseThrow();
    }

    public User deleteFriend(int id, int friendId) {
        String sql = "DELETE FROM friends WHERE USER_A = ? AND USER_B = ?";
        jdbcTemplate.update(sql, id, friendId);
        return getUser(id).orElseThrow();
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        if (resultSet.getInt("user_b") != 0) {
            do {
                user.getFriends().add(resultSet.getInt("user_b"));
            } while (resultSet.next() && resultSet.getInt("id") == user.getId());
        }
        return user;
    }
}
