package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.NoSuchEntityException;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUser(user.getId()).orElseThrow();
    }

    public Optional<User> getUser(int id) {
        String sqlQuery = "select id, email, login, name, birthday " +
                "from users as u left outer join friends as fr on u.id=user_a where id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new NoSuchEntityException("Нет пользователя с таким ID");
        } else {
            return Optional.of(users.get(0));
        }
    }

    public List<User> getUsers() {
        String sqlQuery = "select id, email, login, name, birthday" +
                " from users as u left outer join friends as fr on u.id=user_a";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    public User addFriend(int id, int friendId) {
        String sql = "insert into friends(USER_A, USER_B, ACCEPTED) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sql, id, friendId, Boolean.TRUE);
        return getUser(friendId).orElseThrow();
    }

    public User deleteFriend(int id, int friendId) {
        String sql = "delete from friends where USER_A = ? and USER_B = ?";
        jdbcTemplate.update(sql, id, friendId);
        return getUser(id).orElseThrow();
    }

    public boolean hasUser(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM users WHERE id = ?)", Boolean.class, id));
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        user.setFriends(getFriends(user.getId()));
        return user;
    }

    private Set<Integer> getFriends(int id) {
        String sqlQuery = "select user_b from friends where user_a = ?";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("user_b"), id));
    }
}
