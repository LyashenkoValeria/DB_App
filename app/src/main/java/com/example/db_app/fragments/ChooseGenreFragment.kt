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
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_choose_genre.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseGenreFragment : Fragment() {
    private var type = Type.BOOK
    private var bookGenreList = listOf<Int>()
    private var filmGenreList = listOf<Int>()
    private var musicGenreList = listOf<Int>()
    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_genre, container, false)
    }
    // TODO: 22.05.2021
    //  2) Изменить checkBox на checkableTextView;
    //  6) Сворачивать описание в CardView (????).

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        choose_text.text = resources.getString(R.string.choose_genre_book)
        val userToken = (requireActivity() as MainActivity).getUserToken()

        val genreAdapter = GenreAdapter(userToken)
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
                    (genre_recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    (requireActivity() as MainActivity).setToolbarTitle("Жанры фильмов")
                    choose_text.text = resources.getString(R.string.choose_genre_film)
                }
                Type.FILM -> {
                    type = Type.MUSIC

                    (genre_recycler.adapter as GenreAdapter).run {
                        filmGenreList = getLikeGenreList()
                        setGenreList(type)
                    }
                    (genre_recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    (requireActivity() as MainActivity).setToolbarTitle("Жанры музыки")
                    choose_text.text = resources.getString(R.string.choose_genre_music)
                }
                Type.MUSIC -> {
                    musicGenreList = (genre_recycler.adapter as GenreAdapter).getLikeGenreList()

                    // ОБновление данных в бд
                    val callChangeGenres =  webClient.changeLikeGenre(bookGenreList,filmGenreList, musicGenreList, userToken)

                    callChangeGenres.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {}

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })


//                    val callChangeFilmGenre = webClient.changeLikeGenre(Type.FILM.t, filmGenreList, userToken)
//
//                    callChangeFilmGenre.enqueue(object : Callback<ResponseBody> {
//                        override fun onResponse(
//                            call: Call<ResponseBody>,
//                            response: Response<ResponseBody>
//                        ) {}
//
//                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                            Log.d("db", "Response = $t")
//                        }
//                    })
//
//
//                    val callChangeMusicGenre = webClient.changeLikeGenre(Type.MUSIC.t, musicGenreList, userToken)
//
//                    callChangeMusicGenre.enqueue(object : Callback<ResponseBody> {
//                        override fun onResponse(
//                            call: Call<ResponseBody>,
//                            response: Response<ResponseBody>
//                        ) {}
//
//                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                            Log.d("db", "Response = $t")
//                        }
//                    })
                    toBackFragment(true)
                }
            }
        }

        button_back.setOnClickListener {
            when (type) {
                Type.BOOK -> {
                    toBackFragment(false)
                }
                Type.FILM -> {
                    type = Type.BOOK
                    (genre_recycler.adapter as GenreAdapter).setGenreList(type)
                    (genre_recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    (requireActivity() as MainActivity).setToolbarTitle("Жанры книг")
                    choose_text.text = resources.getString(R.string.choose_genre_book)
                }
                Type.MUSIC -> {
                    type = Type.FILM
                    (genre_recycler.adapter as GenreAdapter).setGenreList(type)
                    (genre_recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    (requireActivity() as MainActivity).setToolbarTitle("Жанры фильмов")
                    choose_text.text = resources.getString(R.string.choose_genre_film)
                }
            }
        }
    }

    private fun toBackFragment(save: Boolean) {
        if (save)
            (requireActivity() as MainActivity).makeToast("Жанры успешно сохранены.")
        val prevFrag = (requireActivity() as MainActivity).prevFragment
        if (prevFrag == R.id.profileFragment)
            (requireActivity() as MainActivity).back()
        else
            (requireActivity() as MainActivity).toContentList()
    }

}