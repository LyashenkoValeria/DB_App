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
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.content_item.view.*
import kotlinx.android.synthetic.main.content_item.view.genre
import kotlinx.android.synthetic.main.content_item.view.name
import kotlinx.android.synthetic.main.content_item.view.rating
import kotlinx.android.synthetic.main.content_item.view.year
import kotlinx.android.synthetic.main.top_content_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopContentAdapter(private val topId: Int, private val type: Type) : RecyclerView.Adapter<TopContentAdapter.TopContentViewHolder>() {

    private val webClient = WebClient().getApi()
    private var contentList: List<ContentIdName> = listOf()
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopContentViewHolder {
        getContent()
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_top, parent, false)
        return TopContentViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TopContentViewHolder, position: Int) {
        holder.updateViewElement(holder, position)
    }

    override fun getItemCount(): Int = contentList.size

    fun getContent() {
        val call = when (type) {
            Type.BOOK -> webClient.getBooksOfTop(topId)
            Type.FILM -> webClient.getFilmOfTop(topId)
            Type.MUSIC -> webClient.getMusicOfTop(topId)
        }

        call.enqueue(object : Callback<List<ContentIdName>> {
            override fun onResponse(call: Call<List<ContentIdName>>, response: Response<List<ContentIdName>>) {
                contentList = response.body()!!
                notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

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

        fun updateViewElement(holder: TopContentViewHolder, position: Int) {
            val contentId = contentList[position].getId()
            val call = when (type) {
                Type.BOOK -> webClient.getBookContent(contentId)
                Type.FILM -> webClient.getFilmContent(contentId)
                Type.MUSIC -> webClient.getMusicContent(contentId)
            }

            call.enqueue(object : Callback<Content> {
                override fun onResponse(call: Call<Content>, response: Response<Content>) {
                    holder.itemView.run {
                        val item = response.body()!!
                        // TODO: 13.05.2021 poster
                        top_content_place.text = "${position + 1} место"
                        top_content_name.text = item.getName()
                        top_content_year.text = item.getYear().toString()
                        top_content_genre.text = item.getGenreString()
                        top_content_rating.text = item.getRating().toString()
                    }
                }
                override fun onFailure(call: Call<Content>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
        }
    }


}