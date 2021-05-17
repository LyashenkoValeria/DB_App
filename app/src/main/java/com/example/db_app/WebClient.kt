package com.example.db_app

import com.example.db_app.dataClasses.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class WebClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.fbear.ru:8080/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: WebClientService = retrofit.create(WebClientService::class.java)

    fun getApi() = api

//    fun getContentList(type: Type): List<Content> {
//        val client = retrofit.create(WebClientService::class.java)
//        val call = when (type) {
//            Type.BOOK -> client.getBookList()
//            Type.FILM -> client.getFilmList()
//            Type.MUSIC -> client.getMusicList()
//        }
//
//        var contentList = listOf<Content>(Content(1, "kek", 5.0, 1, listOf("k"), "f"))
//
//        call.enqueue(object : Callback<List<Content>> {
//            override fun onResponse(call: Call<List<Content>>, response: Response<List<Content>>) {
//                contentList = response.body()!!
//            }
//
//            override fun onFailure(call: Call<List<Content>>, t: Throwable) {
//                Log.d("db", "Response = $t")
//            }
//        })
//        return contentList
//    }

//    fun getContentList(type: ContentAdapter.Type) =
//        when (type) {
//            ContentAdapter.Type.BOOK -> getBookList()
//            ContentAdapter.Type.FILM -> getFilmList()
//            ContentAdapter.Type.MUSIC -> getMusicList()
//        }

//    fun getBookList(): List<Content>? = api.getBookList().execute().body()
//
//    fun getFilmList(): List<Content>? = api.getFilmList().execute().body()
//
//    fun getMusicList(): List<Content>? = api.getMusicList().execute().body()

    fun getBook(id: Int): Book? {
        val client = retrofit.create(WebClientService::class.java)
        val call = client.getBook(id)
        val result = null
        // TODO: 14.05.2021
        return null
    }

    fun getFilm(id: Int): Film? = api.getFilm(id).execute().body()

    fun getMusic(id: Int): Music? = api.getMusic(id).execute().body()

//    fun getMusicGenreById(id: Int): Genre? = api.getMusicGenreById(id).execute().body()


    fun getMusicViewed(id: Int): Boolean? {
        return true         // TODO: 14.05.2021
    }

    fun getGenreList(type: Type) =
        when (type) {
            Type.BOOK -> getBookGenre()
            Type.FILM -> getFilmGenre()
            Type.MUSIC -> getMusicGenre()
        }

    private fun getBookGenre(): List<Genre> {
        return listOf(genre) // TODO: 14.05.2021
    }

    private fun getFilmGenre(): List<Genre> {
        return listOf(genre) // TODO: 14.05.2021
    }

    private fun getMusicGenre(): List<Genre> {
        return listOf(genre) // TODO: 14.05.2021
    }

    fun getLikeGenre(type: Type) =
        when (type) {
            Type.BOOK -> getLikeBookGenre()
            Type.FILM -> getLikeFilmGenre()
            Type.MUSIC -> getLikeMusicGenre()
        }

    private fun getLikeBookGenre(): List<Genre>? {
        return listOf(genre) // TODO: 14.05.2021
    }

    private fun getLikeFilmGenre(): List<Genre>? {
        return listOf(genre) // TODO: 14.05.2021
    }

    private fun getLikeMusicGenre(): List<Genre>? {
        return listOf(genre) // TODO: 14.05.2021
    }

    fun addLikeGenre(type: Type, id: Int) {
        when (type) {
            Type.BOOK -> addLikeBookGenre(id)
            Type.FILM -> addLikeFilmGenre(id)
            Type.MUSIC -> addLikeMusicGenre(id)
        }
    }

    fun changeLikeGenre(type: Type, genreList: List<Genre>) {
        val likeListDB = getLikeGenre(type)!!.toMutableList()
        genreList.forEach {
            addLikeGenre(type, it.getId())
            if (likeListDB.contains(it))
                likeListDB.remove(it)
        }

        likeListDB.forEach {
            delLikeGenre(type, it.getId())
        }
    }

    private fun addLikeBookGenre(idGenre: Int) {
        // TODO: 14.05.2021
    }

    private fun addLikeFilmGenre(idGenre: Int) {
        // TODO: 14.05.2021
    }

    private fun addLikeMusicGenre(idGenre: Int) {
        // TODO: 14.05.2021
    }


    fun delLikeGenre(type: Type, idGenre: Int) {
        when (type) {
            Type.BOOK -> delLikeBookGenre(idGenre)
            Type.FILM -> delLikeFilmGenre(idGenre)
            Type.MUSIC -> delLikeMusicGenre(idGenre)
        }
    }

    private fun delLikeBookGenre(idGenre: Int) {

    }

    private fun delLikeFilmGenre(idGenre: Int) {

    }

    private fun delLikeMusicGenre(idGenre: Int) {

    }

}


