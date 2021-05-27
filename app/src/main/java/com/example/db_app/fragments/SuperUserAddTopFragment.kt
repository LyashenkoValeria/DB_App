/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_super_user_add.*
import kotlinx.android.synthetic.main.fragment_super_user_add_top.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuperUserAddTopFragment : Fragment() {

    var userToken = ""
    private var type = Type.BOOK
    val webClient = WebClient().getApi()
    private val topList = mutableMapOf<String, String>()
    private var count = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_super_user_add_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeStr = arguments?.getString("type") ?: "book"
        val title: String
        type = when (typeStr) {
            Type.BOOK.t -> {
                title = "Создание книжного топа"
                Type.BOOK
            }
            Type.FILM.t -> {
                title = "Создание топа фильмов"
                Type.FILM}
            Type.MUSIC.t -> {
                title = "Создание музыкального топа"
                Type.MUSIC
            }
            else -> {
                title = "Создание книжного топа"
                Type.BOOK
            }
        }
        (requireActivity() as MainActivity).setToolbarTitle(title)

        userToken = (requireActivity() as MainActivity).getUserToken()

        super_top_button_plus.setOnClickListener {
            val contentName = super_top_content.text.toString()

            if (contentName.isNotEmpty()) {
                topList[count.toString()] = contentName
                super_top_content_list.text = "${super_top_content_list.text}\n$count. $contentName"
                super_top_content.setText("")
                count++
            } else {
                (requireActivity() as MainActivity).makeToast("Введите название.")
            }
        }

        super_top_button_save.setOnClickListener {
            val name = super_top_name.text.toString()
            val author = super_top_author.text.toString()

            if (name.isEmpty() || author.isEmpty() || topList.isEmpty()) {
                (requireActivity() as MainActivity).makeToast("Заполните все данные.")
                return@setOnClickListener
            }

            val topName = "$name (автор - $author)"

            val callSave = webClient.saveTop(type.t, topName, JSONObject(topList.toMap()).toString(), userToken)

            callSave.enqueue(object : Callback<Map<String, Int>> {
                override fun onResponse(
                    call: Call<Map<String, Int>>,
                    response: Response<Map<String, Int>>
                ) {
                    val code = response.body()?.get("code")
                    val msg = when (code) {
                        0 -> "Данные успешно сохранены."
                        21 -> "Одна из указанных книг не существует."
                        22 -> "Один из указанных фильмов не существует."
                        23 -> "Одна из указанных песен не существует."
                        else -> "Кажется, что-то пошло не так."
                    }
                    (requireActivity() as MainActivity).makeToast(msg)
                    if (code == 0)
                        (requireActivity() as MainActivity).back()
                }

                override fun onFailure(call: Call<Map<String, Int>>, t: Throwable) {
                    Log.d("db", "Response = $t")
                    (requireActivity() as MainActivity).makeToast("Кажется, что-то пошло не так.")
                }
            })
        }

    }
}