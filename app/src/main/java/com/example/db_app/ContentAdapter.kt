/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.db_app.dataClasses.Content
import kotlinx.android.synthetic.main.content_item.view.*

class ContentAdapter :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    var type = Type.BOOK
    var contentList: List<Content> = WebClient().getBookList()
    private lateinit var listener: OnItemClickListener

    enum class Type(val t: Int) {
        BOOK(0), FILM(1), MUSIC(2) }



    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false)
        return ContentViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.itemView.run {
            val item = contentList[position]
            // TODO: 13.05.2021 poster
            name.text = item.getName()
            year.text = item.getYear().toString()
            genre.text = item.getGenreSting()
            rating.text = item.getRating().toString()

        }
    }

    override fun getItemCount(): Int = contentList.size

    fun setContent(type: Type) {
        this.type = type
        contentList = when(type) {
            Type.BOOK -> WebClient().getBookList()
            Type.FILM -> WebClient().getFilmList()
            Type.MUSIC -> WebClient().getMusicList()
        }
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ContentViewHolder(itemView: View, listener: OnItemClickListener) :
        ViewHolder(itemView) {
        init {
            itemView.apply {
                setOnClickListener{

                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION)
                        listener.onItemClick(pos)
                }
            }
        }
    }

}