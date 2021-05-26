/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.db_app.R
import com.example.db_app.dataClasses.ContentIdName
import kotlinx.android.synthetic.main.music_for_film_item.view.*

class MusicForFilmAdapter (private val musicList: List<ContentIdName>): RecyclerView.Adapter<MusicForFilmAdapter.MusicForFilmAdapterViewHolder>() {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicForFilmAdapter.MusicForFilmAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_for_film_item, parent, false)
        return MusicForFilmAdapterViewHolder(view, listener)
    }

    override fun onBindViewHolder(
        holder: MusicForFilmAdapter.MusicForFilmAdapterViewHolder,
        position: Int
    ) {
        holder.itemView.run {
            val item = musicList[position]
            val musicString = item.name
            val spannableString = SpannableString(musicString)
            spannableString.setSpan(
                UnderlineSpan(),
                0,
                musicString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            music_item.text = spannableString
        }
    }

    override fun getItemCount(): Int = musicList.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun getMusicIdByPosition(position: Int) = musicList[position].id

    inner class MusicForFilmAdapterViewHolder(
        itemView: View,
        listener: OnItemClickListener
    ) :
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
    }
}