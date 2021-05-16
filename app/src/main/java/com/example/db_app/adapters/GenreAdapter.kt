/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.genre_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenreAdapter : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    private var type = Type.BOOK
    private var genreList: List<Genre> = listOf()
    private var likeGenreList = mutableListOf<Genre>()
    private val webClient = WebClient().getApi()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.genre_item, parent, false)
        return GenreViewHolder(view)

    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.itemView.run {
            val item = genreList[position]
            check_box.text = item.getName()
            check_box.isChecked = likeGenreList.contains(item)
            check_box.setOnClickListener {
                check_box.isChecked = !check_box.isChecked
                if (check_box.isChecked)
                    likeGenreList.add(item)
                else
                    likeGenreList.remove(item)

            }
        }
    }

    override fun getItemCount(): Int = genreList.size

    fun setGenreList(type: Type) {
        this.type = type

        val call = when (type) {
            Type.BOOK -> webClient.getBookGenre()
            Type.FILM -> webClient.getFilmGenre()
            Type.MUSIC -> webClient.getMusicGenre()
        }
        call.enqueue(object : Callback<List<Genre>> {
            override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                genreList = response.body()!!
                notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })

        val call2 = when (type) {
            Type.BOOK -> webClient.getBookLikeGenre()
            Type.FILM -> webClient.getFilmLikeGenre()
            Type.MUSIC -> webClient.getMusicLikeGenre()
        }
        call2.enqueue(object : Callback<List<Genre>> {
            override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                likeGenreList = response.body()!!.toMutableList()
                notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })

//        likeGenreList = WebClient().getLikeGenre(type)!!.toMutableList()
//        notifyDataSetChanged()
    }

    fun getLikeGenreList() = likeGenreList

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}