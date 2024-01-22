package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Mpa> getRatings() {
        String sqlQuery = "SELECT id, rating FROM MPA";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, this::mapRowToRating)).stream()
                .sorted(Comparator.comparingInt(Mpa::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Mpa getRatingById(int id) {
        String sqlQuery = "SELECT id, rating FROM MPA WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    public boolean hasRating(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT FROM MPA WHERE id = ?)", Boolean.class, id));
    }

    private Mpa mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("rating"));
        return mpa;
    }
}
