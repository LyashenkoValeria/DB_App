/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.Type
import com.example.db_app.dataClasses.TypeLayout
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
    var layoutType = TypeLayout.LIST

    val currentList = MutableLiveData<MutableList<Int>>(mutableListOf())
    val currentViewedList = MutableLiveData<MutableList<Int>>(mutableListOf())
    val currentRecommendList = MutableLiveData<MutableList<Int>>(mutableListOf())

    var emptyFlag = false       // Флаг пустого полученного из бд списка
    var newTypeFlag = false     // Флаг изменённого типа списка
    var filterPattern = ""
    var filterChanges = false
    var initFlag = true         // Флаг инициализации

    var savedGenres = MutableLiveData<ArrayList<Genre>>()
    var savedActors = MutableLiveData<ArrayList<ContentIdName>>()
    var savedMakers = MutableLiveData<ArrayList<ContentIdName>>()
    var yearBonds = MutableLiveData<Pair<Int, Int>>()
    var durBonds = MutableLiveData<Pair<Int, Int>>()
    var ratingBonds = MutableLiveData<Pair<Int, Int>>()


    init {
        val callInitBook = when (layoutType) {
            TypeLayout.LIST -> {webClient.getNextPartContentByType(Type.BOOK.t, null, 20, userToken)}
            TypeLayout.VIEWED -> {webClient.getViewedByType(Type.BOOK.t, null, 20, userToken)}
            TypeLayout.RECOMMEND -> {webClient.getRecommendByType(Type.BOOK.t, null, 20, userToken)}
        }
        callMoreContent(callInitBook)
        newTypeFlag = true
        initFlag = true
    }

    fun getMoreContent() {
        val callMore = if (filterPattern.isEmpty() && !filterChanges) {
            newTypeFlag = false
            initFlag = false

            when (layoutType) {
                TypeLayout.LIST -> {
                    webClient.getNextPartContentByType(
                        this.type.t,
                        currentList.value?.last(),
                        countNewIds,
                        userToken
                    )
                }
                TypeLayout.VIEWED -> {
                    webClient.getViewedByType(
                        this.type.t,
                        currentViewedList.value?.last(),
                        countNewIds,
                        userToken
                    )
                }
                TypeLayout.RECOMMEND -> {
                    webClient.getRecommendByType(
                        this.type.t,
                        currentRecommendList.value?.last(),
                        countNewIds,
                        userToken
                    )
                }
            }


        } else {
            filterCall(this.type, currentList.value?.last())
        }

        callMoreContent(callMore)
    }

    // TODO: 31.05.2021 clear не срабатывает
    fun updateContentForType(type: Type) {
        this.type = type

        if (filterPattern.isEmpty() && !filterChanges) {
            val callMore = when (layoutType) {
                TypeLayout.LIST -> {
//                    currentList.value?.clear()

                    currentList.value = mutableListOf()
                    webClient.getNextPartContentByType(this.type.t, null, countNewIds, userToken)
                }
                TypeLayout.VIEWED -> {
//                    currentViewedList.value?.clear()

                    currentViewedList.value = mutableListOf()
                    webClient.getViewedByType(this.type.t, null, countNewIds, userToken)
                }
                TypeLayout.RECOMMEND -> {
//                    currentRecommendList.value?.clear()

                    currentRecommendList.value = mutableListOf()
                    webClient.getRecommendByType(this.type.t, null, countNewIds, userToken)}
            }
//            val callMore =
//                webClient.getNextPartContentByType(this.type.t, null, countNewIds, userToken)
            callMoreContent(callMore)
            newTypeFlag = true
        } else {
//                    currentList.value?.clear()

            currentList.value = mutableListOf()
            val call = filterCall(type, null)
            callMoreContent(call)
        }
    }

    private fun callMoreContent(call: Call<List<Int>>) {
        call.enqueue(object : Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                val moreIds = response.body()
                if (moreIds != null) {
                    emptyFlag = moreIds.isEmpty()
                    if (!emptyFlag) {
                        val list = mutableListOf<Int>()
                        when(layoutType){
                            TypeLayout.VIEWED ->{
                                list.addAll(currentViewedList.value!!)
                                list.addAll(moreIds)
                                currentViewedList.value = list
                            }

                            TypeLayout.RECOMMEND ->{
                                list.addAll(currentRecommendList.value!!)
                                list.addAll(moreIds)
                                currentRecommendList.value = list
                            }

                            else -> {
                                list.addAll(currentList.value!!)
                                list.addAll(moreIds)
                                currentList.value = list
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                throw IllegalArgumentException("При callMoreContent() произошла ошибка.")
            }
        })
    }

    private fun filterCall(type: Type, firstId: Int?): Call<List<Int>> {
        val genresId = arrayListOf<Int>()
        if (savedGenres.value != null) {
            for (genre in savedGenres.value!!) {
                genresId.add(genre.id)
            }
        }

        val makersId = arrayListOf<Int>()
        if (savedMakers.value != null) {
            for (maker in savedMakers.value!!) {
                makersId.add(maker.id)
            }
        }

        val rating = if (ratingBonds.value != null) ratingBonds.value else Pair(0, 5)

        return when (type) {
            Type.FILM -> {
                val years = if (yearBonds.value != null) yearBonds.value else Pair(1895, 2021)
                val dur = if (durBonds.value != null) durBonds.value else Pair(40, 2021)

                val actorsId = arrayListOf<Int>()
                if (savedActors.value != null) {
                    for (actor in savedActors.value!!) {
                        actorsId.add(actor.id)
                    }
                }

                webClient.getFilterFilm(
                    years!!.first,
                    years.second,
                    dur!!.first,
                    dur.second,
                    rating!!.first,
                    rating.second,
                    genresId,
                    actorsId,
                    makersId,
                    filterPattern,
                    firstId,
                    countNewIds,
                    userToken
                )
            }

            Type.BOOK -> {
                val years = if (yearBonds.value != null) yearBonds.value else Pair(1500, 2021)

                webClient.getFilterBook(
                    years!!.first,
                    years.second,
                    rating!!.first,
                    rating.second,
                    genresId, makersId, filterPattern, firstId, countNewIds, userToken
                )
            }

            Type.MUSIC -> {
                val years = if (yearBonds.value != null) yearBonds.value else Pair(1500, 2021)
                val dur = if (durBonds.value != null) durBonds.value else Pair(2, 12)

                webClient.getFilterMusic(
                    years!!.first,
                    years.second,
                    dur!!.first,
                    dur.second,
                    rating!!.first,
                    rating.second,
                    genresId, makersId, filterPattern, firstId, countNewIds, userToken
                )
            }
        }
    }
}