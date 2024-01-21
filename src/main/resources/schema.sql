DROP TABLE IF EXISTS films CASCADE;

DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS friends CASCADE;

DROP TABLE IF EXISTS film_genre CASCADE;

DROP TABLE IF EXISTS likes CASCADE;

CREATE TABLE IF NOT EXISTS films (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name varchar(255),
    description varchar(255),
    release_date date,
    duration integer,
    mpa integer NOT NULL REFERENCES mpa(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS users (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    email varchar(255),
    login varchar(255),
    name varchar(255),
    birthday date
    );

CREATE TABLE IF NOT EXISTS likes (
    user_id integer NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    film_id integer NOT NULL REFERENCES films(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
    );

CREATE TABLE IF NOT EXISTS friends (
    user_a integer NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_b integer NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    accepted boolean,
    PRIMARY KEY (user_a, user_b)
    );

CREATE TABLE IF NOT EXISTS genre (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    genre varchar(255)
    );

CREATE TABLE IF NOT EXISTS film_genre (
    film_id integer NOT NULL REFERENCES films(id) ON DELETE CASCADE,
    genre_id integer NOT NULL REFERENCES genre(id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS mpa (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    rating varchar(255)
    );

INSERT INTO mpa (rating)
SELECT ('G')
WHERE
    NOT EXISTS (
        SELECT rating FROM mpa WHERE rating = 'G'
    );

INSERT INTO mpa (rating)
SELECT ('PG')
WHERE
    NOT EXISTS (
        SELECT rating FROM mpa WHERE rating = 'PG'
    );

INSERT INTO mpa (rating)
SELECT ('PG-13')
WHERE
    NOT EXISTS (
        SELECT rating FROM mpa WHERE rating = 'PG-13'
    );

INSERT INTO mpa (rating)
SELECT ('R')
WHERE
    NOT EXISTS (
        SELECT rating FROM mpa WHERE rating = 'R'
    );

INSERT INTO mpa (rating)
SELECT ('NC-17')
WHERE
    NOT EXISTS (
        SELECT rating FROM mpa WHERE rating = 'NC-17'
    );

INSERT INTO genre (genre)
SELECT ('Комедия')
WHERE
    NOT EXISTS (
        SELECT genre FROM genre WHERE genre = 'Комедия'
    );

INSERT INTO genre (genre)
SELECT ('Драма')
WHERE
    NOT EXISTS (
        SELECT genre FROM genre WHERE genre = 'Драма'
    );

INSERT INTO genre (genre)
SELECT ('Мультфильм')
WHERE
    NOT EXISTS (
        SELECT genre FROM genre WHERE genre = 'Мультфильм'
    );

INSERT INTO genre (genre)
SELECT ('Триллер')
WHERE
    NOT EXISTS (
        SELECT genre FROM genre WHERE genre = 'Триллер'
    );

INSERT INTO genre (genre)
SELECT ('Документальный')
WHERE
    NOT EXISTS (
        SELECT genre FROM genre WHERE genre = 'Документальный'
    );

INSERT INTO genre (genre)
SELECT ('Боевик')
WHERE
    NOT EXISTS (
        SELECT genre FROM genre WHERE genre = 'Боевик'
    );
