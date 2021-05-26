package com.example.db_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.db_app.dataClasses.*


class FilterViewModel: ViewModel() {
    private val genres = MutableLiveData<ArrayList<Genre>>()
    private val actors = MutableLiveData<ArrayList<ContentIdName>>()
    private val makers = MutableLiveData<ArrayList<ContentIdName>>()
    private val yearBonds = MutableLiveData<Pair<Int, Int>>()
    private val durBonds = MutableLiveData<Pair<Int, Int>>()
    private val ratingBonds = MutableLiveData<Pair<Int, Int>>()

    private val allBookGenre = MutableLiveData<List<Genre>>()
    private val allFilmGenre = MutableLiveData<List<Genre>>()
    private val allMusicGenre = MutableLiveData<List<Genre>>()
    private val allBookPeople = MutableLiveData<List<ContentIdName>>()
    private val allFilmActors = MutableLiveData<List<ContentIdName>>()
    private val allFilmMakers = MutableLiveData<List<ContentIdName>>()
    private val allMusicPeople = MutableLiveData<List<ContentIdName>>()

    fun setGenres(input: ArrayList<Genre>) {
        genres.value = input
    }

    fun getGenres(): LiveData<ArrayList<Genre>> {
        return genres
    }

    fun setActors(input: ArrayList<ContentIdName>) {
        actors.value = input
    }

    fun getActors(): LiveData<ArrayList<ContentIdName>> {
        return actors
    }

    fun setMakers(input: ArrayList<ContentIdName>) {
        makers.value = input
    }

    fun getMakers(): LiveData<ArrayList<ContentIdName>> {
        return makers
    }

    fun setYears(input: Pair<Int, Int>) {
        yearBonds.value = input
    }

    fun getYears(): LiveData<Pair<Int, Int>> {
        return yearBonds
    }

    fun setDuration(input: Pair<Int, Int>) {
        durBonds.value = input
    }

    fun getDuration(): LiveData<Pair<Int, Int>> {
        return durBonds
    }

    fun setRating(input: Pair<Int, Int>) {
        ratingBonds.value = input
    }

    fun getRating(): LiveData<Pair<Int, Int>> {
        return ratingBonds
    }

    fun setGenresForFilter(type: Type, input: List<Genre>){
        when (type) {
            Type.BOOK -> {
                allBookGenre.value = input
            }
            Type.FILM -> {
                allFilmGenre.value = input
            }
            Type.MUSIC -> {
                allMusicGenre.value = input
            }
        }
    }

    fun getGenresForFilter(type: Type): LiveData<List<Genre>>{
        return when (type){
            Type.BOOK -> {
                allBookGenre
            }
            Type.FILM -> {
                allFilmGenre
            }
            Type.MUSIC -> {
                allMusicGenre
            }
        }
    }

    fun setPeopleForFilter(type: Type, input: List<ContentIdName>){
        when (type) {
            Type.BOOK -> {
                allBookPeople.value = input
            }
            Type.FILM -> {
                allFilmMakers.value = input
            }
            Type.MUSIC -> {
                allMusicPeople.value = input
            }
        }
    }

    fun getPeopleForFilter(type: Type): LiveData<List<ContentIdName>>{
        return when (type){
            Type.BOOK -> {
                allBookPeople
            }
            Type.FILM -> {
                allFilmMakers
            }
            Type.MUSIC -> {
                allMusicPeople
            }
        }
    }

    fun setActorsForFilter(input: List<ContentIdName>){
        allFilmActors.value = input
    }

    fun getActorsForFilter(): LiveData<List<ContentIdName>>{
        return allFilmMakers
    }


}