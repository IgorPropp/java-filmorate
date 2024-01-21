package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
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
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Film createFilm(Film film) {
        String sql = "insert into films(name, description, release_date, duration, mpa) " +
                "values (?, ?, ?, ?, ?)";
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
                String sql2 = "insert into FILM_GENRE set film_id = ?, genre_id = ?";
                jdbcTemplate.update(sql2, film.getId(), genre.getId());
            }
            return getFilm(film.getId()).orElseThrow();
        } catch (NullPointerException e) {
            return getFilm(film.getId()).orElseThrow();
        }
    }

    public Film updateFilm(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa = ? where id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        String sqlDelete = "delete from FILM_GENRE where film_id = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        try {
            for (Genre genre : film.getGenres()) {
                String sql2 = "insert into FILM_GENRE set film_id = ?, genre_id = ?";
                jdbcTemplate.update(sql2, film.getId(), genre.getId());
            }
            return getFilm(film.getId()).orElseThrow();
        } catch (NullPointerException e) {
            return getFilm(film.getId()).orElseThrow();
        }
    }

    public Optional<Film> getFilm(int id) {
        String sqlQuery = "select id, name, description, release_date, duration, mpa " +
                "from FILMS where id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
    }

    public List<Film> getFilms() {
        String sqlQuery = "select id, name, description, release_date, duration, mpa " +
                "from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    public boolean hasFilm(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM FILMS WHERE id = ?)", Boolean.class, id));
    }

    public Film putLike(int id, int userId) {
        String sqlQuery = "insert into likes(user_id, film_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, id);
        return getFilm(id).orElseThrow();
    }

    public Film deleteLike(int id, int userId) {
        String sqlQuery = "delete from LIKES where user_id = ? and film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, id);
        return getFilm(id).orElseThrow();
    }

    public List<Film> getTopFilms(int count) {
        String sqlQuery = "select id, name, description, release_date, duration, mpa, COUNT(user_id) as likes " +
                "from FILMS as f left outer join likes as l on f.id = l.film_id group by id order by likes desc LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    public Set<Mpa> getRatings() {
        String sqlQuery = "select id, rating from MPA";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, this::mapRowToRating)).stream().sorted(Comparator.comparingInt(Mpa::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Mpa getRatingById(int id) {
        String sqlQuery = "select id, rating from MPA where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    public boolean hasRating(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM MPA WHERE id = ?)", Boolean.class, id));
    }

    public Set<Genre> getGenres() {
        String sqlQuery = "select id, genre from GENRE";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, this::mapRowToGenre)).stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Genre getGenreById(int id) {
        String sqlQuery = "select id, genre from GENRE where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    public Set<Genre> getGenresByFilmId(int id) {
        String sql = "select genre_id from FILM_GENRE where film_id = ?";
        List<Integer> response = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("genre_id"), id);
        Collections.sort(response);
        Set<Genre> genres = new HashSet<>();
        for (Integer genreId : response) {
            genres.add(getGenreById(genreId));
        }
        return genres;
    }

    public boolean hasGenre(int id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM GENRE WHERE id = ?)", Boolean.class, id));
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(getRatingById(resultSet.getInt("mpa")));
        film.setGenres(getGenresByFilmId(resultSet.getInt("id")));
        film.setLikes(getLikes(film.getId()));
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("genre"));
        return genre;
    }

    private Mpa mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("rating"));
        return mpa;
    }

    private Set<Integer> getLikes(int id) {
        String sqlQuery = "select FILM_ID from likes where USER_ID = ?";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("FILM_ID"), id));
    }
}
