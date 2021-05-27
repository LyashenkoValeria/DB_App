/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.adapters.SpinnerAdapter
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_super_user_add.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuperUserAddFragment : Fragment() {

    private var type = Type.BOOK
    val webClient = WebClient().getApi()
    var userToken = ""
    var peopleFunc = listOf<ContentIdName>()
    private var funcSelected = ContentIdName(0, "")     // временное
    private var genres = listOf<Genre>()

    private val peoplesFilmSelected = mutableMapOf<String, Int>()     // нужно
    private val peoplesSelected = mutableListOf<String>()             // нужно
    private val genresSelected = mutableListOf<ContentIdName>()       // нужно

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_super_user_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeStr = arguments?.getString("type") ?: "book"
        val title: String
        type = when (typeStr) {
            Type.BOOK.t -> {
                title = "Создание книги"
                Type.BOOK
            }
            Type.FILM.t -> {
                title = "Создание фильма"
                Type.FILM
            }
            Type.MUSIC.t -> {
                title = "Создание музыки"
                Type.MUSIC
            }
            else -> {
                title = "Создание книги"
                Type.BOOK
            }
        }

        (requireActivity() as MainActivity).setToolbarTitle(title)

        userToken = (requireActivity() as MainActivity).getUserToken()

        // Заполняем спиннер с жанрами
        val callGenres = webClient.getGenreByType(type.t, userToken)
        callGenres.enqueue(object : Callback<List<Genre>> {
            override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                genres = response.body()!!
                val genresAdapter = SpinnerAdapter(requireContext(), response.body()!!)

                val onClickListener = View.OnClickListener {
                    genresSelected.clear()
                    val genr = genresAdapter.getSelectedItems().toMutableList()
                    genr.forEach { genresSelected.add(it.toContentIdName()) }
                    super_genres_list.text =
                        genresSelected.joinToString(separator = ", ") { it.name }
                }

                genresAdapter.customListener = true
                genresAdapter.listener = onClickListener
                super_genres_spinner.adapter = genresAdapter
            }

            override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })

        // Если фильм, добавляем возможность выбирать функцию человека
        when (type) {
            Type.FILM -> {
                super_peoples_text.text = "Люди"
                super_function.visibility = View.VISIBLE
                super_series.hint = "Серия фильмов"

                val callFunc = webClient.getFunctions(userToken)
                callFunc.enqueue(object : Callback<List<ContentIdName>> {
                    override fun onResponse(
                        call: Call<List<ContentIdName>>,
                        response: Response<List<ContentIdName>>
                    ) {
                        peopleFunc = response.body()!!
                        val peopleForAdapter = mutableListOf<String>()
                        peopleFunc.forEach { peopleForAdapter.add(it.name) }

                        super_function.adapter =
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                peopleForAdapter
                            )

                        super_function.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    funcSelected = peopleFunc[position]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }
                            }
                    }

                    override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })

                super_button_plus.setOnClickListener {
                    val peopleFullName = super_people.text.toString()

                    if (peopleFullName.isNotEmpty()) {
                        peoplesFilmSelected[peopleFullName] = funcSelected.id
                        super_peoples_list.text =
                            "${super_peoples_list.text}\n$peopleFullName - ${funcSelected.name}"
                        super_people.setText("")
                    } else
                        (requireActivity() as MainActivity).makeToast("Введите имя")
                }
            }
            Type.BOOK -> {
                super_peoples_text.text = "Авторы"
                super_function.visibility = View.GONE
                super_duration_layout.visibility = View.GONE
                super_series.hint = "Серия книг"
                super_films_book_layout.visibility = View.GONE
                super_films_music_layout.visibility = View.GONE

                super_button_plus.setOnClickListener {
                    val peopleFullName = super_people.text.toString()

                    if (peopleFullName.isNotEmpty()) {
                        peoplesSelected.add(peopleFullName)
                        super_peoples_list.text = "${super_peoples_list.text}\n$peopleFullName"
                        super_people.setText("")
                    } else
                        (requireActivity() as MainActivity).makeToast("Введите имя")
                }
            }
            Type.MUSIC -> {
                super_peoples_text.text = "Исполнители"
                super_function.visibility = View.GONE
                super_series.hint = "Музыкальный альбом"
                super_films_book_layout.visibility = View.GONE
                super_films_music_layout.visibility = View.GONE

                super_button_plus.setOnClickListener {
                    val peopleFullName = super_people.text.toString()

                    if (peopleFullName.isNotEmpty()) {
                        peoplesSelected.add(peopleFullName)
                        super_peoples_list.text = "${super_peoples_list.text}\n$peopleFullName"
                        super_people.setText("")
                    } else
                        (requireActivity() as MainActivity).makeToast("Введите имя")
                }
            }
        }

        super_button_save.setOnClickListener {
            val name = super_name.text.toString()
            val year = super_year.text.toString()
            val duration = super_duration.text.toString()
            val desc = super_desc.text.toString()
            val poster =
                if (super_poster.text.isNullOrEmpty()) null else super_poster.text.toString()
            val series =
                if (super_series.text.isNullOrEmpty()) null else super_series.text.toString()
            val filmsBook =
                if (super_films_book.text.isNullOrEmpty()) null else super_films_book.text.toString()
            val filmsMusic =
                if (super_films_music.text.isNullOrEmpty()) null else super_films_music.text.toString()

            if (name.isEmpty() || year.isEmpty() || (duration.isEmpty() && type != Type.BOOK) ||
                (desc.isEmpty() && type != Type.MUSIC) ||
                (peoplesSelected.isEmpty() && peoplesFilmSelected.isEmpty()) || genresSelected.isEmpty()
            ) {
                (requireActivity() as MainActivity).makeToast("Заполните обязательные поля.")
                return@setOnClickListener
            }

            val genresToCall = mutableListOf<Int>()
            genresSelected.forEach { genresToCall.add(it.id) }

            val callSave = when (type) {
                Type.BOOK -> webClient.saveBook(
                    name,
                    year.toInt(),
                    desc,
                    poster,
                    series,
                    peoplesSelected,
                    genresToCall,
                    userToken
                )
                Type.FILM -> webClient.saveFilm(
                    name,
                    year.toInt(),
                    duration.toInt(),
                    desc,
                    poster,
                    series,
                    filmsBook,
                    filmsMusic,
                    JSONObject(peoplesFilmSelected.toMap()).toString(),
                    genresToCall,
                    userToken
                )
                Type.MUSIC -> webClient.saveMusic(
                    name,
                    year.toInt(),
                    duration.toInt(),
                    poster,
                    peoplesSelected,
                    series,
                    genresToCall,
                    userToken
                )
            }

            callSave.enqueue(object : Callback<Map<String, Int>> {
                override fun onResponse(
                    call: Call<Map<String, Int>>,
                    response: Response<Map<String, Int>>
                ) {
                    val code = response.body()?.get("code")
                    val msg = when (code) {
                        0 -> "Данные успешно сохранены."
                        10 -> "Указанная книга не существует."
                        11 -> "Указанная песня не существует."
                        else -> "Кажется, что-то пошло не так."
                    }

                    (requireActivity() as MainActivity).makeToast(msg)
                    if (code == 0)
                        (requireActivity() as MainActivity).back()
                }

                override fun onFailure(call: Call<Map<String, Int>>, t: Throwable) {
                    Log.d("db", "Response = $t")
                    (requireActivity() as MainActivity).makeToast("Кажется, что-то пошло совсем не так.")
                }
            })

        }

    }
}