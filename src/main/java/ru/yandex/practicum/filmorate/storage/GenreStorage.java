package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Genre> getGenres() {
        String sqlQuery = "SELECT id, genre FROM GENRE";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, this::mapRowToGenre)).stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Genre getGenreById(int id) {
        String sqlQuery = "SELECT id, genre FROM GENRE WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("genre"));
        return genre;
    }

    public boolean hasGenre(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT FROM GENRE WHERE id = ?)", Boolean.class, id));
    }
}
