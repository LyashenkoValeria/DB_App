package com.example.db_app

import com.example.db_app.dataClasses.*
import okhttp3.ResponseBody
import retrofit2.Call
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

    // Получение книги, фильма или музыки (в зависимости от type) как Content
    @GET("{type}/{id}")
    fun getContentById(
        @Path("type") type: String,
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<Content>

    @GET("book/{id}")
    fun getBookForUser(
        @Path("id") bookId: Int,
        @Header("Authorization") token: String,
        @Query("expanded") expanded: Boolean = true
    ): Call<Book>

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

    // Получение списка книг, фильмов или музыки (в зависимости от type) как <List<ContentIdName>
    @GET("{type}")
    fun getContentListByType(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    // Возврат жанров по типу
    @GET("{type}_genre")
    fun getGenreByType(
        @Path("type") type: String,
        @Header("Authorization") token: String): Call<List<Genre>>


    /** ------------------------------ Работа с топами ------------------------------ **/

    // Получение списка топов по типу контента
    @GET("top/{type}")
    fun getTopsByType(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    @GET("top/{type}/{id}")
    fun getTopByTypeAndId(
        @Path("type") type: String,
        @Path("id") topId: Int,
        @Header("Authorization") token: String
    ): Call<Top>


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
