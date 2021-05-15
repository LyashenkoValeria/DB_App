package com.example.db_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.adapters.ContentAdapter.Type
import kotlinx.android.synthetic.main.fragment_content.*


class ContentFragment : Fragment() {
    private var type = Type.BOOK

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
        val book = WebClient().getBook(id)
        name.text = book.getName()
        year.text = book.getYear().toString()
        book_music_author.text = book.getPeopleString()
        genre.text = book.getGenreSting()
        book_series.text = book.getBookSeries().getName()
        content_top.text = "Топ 100 всего-всего"
        content_pos.text = "1"
        book_film_desc.text = book.getDesc()
        rating.rating = WebClient().getRating(type, id).toFloat()
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
        val film = WebClient().getFilm(id)
        name.text = film.getName()
        year.text = film.getYear().toString()
        film_music_dur.text = film.getDuration().toString()
        film_actor.text = film.getActorsString()
        film_maker.text = film.getMakerString()
        genre.text = film.getGenreSting()
        film_series.text = film.getFilmSeries().getName()
        film_book.text = film.getBookName()
        film_music.text = film.getMusic().getName()
        content_top.text = "Топ 100 всего-всего"
        content_pos.text = "1"
        book_film_desc.text = film.getDesc()
        rating.rating = WebClient().getRating(type, id).toFloat()
        // скрываем не нужное
        book_music_author_row.visibility = View.GONE
        book_music_author.visibility = View.GONE
        book_series_row.visibility = View.GONE
        book_series.visibility = View.GONE
        music_album_row.visibility = View.GONE
        music_album.visibility = View.GONE
    }

    private fun drawMusic(id: Int) {
        val music = WebClient().getMusic(id)
        name.text = music.getName()
        year.text = music.getYear().toString()
        film_music_dur.text = music.getDuration().toString()
        book_music_author.text = music.getArtistString()
        genre.text = music.getGenreSting()
        music_album.text = music.getAlbumName()
        content_top.text = "Топ 100 всего-всего"
        content_pos.text = "1"
        rating.rating = WebClient().getRating(type, id).toFloat()
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