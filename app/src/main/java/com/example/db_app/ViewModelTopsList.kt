/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Type
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModelTopsList(application: Application) : AndroidViewModel(application) {

    private val webClient = WebClient().getApi()
    private val userToken: String =
        application.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
            .getString("userToken", "")!!
    private val countNewIds = 20
    private var type = Type.BOOK

    val currentList = MutableLiveData<MutableList<ContentIdName>>(mutableListOf())
    var emptyFlag = false       // Флаг пустого полученного из бд списка
    var newTypeFlag = false     // Флаг изменённого типа списка
    var initFlag = true         // Флаг инициализации
    var filterPattern = ""
    var count = 0


//    init {
//        val callInitTopsBook = webClient.getNextPartTopsByType(Type.BOOK.t, null, 20, userToken)
//        callMoreTops(callInitTopsBook)
//        newTypeFlag = true
//    }

    fun init() {
        currentList.value?.clear()
        val callInitTopsBook = webClient.getNextPartTopsByType(Type.BOOK.t, null, 20, userToken)
        initFlag = count == 0
        callMoreTops(callInitTopsBook)
        newTypeFlag = true
        count++
    }


    fun getMoreTops() {
        val callMore = if (filterPattern.isEmpty()) {
            newTypeFlag = false
            initFlag = false

            webClient.getNextPartTopsByType(
                this.type.t,
                currentList.value?.last()?.id,
                countNewIds,
                userToken
            )
        } else {
            filterCallTops(this.type, currentList.value?.last()?.id)
        }
        callMoreTops(callMore)
    }

    fun updateTopsForType(type: Type) {
        this.type = type
        initFlag = false

        currentList.value?.clear()
        if (filterPattern.isEmpty()) {
            val callMore =
                webClient.getNextPartTopsByType(this.type.t, null, countNewIds, userToken)
            callMoreTops(callMore)
            newTypeFlag = true
        } else {
            val call = filterCallTops(type, null)
            callMoreTops(call)
        }
    }

    private fun callMoreTops(call: Call<List<ContentIdName>>) {
        call.enqueue(object : Callback<List<ContentIdName>> {
            override fun onResponse(
                call: Call<List<ContentIdName>>,
                response: Response<List<ContentIdName>>
            ) {
                val moreIds = response.body()
                if (moreIds != null) {
                    emptyFlag = moreIds.isEmpty()
                    if (!emptyFlag) {
                        val list = mutableListOf<ContentIdName>()
                        list.addAll(currentList.value!!)
                        list.addAll(moreIds)
                        currentList.value = list
                    }
                }
            }

            override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                throw IllegalArgumentException("При callMoreTops() произошла ошибка.")
            }
        })
    }

    private fun filterCallTops(type: Type, firstId: Int?): Call<List<ContentIdName>> {
        return webClient.getFilterForTops(type.t, filterPattern,firstId, countNewIds, userToken)
    }
}