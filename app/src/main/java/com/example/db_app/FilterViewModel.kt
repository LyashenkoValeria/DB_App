package com.example.db_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.db_app.dataClasses.Artist
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.People


class FilterViewModel: ViewModel() {
    private val genres = MutableLiveData<ArrayList<Genre>>()
    private val actors = MutableLiveData<ArrayList<People>>()
    private val makers = MutableLiveData<ArrayList<People>>()
    private val artists = MutableLiveData<ArrayList<Artist>>()
    private val yearBonds = MutableLiveData<Pair<Int, Int>>()
    private val durBonds = MutableLiveData<Pair<Int, Int>>()
    private val ratingBonds = MutableLiveData<Pair<Int, Int>>()

    fun setGenres(input: ArrayList<Genre>) {
        genres.value = input
    }

    fun getGenres(): LiveData<ArrayList<Genre>> {
        return genres
    }

    fun setActors(input: ArrayList<People>) {
        actors.value = input
    }

    fun getActors(): LiveData<ArrayList<People>> {
        return actors
    }

    fun setMakers(input: ArrayList<People>) {
        makers.value = input
    }

    fun getMakers(): LiveData<ArrayList<People>> {
        return makers
    }

    fun setArtists(input: ArrayList<Artist>) {
        artists.value = input
    }

    fun getArtists(): LiveData<ArrayList<Artist>> {
        return artists
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
}