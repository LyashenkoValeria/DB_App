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

    // Получение следующего слайса (из size элементов) контента (если id = null - для первого)
    @GET("{type}/slice")
    fun getNextPartContentByType(
        @Path("type") type: String,
        @Query("id") id: Int?,
        @Query("size") size: Int,
        @Header("Authorization") token: String
    ): Call<List<Int>>



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
        @Header("Authorization") token: String
    ): Call<List<Genre>>

    // Возврат лайкнутыых жанров по типу
    @GET("user/genre/{type}")
    fun getLikeGenreByType(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<Int>>

    // Изменение лайкнутыых жанров по типу
    @POST("user/update/genre")
    fun changeLikeGenre(
        @Query("book_genres") likeBookGenre: List<Int>,
        @Query("film_genres") likeFilmGenre: List<Int>,
        @Query("music_genres") likeMusicGenre: List<Int>,
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    // Получение списка людей по типу (BOOK и MUSIC)
    @GET("people/{type}")
    fun getPeopleByType(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    @GET("people/functions")
    fun getFunctions(@Header("Authorization") token: String): Call<List<ContentIdName>>

    // Запись книги в бд
    @POST("moderate/book")
    fun saveBook(
        @Query("name") name: String,
        @Query("year") year: Int,
        @Query("description") desc: String,
        @Query("poster") poster: String? = null,
        @Query("book_series") bookSeries: String? = null,
        @Query("authors") authors: List<String>,
        @Query("genres") genres: List<Int>,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>

    // Запись фильма в бд
    @POST("moderate/film")
    fun saveFilm(
        @Query("name") name: String,
        @Query("year") year: Int,
        @Query("duration") duration: Int,
        @Query("description") desc: String,
        @Query("poster") poster: String? = null,
        @Query("film_series") filmSeries: String? = null,
        @Query("book") book: String? = null,
        @Query("music") music: String? = null,
        @Query("peoples") peoples: String,
        @Query("genres") genres: List<Int>,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>

    // Запись песни в бд и возврат его id, если всё ок
    @POST("moderate/music")
    fun saveMusic(
        @Query("name") name: String,
        @Query("year") year: Int,
        @Query("duration") duration: Int,
        @Query("poster") poster: String? = null,
        @Query("artists") artists: List<String>,
        @Query("albums") albums: String?,
        @Query("genres") genres: List<Int>,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>

    @POST("moderate/top/{type}")
    fun saveTop(
        @Path("type") type: String,
        @Query("name") name: String,
        @Query("contents") contentList: String,
        @Header("Authorization") token: String
    ): Call<Map<String, Int>>

    /** ------------------------------ Работа с топами ------------------------------ **/

    // Получение следующего слайса (из size элементов) контента (если id = null - для первого)
    @GET("top/{type}")
    fun getNextPartTopsByType(
        @Path("type") type: String,
        @Query("id") id: Int?,
        @Query("size") size: Int,
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

    //Получение списка просмотренных
    @GET("user/{type}")
    fun getViewedByType(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    //Получение списка рекомендации
    @GET("user/recommended/{type}")
    fun getRecommend(
        @Path("type") type: String,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    /** ------------------------------ Работа с фильтраией ------------------------------ **/

    //Получение списка фильтрации для книг
    @GET("book/filter")
    fun getFilterBook(
        @Query("year_down") year_down: Int,
        @Query("year_up") year_up: Int,
        @Query("rating_down") rating_down: Int,
        @Query("rating_up") rating_up: Int,
        @Query("authors") authorsId: List<Int>?,
        @Query("genres") genresId: List<Int>?,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    //Получение списка фильтрации для фильмов
    @GET("film/filter")
    fun getFilterFilm(
        @Query("year_down") year_down: Int,
        @Query("year_up") year_up: Int,
        @Query("rating_down") rating_down: Int,
        @Query("rating_up") rating_up: Int,
        @Query("duration_down") duration_down: Int,
        @Query("duration_up") duration_up: Int,
        @Query("actors") actorsId: List<Int>?,
        @Query("creators") creatorsId: List<Int>?,
        @Query("genres") genresId: List<Int>?,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    //Получение списка фильтрации для фильмов
    @GET("music/filter")
    fun getFilterMusic(
        @Query("genres") genresId: List<Int>?,
        @Query("artists") artistsId: List<Int>?,
        @Query("duration_down") duration_down: Int,
        @Query("duration_up") duration_up: Int,
        @Query("year_down") year_down: Int,
        @Query("year_up") year_up: Int,
        @Query("rating_down") rating_down: Int,
        @Query("rating_up") rating_up: Int,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>

    // Получение людей фильмов (actor = true - актёры, = false - все остальные)
    @GET("people/film")
    fun getPeopleForFilm(
        @Query("actors") actors: Boolean = true,
        @Header("Authorization") token: String
    ): Call<List<ContentIdName>>
}


//fun main() {
//    val call = WebClient().getApi().saveFilm("test", 1, 2, "kek", null, null, null, null, mapOf("Kek" to 123, "lol" to 234), listOf(1), "kekToken")
//    call.enqueue(object : Callback<Int> {
//        override fun onResponse(
//            call: Call<Int>,
//            response: Response<Int>
//        ) {
//            1+1
//        }
//
//        override fun onFailure(call: Call<Int>, t: Throwable) {
//            Log.d("db", "Response = $t")
////            (requireActivity() as MainActivity).makeToast("Кажется, что-то пошло совсем не так.")
//        }
//    })
//    1 + 1
//}
