package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.Book
import com.example.db_app.dataClasses.Film
import com.example.db_app.dataClasses.Music
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_content.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentFragment : Fragment() {
    private var type = Type.BOOK
    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getInt("id") ?: 1
        val t = arguments?.getInt("type")
        type = when (t) {
            1 -> {
                drawFilm(id)
                Type.FILM
            }
            2 -> {
                drawMusic(id)
                Type.MUSIC
            }
            else -> {
                drawBook(id)
                Type.BOOK
            }
        }
    }

    // TODO: 13.05.2021 Отмечать просмотренным

    private fun drawBook(id: Int) {
        val call = webClient.getBook(id)
        call.enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                val book = response.body()!!
                drawViewBook(book)
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun drawViewBook(book: Book) {
        name.text = book.getName()
        year.text = book.getYear().toString()
        book_music_author.text = book.getAuthorsString()
        genre.text = book.getGenreString()
        book_series.run {
            val bookSeries = book.getBookSeries()
            visibility = if (bookSeries != null) {
                book_series_row.visibility = View.VISIBLE
                text = bookSeries.getName()
                View.VISIBLE
            } else
                View.GONE
        }

        content_top.text = "Топ 100 всего-всего"
        content_pos.text = "1"
        book_film_desc.text = book.getDesc()
        rating.rating = book.getRating().toFloat()
        // скрываем не нужное
        film_music_dur_row.visibility = View.GONE
        film_music_dur.visibility = View.GONE
        film_actor_row.visibility = View.GONE
        film_actor.visibility = View.GONE
        film_maker_row.visibility = View.GONE
        film_maker.visibility = View.GONE
        film_series_row.visibility = View.GONE
        film_series.visibility = View.GONE
        music_album_row.visibility = View.GONE
        music_album.visibility = View.GONE
        film_book_row.visibility = View.GONE
        film_book.visibility = View.GONE
        film_music_row.visibility = View.GONE
        film_music.visibility = View.GONE
    }

    private fun drawFilm(id: Int) {
        val call = webClient.getFilm(id)
        call.enqueue(object : Callback<Film> {
            override fun onResponse(call: Call<Film>, response: Response<Film>) {
                val film = response.body()!!
                drawViewFilm(film)
            }

            override fun onFailure(call: Call<Film>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun drawViewFilm(film: Film) {
        name.text = film.getName()
        year.text = film.getYear().toString()
        film_music_dur.text = film.getDuration().toString()
        film_actor.text = film.getActorsString()
        film_maker.text = film.getMakerString()
        genre.text = film.getGenreString()
        film_series.run {
            val filmSeries = film.getFilmSeries()
            visibility = if (filmSeries != null) {
                film_series_row.visibility = View.VISIBLE
                text = filmSeries.getName()
                View.VISIBLE
            } else
                View.GONE
        }

        film_book.text = film.getBookName()
        film_music.text = film.getMusic().joinToString(separator = ", ")
        content_top.text = "Топ 100 всего-всего"
        content_pos.text = "1"
        book_film_desc.text = film.getDesc()
        rating.rating = film.getRating().toFloat()
        // скрываем не нужное
        book_music_author_row.visibility = View.GONE
        book_music_author.visibility = View.GONE
        book_series_row.visibility = View.GONE
        book_series.visibility = View.GONE
        music_album_row.visibility = View.GONE
        music_album.visibility = View.GONE
    }

    private fun drawMusic(id: Int) {
        val call = webClient.getMusic(id)
        call.enqueue(object : Callback<Music> {
            override fun onResponse(call: Call<Music>, response: Response<Music>) {
                val music = response.body()!!
                drawViewMusic(music)
            }

            override fun onFailure(call: Call<Music>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun drawViewMusic(music: Music) {
        name.text = music.getName()
        year.text = music.getYear().toString()
        film_music_dur.text = music.getDuration().toString()
        book_music_author.text = music.getArtistString()
        genre.text = music.getGenreString()
        music_album.run {
            val album = music.getAlbum()
            visibility = if (album != null) {
                music_album_row.visibility = View.VISIBLE
                text = album.getName()
                View.VISIBLE
            } else
                View.GONE
        }

        content_top.text = "Топ 100 всего-всего"
        content_pos.text = "1"
        rating.rating = music.getRating().toFloat()
        // скрываем не нужное
        film_actor_row.visibility = View.GONE
        film_actor.visibility = View.GONE
        film_maker_row.visibility = View.GONE
        film_maker.visibility = View.GONE
        film_series_row.visibility = View.GONE
        film_series.visibility = View.GONE
        book_series_row.visibility = View.GONE
        book_series.visibility = View.GONE
        film_book_row.visibility = View.GONE
        film_book.visibility = View.GONE
        film_music_row.visibility = View.GONE
        film_music.visibility = View.GONE
    }
}