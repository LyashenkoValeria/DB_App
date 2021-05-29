/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.db_app.dataClasses.Type
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModelContentList(application: Application) : AndroidViewModel(application) {

    private val webClient = WebClient().getApi()
    private val userToken: String =
        application.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
            .getString("userToken", "")!!
    private val countNewIds = 20
    private var type = Type.BOOK

    val currentList = MutableLiveData<MutableList<Int>>(mutableListOf())
    var emptyFlag = false       // Флаг пустого полученного из бд списка
    var newTypeFlag = false     // Флаг изменённого типа списка


    init {
        val callInitBook = webClient.getNextPartContentByType(Type.BOOK.t, null, 20, userToken)
        callMoreContent(callInitBook, Type.BOOK)
    }

    fun getMoreContent() {
        newTypeFlag = false

        val callMore = webClient.getNextPartContentByType(
            this.type.t,
            currentList.value?.last(),
            countNewIds,
            userToken
        )

        callMoreContent(callMore, this.type)
    }

    fun updateContentForType(type: Type) {
        this.type = type
        currentList.value?.clear()
        val callMore = webClient.getNextPartContentByType(this.type.t, null, countNewIds, userToken)
        callMoreContent(callMore, this.type)
        newTypeFlag = true
    }

    private fun callMoreContent(call: Call<List<Int>>, type: Type) {
        call.enqueue(object : Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                val moreIds = response.body()
                if (moreIds != null) {
                    emptyFlag = moreIds.isEmpty()
                    if (!emptyFlag) {
                        val list = mutableListOf<Int>()
                        list.addAll(currentList.value!!)
                        list.addAll(moreIds)
                        currentList.value = list
                    }
                }
            }

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                throw IllegalArgumentException("При callMoreContent() произошла ошибка.")
            }
        })
    }
}