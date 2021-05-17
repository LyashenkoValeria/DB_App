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
import com.example.db_app.dataClasses.Top
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.top_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopsAdapter : RecyclerView.Adapter<TopsAdapter.TopsViewHolder>() {

    private val webClient = WebClient().getApi()
    private var type = Type.BOOK
    private var topsList = listOf<Top>()

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
            val top = topsList[position]
            top_item_name.text = top.getName()
            top_item_author.text = top.getAuthor()
//            val l = resources.getDrawable(R.drawable.nav_header_backgroud, context.theme) as ShapeDrawable
//            l.mutate().s
//            val g = GradientDrawable()
//            g.gradientType
        }
    }

    override fun getItemCount(): Int = topsList.size

    fun getContentByPosition(position: Int) = topsList[position]

    fun setContent(type: Type) {
        this.type = type

        val call = when (type) {
            Type.BOOK -> webClient.getTopsBook()
            Type.FILM -> webClient.getTopsFilm()
            Type.MUSIC -> webClient.getTopsMusic()
        }

        call.enqueue(object : Callback<List<Top>> {
            override fun onResponse(call: Call<List<Top>>, response: Response<List<Top>>) {
                topsList = response.body()!!
                notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<Top>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    fun setOnItemClickListener(listener: TopsAdapter.OnItemClickListener) {
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