interface WebClientService {

//    GET https://localhost:8080/api/user/auth?username=teacons&password=123321

    // todo возвращает id пользователя или null, если такого нет
    @GET("user/auth")
    fun auth(@Query("username") username: String, @Query("password") password: String): Call<Int?>

    //    https://api.fbear.ru:8080/api/user/reg?username=Juniell1&password=123321&email=juniell@fbear.ri
    // TODO: 17.05.2021 написать дата класс? выход - этот класс? или мапа?
    @GET("user/reg")
    fun reg(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("email") email: String
    ): Call<Int?>

    @GET("book/{id}")
    fun getBook(@Path("id") id: Int): Call<Book>

    @GET("book/{id}")
    fun getBookContent(@Path("id") id: Int): Call<Content>

    @GET("film/{id}")
    fun getFilmContent(@Path("id") id: Int): Call<Content>

    @GET("music/{id}")
    fun getMusicContent(@Path("id") id: Int): Call<Content>

    @GET("film/{id}")
    fun getFilm(@Path("id") id: Int): Call<Film>

    @GET("music/{id}")
    fun getMusic(@Path("id") id: Int): Call<Music>

    @GET("book")
    fun getBookList(): Call<List<ContentIdName>>

    @GET("film")
    fun getFilmList(): Call<List<ContentIdName>>

    @GET("music")
    fun getMusicList(): Call<List<ContentIdName>>

//    @GET("music_genre/{id}")

    @GET("book_genre")
    fun getBookGenre(): Call<List<Genre>>

    @GET("music_genre")
    fun getMusicGenre(): Call<List<Genre>>

    @GET("film_genre")
    fun getFilmGenre(): Call<List<Genre>>

    // TODO: 14.05.2021
    @GET("")
    fun getBookLikeGenre(): Call<List<Genre>>

    @GET("")
    fun getFilmLikeGenre(): Call<List<Genre>>

    @GET("")
    fun getMusicLikeGenre(): Call<List<Genre>>

    // TODO: 16.05.2021 Как это вообще оформить?
//    @SET("")
    fun changeLikeBookGenre(userId: Int, likeBookGenre: List<Genre>): Call<Boolean>

    // TODO: 16.05.2021 Как это вообще оформить?
//    @SET("")
    fun changeLikeFilmGenre(userId: Int, likeFilmGenre: List<Genre>): Call<Boolean>

    // TODO: 16.05.2021 Как это вообще оформить?
//    @SET("")
    fun changeLikeMusicGenre(userId: Int, likeMusicGenre: List<Genre>): Call<Boolean>

    @GET("")
    fun getTopsBook(): Call<List<Top>>

    @GET("")
    fun getTopsFilm(): Call<List<Top>>

    @GET("")
    fun getTopsMusic(): Call<List<Top>>

    //    val book = api.getBook(1).execute().body()
//    fun getMusicGenreById(@Path("id") id: Int): Call<Genre>
    @GET("book")
    fun getBooksOfTop(topId: Int): Call<List<ContentIdName>>

    @GET("film")
    fun getFilmOfTop(topId: Int): Call<List<ContentIdName>>

    @GET("music")
    fun getMusicOfTop(topId: Int): Call<List<ContentIdName>>
}

//fun main() {
//    print(book?.getName())
//}


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
    5.0,
    bookSeries,
    peopleList,
    genreList
)
val book2 = Book(
    2,
    "book 2",
    2222,
    "Это book 2",
    "poster",
    5.0,
    bookSeries,
    peopleList,
    genreList
)
val book3 = Book(
    3,
    "book 3",
    3333,
    "Это book 3",
    "poster",
    5.0,
    bookSeries,
    peopleList,
    genreList
)

// -------------------------------------------------------------------------------------------------
val artist = Artist(1, "artist 1", "Это artist 1")
val album = MusicAlbum(1, "music album 1", 1111, null)
val artistList = listOf(artist)

val music1 = Music(1, "music 1", 1111, 1, 5.0, artistList, album, genreList)
val music2 = Music(2, "music 2", 1111, 2, 5.0, artistList, album, genreList)
val music3 = Music(3, "music 2", 1111, 3, 5.0, artistList, album, genreList)

// -------------------------------------------------------------------------------------------------
val filmSeries = FilmSeries(1, "film series 1", "Это film series 1")
val listMusic = listOf(music1)
val film1 = Film(
    1,
    "film 1",
    1111,
    10,
    "Это film 1",
    "poster",
    5.0,
    filmSeries,
    book1,
    listMusic,
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
    5.0,
    filmSeries,
    book2,
    listMusic,
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
    5.0,
    filmSeries,
    book2,
    listMusic,
    peopleList,
    genreList
)