/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.db_app.R
import com.example.db_app.ViewModelTopsList
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.top_item.view.*

class TopsAdapter(private val viewModel: ViewModelTopsList) : RecyclerView.Adapter<TopsAdapter.TopsViewHolder>() {

    private var type = Type.BOOK
    private var topsList = listOf<ContentIdName>()
    private var size = 0
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopsAdapter.TopsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top_item, parent, false)
        return TopsViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TopsAdapter.TopsViewHolder, position: Int) {
        holder.itemView.run {
            if (position == topsList.size - 20)      // Подгружаем новый список, когда остаётся 10 элементов
                viewModel.getMoreTops()
            val top = topsList[position]
            top_item_name.text = top.getTopName()
            top_item_author.text = top.getTopAuthor()
        }
    }

    override fun getItemCount(): Int = size

    fun getTopByPosition(position: Int) = topsList[position]

    fun setContent(type: Type, newList: List<ContentIdName>, size: Int) {
        this.type = type
        this.size = size
        topsList = newList
        notifyDataSetChanged()      // TODO: 30.05.2021 убрать?
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class TopsViewHolder(itemView: View, listener: OnItemClickListener) :
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

