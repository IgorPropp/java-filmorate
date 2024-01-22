package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaStorage;
    private final GenreStorage genreStorage;


    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
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
            for (Genre genre : film.getGenres()) {
                String sql2 = "INSERT INTO FILM_GENRE SET film_id = ?, genre_id = ?";
                jdbcTemplate.update(sql2, film.getId(), genre.getId());
            }
            return getFilm(film.getId()).orElseThrow();
        } catch (NullPointerException e) {
            return getFilm(film.getId()).orElseThrow();
        }
    }

    public Optional<Film> getFilm(int id) {
        String sqlQuery = "SELECT id, name, description, release_date, duration, mpa " +
                "FROM FILMS WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Film> getFilms() {
        String sqlQuery = "SELECT id, name, description, release_date, duration, mpa " +
                "FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
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
        String sqlQuery = "SELECT id, name, description, release_date, duration, mpa, COUNT(user_id) as likes " +
                "FROM FILMS AS f LEFT OUTER JOIN likes AS l ON f.id = l.film_id" +
                " GROUP BY id ORDER BY likes DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(mpaStorage.getRatingById(resultSet.getInt("mpa")));
        film.setGenres(getGenresByFilmId(resultSet.getInt("id")));
        film.setLikes(getLikes(film.getId()));
        return film;
    }

    private Set<Integer> getLikes(int id) {
        String sqlQuery = "SELECT FILM_ID FROM likes WHERE USER_ID = ?";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("FILM_ID"), id));
    }

    public Set<Genre> getGenresByFilmId(int id) {
        String sql = "SELECT genre_id FROM FILM_GENRE WHERE film_id = ?";
        List<Integer> response = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("genre_id"), id);
        Collections.sort(response);
        Set<Genre> genres = new HashSet<>();
        for (Integer genreId : response) {
            genres.add(genreStorage.getGenreById(genreId));
        }
        return genres;
    }
}
