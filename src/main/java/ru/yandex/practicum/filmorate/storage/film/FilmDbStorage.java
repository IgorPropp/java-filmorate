package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;


    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film createFilm(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId((Integer) keyHolder.getKey());

        try {
            for (Genre genre : film.getGenres()) {
                String sql2 = "INSERT INTO FILM_GENRE SET film_id = ?, genre_id = ?";
                jdbcTemplate.update(sql2, film.getId(), genre.getId());
            }
            return getFilm(film.getId()).orElseThrow();
        } catch (NullPointerException e) {
            return getFilm(film.getId()).orElseThrow();
        }
    }

    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        String sqlDelete = "DELETE FROM FILM_GENRE WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        try {
            for (Genre genre : film.getGenres().stream().distinct().collect(Collectors.toList())) {
                String sql2 = "INSERT INTO FILM_GENRE SET film_id = ?, genre_id = ?";
                jdbcTemplate.update(sql2, film.getId(), genre.getId());
            }
            return getFilm(film.getId()).orElseThrow();
        } catch (NullPointerException e) {
            return getFilm(film.getId()).orElseThrow();
        }
    }

    public Optional<Film> getFilm(int id) {
        String sqlQuery = "SELECT F.id, F.name, F.description, F.release_date, F.duration, M.id as mpa_id, M.rating, " +
                "G.id as genre_id, G.genre " +
                "FROM FILMS F LEFT OUTER JOIN PUBLIC.FILM_GENRE FG on F.ID = FG.FILM_ID " +
                "LEFT OUTER JOIN PUBLIC.MPA M on F.MPA = M.ID " +
                "LEFT OUTER JOIN PUBLIC.LIKES L on F.ID = L.FILM_ID " +
                "LEFT OUTER JOIN PUBLIC.GENRE G on FG.GENRE_ID = G.ID " +
                "WHERE F.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToSingleFilm, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Film> getFilms() {
        String sqlQuery = "SELECT F.id, F.name, F.description, F.release_date, F.duration, M.id as mpa_id, M.rating " +
                "FROM FILMS F LEFT OUTER JOIN PUBLIC.FILM_GENRE FG on F.ID = FG.FILM_ID " +
                "LEFT OUTER JOIN PUBLIC.MPA M on F.MPA = M.ID " +
                "LEFT OUTER JOIN PUBLIC.LIKES L on F.ID = L.FILM_ID ";
        List<Film>films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        HashMap<Integer, ArrayList<Genre>> relMap = new HashMap<>();
        String sqlQuery2 = "SELECT FG.film_id as film_id, G.id as genre_id, G.genre as genre FROM FILM_GENRE FG " +
                "LEFT OUTER JOIN GENRE G on FG.GENRE_ID = G.ID";
        jdbcTemplate.query(sqlQuery2, (rs, rowNum) -> {
            if (!relMap.containsKey(rs.getInt("film_id"))) {
                relMap.put(rs.getInt("film_id"), new ArrayList<>(List.of(new Genre(rs.getInt("genre_id"), rs.getString("genre")))));
            } else {
                relMap.get(rs.getInt("film_id")).add(new Genre(rs.getInt("genre_id"), rs.getString("genre")));
            }
            return null;
        });
        for (Film film : films) {
            for (Integer id : relMap.keySet()) {
                if (film.getId() == id) {
                    film.setGenres(relMap.get(id));
                }
            }
        }
        return films;
    }

    public Film putLike(int id, int userId) {
        String sqlQuery = "INSERT INTO likes(user_id, film_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, id);
        return getFilm(id).orElseThrow();
    }

    public Film deleteLike(int id, int userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, id);
        return getFilm(id).orElseThrow();
    }

    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT F.id, F.name, F.description, F.release_date, F.duration, M.id as mpa_id, M.rating, " +
                "COUNT(user_id) as likes " +
                "FROM FILMS F LEFT OUTER JOIN PUBLIC.FILM_GENRE FG on F.ID = FG.FILM_ID " +
                "LEFT OUTER JOIN PUBLIC.MPA M on F.MPA = M.ID " +
                "LEFT OUTER JOIN PUBLIC.LIKES L on F.ID = L.FILM_ID " +
                "GROUP BY F.id ORDER BY likes DESC LIMIT ?";
        List<Film>films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        HashMap<Integer, ArrayList<Genre>> relMap = new HashMap<>();
        String sqlQuery2 = "SELECT FG.film_id as film_id, G.id as genre_id, G.genre as genre FROM FILM_GENRE FG " +
                "LEFT OUTER JOIN GENRE G on FG.GENRE_ID = G.ID";
        jdbcTemplate.query(sqlQuery2, (rs, rowNum) -> {
            if (!relMap.containsKey(rs.getInt("film_id"))) {
                relMap.put(rs.getInt("film_id"), new ArrayList<>(List.of(new Genre(rs.getInt("genre_id"), rs.getString("genre")))));
            } else {
                relMap.get(rs.getInt("film_id")).add(new Genre(rs.getInt("genre_id"), rs.getString("genre")));
            }
            return null;
        });
        for (Film film : films) {
            for (Integer id : relMap.keySet()) {
                if (film.getId() == id) {
                    film.setGenres(relMap.get(id));
                }
            }
        }
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("rating")));
        return film;
    }

    private Film mapRowToSingleFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("rating")));
        if (resultSet.getString("genre") != null) {
            do {
                Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre"));
                film.getGenres().add(genre);
            } while (resultSet.next() && resultSet.getInt("id") == film.getId());
        }
        return film;
    }
}
