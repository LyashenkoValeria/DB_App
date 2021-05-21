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
        return when(id){
            1 -> book1
            2 -> book2
            3 -> book3
            4 -> book4
            5 -> book5
            else -> book6
        }
    }

    fun getFilm(id: Int): Film {
        return when(id){
            1 -> film1
            2 -> film2
            3 -> film3
            else -> film3
        }
    }

    fun getMusic(id: Int): Music {
        return when(id){
            1 -> music1
            2 -> music2
            3 -> music3
            else -> music3
        }
    }

    fun getBookList(): List<Content> {
        // TODO: 13.05.2021 Выдирать из БД
        val bookList = listOf(book1, book2, book3, book4, book5, book6)
        val resultList = mutableListOf<Content>()
        for (book in bookList) {
            val rating = book.getId()/2.toDouble() // TODO: 13.05.2021 Выдирать рейтинг и постеры
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
            val rating = film.getId()*1.5// TODO: 13.05.2021 Выдирать рейтинг
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
            val rating = music.getId()*1.5 // TODO: 13.05.2021 Выдирать рейтинг
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


    val people = People(1, "Author 1", 2000, "Режиссёр")
    val people2 = People(2, "Author 2", 2000, "Сценарист")
    val people3 = People(3, "Author 3", 2000, "Актёр")
    val genre = Genre(1, "genre 1", "Это genre 1")
    val genre2 = Genre(2, "genre 2", "Это genre 2")
    val genre3 = Genre(3, "genre 3", "Это genre 3")
    val peopleList = listOf(people)
    val peopleList2 = listOf(people2,people3)
    val peopleList3 = listOf(people, people3)
    val peopleAll = listOf(people, people2,people3)
    val genreList = listOf(genre)
    val genreList2 = listOf(genre, genre2)
    val genreList3 = listOf(genre3, genre2)

    fun getPeopleListTest(): List<People>{
        return peopleAll
    }

    // -------------------------------------------------------------------------------------------------
    val bookSeries = BookSeries(1, "book series 1", "Это book series 1")
    val book1 = Book(
        1,
        "book 1",
        1500,
        "Это book 1",
        "poster",
        bookSeries,
        peopleList,
        genreList
    )
    val book2 = Book(
        2,
        "book 2",
        1905,
        "Это book 2",
        "poster",
        bookSeries,
        peopleList3,
        genreList2
    )
    val book3 = Book(
        3,
        "text",
        2021,
        "Это book 3",
        "poster",
        bookSeries,
        peopleList,
        genreList3
    )

    val book4 = Book(
        4,
        "text 2",
        1800,
        "Это book 1",
        "poster",
        bookSeries,
        peopleList2,
        genreList3
    )
    val book5 = Book(
        5,
        "test",
        1905,
        "Это book 2",
        "poster",
        bookSeries,
        peopleList3,
        genreList2
    )
    val book6 = Book(
        6,
        "test 2",
        1606,
        "Это book 3",
        "poster",
        bookSeries,
        peopleList2,
        genreList
    )

    // -------------------------------------------------------------------------------------------------
    val artist = Artist(1, "Singer1", "Певец")
    val artist2 = Artist(2, "Singer2", "Певец")
    val artist3 = Artist(3, "Group1", "Группа")
    val artist4 = Artist(4, "Group2", "Группа")
    val artistList = listOf(artist, artist2)
    val artistList2 = listOf(artist2, artist4)
    val artistList3 = listOf(artist3)

    val music1 = Music(1, "music 1", 1501, 8, artistList, genreList)
    val music2 = Music(2, "music 2", 2020, 2, artistList2, genreList2)
    val music3 = Music(3, "music 3", 1600, 3, artistList3, genreList3)

    // -------------------------------------------------------------------------------------------------
    val filmSeries = FilmSeries(1, "film series 1", "Это film series 1")
    val film1 = Film(
        1,
        "film 1",
        1950,
        128,
        "Это film 1",
        "poster",
        filmSeries,
        book2,
        music3,
        peopleList,
        genreList
    )
    val film2 = Film(
        2,
        "film 2",
        1900,
        45,
        "Это film 2",
        "poster",
        filmSeries,
        book2,
        music3,
        peopleList3,
        genreList
    )
    val film3 = Film(
        3,
        "film 3",
        2000,
        90,
        "Это film 3",
        "poster",
        filmSeries,
        book2,
        music3,
        peopleList2,
        genreList3
    )
}