/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app

import com.example.db_app.adapters.ContentAdapter.Type
import com.example.db_app.dataClasses.*

class WebClient {

    fun getRating(type: Type, id: Int): Double {
        return 5.0
    }

    fun getBook(id: Int): Book {
        return book1
    }

    fun getFilm(id: Int): Film {
        return film1
    }

    fun getMusic(id: Int): Music {
        return music1
    }

    fun getBookList(): List<Content> {
        // TODO: 13.05.2021 Выдирать из БД
        val bookList = listOf(book1, book2, book3)
        val resultList = mutableListOf<Content>()
        for (book in bookList) {
            val rating = 5.0 // TODO: 13.05.2021 Выдирать рейтинг и постеры
            val poster = "poster"
            resultList.add(
                Content(
                    book.getId(),
                    book.getName(),
                    rating,
                    book.getYear(),
                    book.getGenres(),
                    poster
                )
            )
        }
        return resultList
    }

    fun getFilmList(): List<Content> {
        val filmList = listOf(film1, film2, film3)
        val resultList = mutableListOf<Content>()
        for (film in filmList) {
            val rating = 5.0 // TODO: 13.05.2021 Выдирать рейтинг
            val poster = "poster"
            resultList.add(
                Content(
                    film.getId(),
                    film.getName(),
                    rating,
                    film.getYear(),
                    film.getGenres(),
                    poster
                )
            )
        }
        return resultList
    }

    fun getMusicList(): List<Content> {
        val musicList = listOf(music1, music2, music3)
        val resultList = mutableListOf<Content>()
        for (music in musicList) {
            val rating = 5.0 // TODO: 13.05.2021 Выдирать рейтинг
            val poster = "poster"
            resultList.add(
                Content(
                    music.getId(),
                    music.getName(),
                    rating,
                    music.getYear(),
                    music.getGenres(),
                    poster
                )
            )
        }
        return resultList
    }


    val people = People(1, "Человек Первый", 1111, null)
    val genre = Genre(1, "genre 1", "Это genre 1")
    val peopleList = listOf(people)
    val genreList = listOf(genre)

    // -------------------------------------------------------------------------------------------------
    val bookSeries = BookSeries(1, "book series 1", "Это book series 1")
    val book1 = Book(
        1,
        "book 1",
        1111,
        "Это book 1",
        "poster",
        bookSeries,
        peopleList,
        genreList
    )
    val book2 = Book(
        2,
        "test",
        2222,
        "Это book 2",
        "poster",
        bookSeries,
        peopleList,
        genreList
    )
    val book3 = Book(
        3,
        "text",
        3333,
        "Это book 3",
        "poster",
        bookSeries,
        peopleList,
        genreList
    )

    // -------------------------------------------------------------------------------------------------
    val artist = Artist(1, "artist 1", "Это artist 1")
    val artistList = listOf(artist)

    val music1 = Music(1, "music 1", 1111, 1, artistList, genreList)
    val music2 = Music(2, "music 2", 1111, 2, artistList, genreList)
    val music3 = Music(3, "music 2", 1111, 3, artistList, genreList)

    // -------------------------------------------------------------------------------------------------
    val filmSeries = FilmSeries(1, "film series 1", "Это film series 1")
    val film1 = Film(
        1,
        "film 1",
        1111,
        10,
        "Это film 1",
        "poster",
        filmSeries,
        book1,
        music1,
        peopleList,
        genreList
    )
    val film2 = Film(
        2,
        "film 2",
        2222,
        20,
        "Это film 2",
        "poster",
        filmSeries,
        book2,
        music2,
        peopleList,
        genreList
    )
    val film3 = Film(
        3,
        "film 3",
        3333,
        30,
        "Это film 3",
        "poster",
        filmSeries,
        book2,
        music3,
        peopleList,
        genreList
    )
}