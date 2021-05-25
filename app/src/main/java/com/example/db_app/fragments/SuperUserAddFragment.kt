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
import okhttp3.ResponseBody
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

    private val peoplesFilmSelected = mutableMapOf<String, ContentIdName>()     // нужно
    private val peoplesSelected = mutableListOf<String>()                       // нужно
    private val genresSelected = mutableListOf<ContentIdName>()                 // нужно

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_super_user_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeStr = arguments?.getString("type") ?: "book"
        type = when (typeStr) {
            Type.BOOK.t -> Type.BOOK
            Type.FILM.t -> Type.FILM
            Type.MUSIC.t -> Type.MUSIC
            else -> Type.BOOK
        }
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
                        val peopleRole = funcSelected
                        peoplesFilmSelected[peopleFullName] = peopleRole
                        super_peoples_list.text =
                            "${super_peoples_list.text}\n$peopleFullName - ${peopleRole.name}"
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
                super_poster_layout.visibility = View.GONE

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
//            val year = super_year.text.toString().toInt()
            val year = 2000
//            val duration = super_duration.text.toString().toInt()
            val duration = 12
            val desc = super_desc.text.toString()
            val poster = super_poster.text.toString()
            val series = super_series.text.toString()
            val filmsBook = super_films_book.text.toString()
            val filmsMusic = super_films_music.text.toString()

            if (name.isEmpty() || year == 0 || (duration == 0 && type != Type.BOOK) ||
                (desc.isEmpty() && type != Type.MUSIC) || (peoplesSelected.isEmpty() && peoplesFilmSelected.isEmpty()) || genresSelected.isEmpty()) {
                (requireActivity() as MainActivity).makeToast("Заполните обязательные поля.")
                return@setOnClickListener
            }

            val genresToCall = mutableListOf<Int>()
            genresSelected.forEach { genresToCall.add(it.id) }

            val callSaveBook = webClient.saveBook(name, year, desc, poster, series, peoplesSelected, genresToCall, userToken)

            callSaveBook.enqueue(object : Callback<Int> {
                override fun onResponse(
                    call: Call<Int>,
                    response: Response<Int>
                ) {
                    if (response.body() != null) {
                    (requireActivity() as MainActivity).makeToast("Данные успешно сохранены.")
                    (requireActivity() as MainActivity).back()
                    } else {
                        (requireActivity() as MainActivity).makeToast("Кажется, что-то пошло не так.")
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.d("db", "Response = $t")
                    (requireActivity() as MainActivity).makeToast("Кажется, что-то пошло совсем не так.")
                }
            })




//            val callSave = when (type) {
//                Type.BOOK -> webClient.saveBook()
//                Type.FILM -> webClient.saveFilm()
//                Type.MUSIC -> webClient.saveMusic()
//            }
//
//            callSave.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//                    (requireActivity() as MainActivity).makeToast("Данные успешно сохранены.")
//                    (requireActivity() as MainActivity).back()
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.d("db", "Response = $t")
//                    (requireActivity() as MainActivity).makeToast("Кажется, что-то пошло не так.")
//                }
//            })
        }

    }
}