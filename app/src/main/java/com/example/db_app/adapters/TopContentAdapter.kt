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
import com.example.db_app.dataClasses.Content
import com.example.db_app.dataClasses.Top
import com.example.db_app.dataClasses.TopEl
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.content_item.view.*
import kotlinx.android.synthetic.main.top_content_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopContentAdapter(private val contentList: List<TopEl>, private val type: Type, private val userToken: String) : RecyclerView.Adapter<TopContentAdapter.TopContentViewHolder>() {
    // TODO: 22.05.2021 Не работатет. Получать контент по id и отрисовывать
    private lateinit var listener: OnItemClickListener
    private val webClient = WebClient().getApi()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top_content_item, parent, false)
        return TopContentViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TopContentViewHolder, position: Int) {
        holder.updateViewElement(holder, position)
    }

    override fun getItemCount(): Int = contentList.size

    fun getContentByPosition(position: Int) = contentList[position]

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class TopContentViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION)
                        listener.onItemClick(pos)
                }
            }
        }

        fun updateViewElement(holder: TopContentAdapter.TopContentViewHolder, position: Int) {
            val contentId = contentList[position]
            val call = webClient.getContentById(type.t, contentId.id, userToken)

            call.enqueue(object : Callback<Content> {
                override fun onResponse(call: Call<Content>, response: Response<Content>) {
                    holder.itemView.run {
                        val item = response.body()
                        if (item != null) { // TODO: 20.05.2021 Удалить или обрабатывать по-другому
                            top_content_poster.setImageResource(
                                when (type) {
                                    Type.BOOK -> R.drawable.book_poster
                                    Type.FILM -> R.drawable.film_poster
                                    Type.MUSIC -> R.drawable.music_poster
                                }
                            )
                            top_content_place.text =  resources.getString(R.string.place_in_top, contentId.position)
                            top_content_name.text = item.name
                            top_content_year.text = item.year.toString()
                            top_content_genre.text = item.getGenreString()
                            top_content_rating.text = item.rating.toString()
                            top_content_rating_bar.rating = item.rating.toFloat()
                        }
                    }
                }

                override fun onFailure(call: Call<Content>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
        }
    }
}