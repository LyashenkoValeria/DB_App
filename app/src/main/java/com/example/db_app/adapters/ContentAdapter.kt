/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.Content
import kotlinx.android.synthetic.main.content_item.view.*

class ContentAdapter :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>(), Filterable{

    var type = Type.BOOK
    var contentList: List<Content> = WebClient().getBookList()
    var contentListFull: List<Content> = WebClient().getBookList()
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

        when(type) {
            Type.BOOK -> {
                contentList = WebClient().getBookList()
                contentListFull = WebClient().getBookList()
            }
            Type.FILM -> {
                contentList = WebClient().getFilmList()
                contentListFull = WebClient().getFilmList()
            }
            Type.MUSIC -> {
                contentList = WebClient().getMusicList()
                contentListFull = WebClient().getMusicList()
            }
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

    override fun getFilter(): Filter {
        return contentFilter
    }

    private val contentFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterList = arrayListOf<Content>()

            if (constraint == null || constraint.isEmpty()){
                filterList.addAll(contentListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                for (content in contentListFull) {
                    if (content.getName().toLowerCase().contains(filterPattern))
                        filterList.add(content)
                }
            }

            val result = FilterResults()
            result.values = filterList

            return result
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            contentList = results.values as List<Content>
            notifyDataSetChanged()
        }
    }

}