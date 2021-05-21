package com.example.db_app

import android.provider.ContactsContract
import com.example.db_app.dataClasses.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class WebClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.fbear.ru:8080/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: WebClientService = retrofit.create(WebClientService::class.java)

    fun getApi() = api
}


interface WebClientService {

    @GET("book/{id}")
    fun getBookForUser(
        @Path("id") bookId: Int,
        @Header("Authorization") token: String,
        @Query("expanded") expanded: Boolean = true
    ): Call<Book>

    @GET("book/{id}")
    fun getBookContent(@Path("id") id: Int, @Header("Authorization") token: String): Call<Content>

    @GET("film/{id}")
    fun getFilmContent(@Path("id") id: Int, @Header("Authorization") token: String): Call<Content>

    @GET("music/{id}")
    fun getMusicContent(@Path("id") id: Int, @Header("Authorization") token: String): Call<Content>

    @GET("film/{id}")
    fun getFilmForUser(
        @Path("id") filmId: Int,
        @Header("Authorization") token: String,
        @Query("expanded") expanded: Boolean = true
    ): Call<Film>

    @GET("music/{id}")
    fun getMusicForUser(
        @Path("id") musicId: Int,
        @Header("Authorization") token: String,
        @Query("expanded") expanded: Boolean = true,
    ): Call<Music>

    @GET("book")
    fun getBookList(@Header("Authorization") token: String): Call<List<ContentIdName>>

    @GET("film")
    fun getFilmList(@Header("Authorization") token: String): Call<List<ContentIdName>>

    @GET("music")
    fun getMusicList(@Header("Authorization") token: String): Call<List<ContentIdName>>


    @GET("book_genre")
    fun getBookGenre(@Header("Authorization") token: String): Call<List<Genre>>

    @GET("music_genre")
    fun getMusicGenre(@Header("Authorization") token: String): Call<List<Genre>>

    @GET("film_genre")
    fun getFilmGenre(@Header("Authorization") token: String): Call<List<Genre>>

    /** ------------------------------ Работа с топами ------------------------------ **/

    // Получение списка топов по типу контента
    @GET("top/{type}")
    fun getTopsByType(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>


    /** ------------------------------ Работа с юзером ------------------------------ **/

    // Авторизация пользователя (возврат - токен пользователя или null, если такого нет)
    @GET("user/auth")
    fun auth(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Map<String, String?>>

    // Регистрация пользователя
    @POST("user/reg")
    fun reg(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("email") email: String
    ): Call<Map<String, Int>>

    // Изменение отметки просмотренного и рейтинга контента
    @POST("user/update/{type}/{content_id}")
    fun changeViewedContent(
        @Path("type") type: String,
        @Path("content_id") contentId: Int,
        @Header("Authorization") token: String,
        @Query("viewed") viewed: Boolean,
        @Query("rating") rating: Int? = null
    ): Call<ResponseBody>

    // Получение информации о пользователе
    @GET("user/info")
    fun getUserInfo(@Header("Authorization") token: String): Call<User>

    // Обновление username
    @POST("user/update/username")
    fun updateUsername(
        @Query("new_username") newUsername: String,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>

    // Обновление email
    @POST("user/update/email")
    fun updateEmail(
        @Query("new_email") newEmail: String,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>

    // Обновление пароля
    @POST("user/update/password")
    fun updatePass(
        @Query("old_password") oldPass: String,
        @Query("new_password") newPass: String,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>





    // TODO: 14.05.2021
    @GET("")
    fun getBookLikeGenre(@Header("Authorization") token: String): Call<List<Genre>>

    @GET("")
    fun getFilmLikeGenre(@Header("Authorization") token: String): Call<List<Genre>>

    @GET("")
    fun getMusicLikeGenre(@Header("Authorization") token: String): Call<List<Genre>>

    // TODO: 16.05.2021 Как это вообще оформить?
//    @SET("")
    fun changeLikeBookGenre(userId: Int, likeBookGenre: List<Genre>): Call<Boolean>

    // TODO: 16.05.2021 Как это вообще оформить?
//    @SET("")
    fun changeLikeFilmGenre(userId: Int, likeFilmGenre: List<Genre>): Call<Boolean>

    // TODO: 16.05.2021 Как это вообще оформить?
//    @SET("")
    fun changeLikeMusicGenre(userId: Int, likeMusicGenre: List<Genre>): Call<Boolean>


    //    val book = api.getBook(1).execute().body()
//    fun getMusicGenreById(@Path("id") id: Int): Call<Genre>

//    @GET("book")
    fun getBooksOfTop(topId: Int): Call<List<ContentIdName>>

//    @GET("film")
    fun getFilmOfTop(topId: Int): Call<List<ContentIdName>>

//    @GET("music")
    fun getMusicOfTop(topId: Int): Call<List<ContentIdName>>
}

//fun main() {
//    print(book?.getName())
//}


//val people = People(1, "Человек Первый", 1111, null)
val genre = Genre(1, "genre 1", "Это genre 1")
//val peopleList = listOf(people)
//val genreList = listOf(genre)
//
//// -------------------------------------------------------------------------------------------------
//val bookSeries = BookSeries(1, "book series 1", "Это book series 1")
//val book1 = Book(
//    1,
//    "book 1",
//    1111,
//    "Это book 1",
//    "poster",
//    5.0,
//    bookSeries,
//    peopleList,
//    genreList
//)
//val book2 = Book(
//    2,
//    "book 2",
//    2222,
//    "Это book 2",
//    "poster",
//    5.0,
//    bookSeries,
//    peopleList,
//    genreList
//)
//val book3 = Book(
//    3,
//    "book 3",
//    3333,
//    "Это book 3",
//    "poster",
//    5.0,
//    bookSeries,
//    peopleList,
//    genreList
//)
//
//// -------------------------------------------------------------------------------------------------
//val artist = Artist(1, "artist 1", "Это artist 1")
//val album = MusicAlbum(1, "music album 1", 1111, null)
//val artistList = listOf(artist)
//
//val music1 = Music(1, "music 1", 1111, 1, 5.0, artistList, album, genreList)
//val music2 = Music(2, "music 2", 1111, 2, 5.0, artistList, album, genreList)
//val music3 = Music(3, "music 2", 1111, 3, 5.0, artistList, album, genreList)
//
//// -------------------------------------------------------------------------------------------------
//val filmSeries = FilmSeries(1, "film series 1", "Это film series 1")
//val listMusic = listOf(music1)
//val film1 = Film(
//    1,
//    "film 1",
//    1111,
//    10,
//    "Это film 1",
//    "poster",
//    5.0,
//    filmSeries,
//    book1,
//    listMusic,
//    peopleList,
//    genreList
//)
//val film2 = Film(
//    2,
//    "film 2",
//    2222,
//    20,
//    "Это film 2",
//    "poster",
//    5.0,
//    filmSeries,
//    book2,
//    listMusic,
//    peopleList,
//    genreList
//)
//val film3 = Film(
//    3,
//    "film 3",
//    3333,
//    30,
//    "Это film 3",
//    "poster",
//    5.0,
//    filmSeries,
//    book2,
//    listMusic,
//    peopleList,
//    genreList
//)