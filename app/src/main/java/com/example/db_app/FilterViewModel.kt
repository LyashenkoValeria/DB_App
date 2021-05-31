package com.example.db_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.db_app.dataClasses.*


class FilterViewModel: ViewModel() {
    private val allBookGenre = MutableLiveData<List<Genre>>()
    private val allFilmGenre = MutableLiveData<List<Genre>>()
    private val allMusicGenre = MutableLiveData<List<Genre>>()
    private val allBookPeople = MutableLiveData<List<ContentIdName>>()
    private val allFilmActors = MutableLiveData<List<ContentIdName>>()
    private val allFilmMakers = MutableLiveData<List<ContentIdName>>()
    private val allMusicPeople = MutableLiveData<List<ContentIdName>>()

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