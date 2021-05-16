package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.adapters.GenreAdapter
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_choose_genre.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseGenreFragment : Fragment() {
    private var type = Type.BOOK
    private var bookGenreList = listOf<Genre>()
    private var filmGenreList = listOf<Genre>()
    private var musicGenreList = listOf<Genre>()
    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_genre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        choose_text.text = "Выберите любимые жанры книг"

        val genreAdapter = GenreAdapter()
        genre_recycler.layoutManager = LinearLayoutManager(requireContext())
        genre_recycler.adapter = genreAdapter
        (genre_recycler.adapter as GenreAdapter).setGenreList(type)

        button_next.setOnClickListener {
            when (type) {
                Type.BOOK -> {
                    type = Type.FILM
                    (genre_recycler.adapter as GenreAdapter).run {
                        bookGenreList = getLikeGenreList()
                        setGenreList(type)
                    }
                    choose_text.text = "Выберите любимые жанры фильмов"
                }
                Type.FILM -> {
                    type = Type.MUSIC

                    (genre_recycler.adapter as GenreAdapter).run {
                        filmGenreList = getLikeGenreList()
                        setGenreList(type)
                    }
                    choose_text.text = "Выберите любимые жанры музыки"
                }
                Type.MUSIC -> {
                    musicGenreList = (genre_recycler.adapter as GenreAdapter).getLikeGenreList()

                    // ОБновление данных в бд
//                    WebClient().changeLikeGenre(Type.BOOK, bookGenreList)
                    val userId = 1 // TODO: 16.05.2021  Добавить сохранение и получение userId
                    val call1 = webClient.changeLikeBookGenre(userId, bookGenreList)

                    call1.enqueue(object : Callback<Boolean> {
                        override fun onResponse(
                            call: Call<Boolean>,
                            response: Response<Boolean>
                        ) {}

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })

//                    WebClient().changeLikeGenre(Type.FILM, filmGenreList)
                    val call2 = webClient.changeLikeFilmGenre(userId, filmGenreList)

                    call2.enqueue(object : Callback<Boolean> {
                        override fun onResponse(
                            call: Call<Boolean>,
                            response: Response<Boolean>
                        ) {}

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })

//                    WebClient().changeLikeGenre(Type.MUSIC, musicGenreList)
                    val call3 = webClient.changeLikeMusicGenre(userId, musicGenreList)

                    call3.enqueue(object : Callback<Boolean> {
                        override fun onResponse(
                            call: Call<Boolean>,
                            response: Response<Boolean>
                        ) {}

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })


                    (requireActivity() as MainActivity).toContentList()
                }
            }
        }

        button_back.setOnClickListener {
            when (type) {
                Type.BOOK -> {
                    (requireActivity() as MainActivity).toContentList()
                }
                Type.FILM -> {
                    type = Type.BOOK
                    (genre_recycler.adapter as GenreAdapter).setGenreList(type)
                    choose_text.text = "Выберите любимые жанры книг"
                }
                Type.MUSIC -> {
                    type = Type.FILM
                    (genre_recycler.adapter as GenreAdapter).setGenreList(type)
                    choose_text.text = "Выберите любимые жанры фильмов"
                }
            }
        }
    }
}