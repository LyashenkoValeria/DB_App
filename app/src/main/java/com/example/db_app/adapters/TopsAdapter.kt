/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.top_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TopsAdapter (private val userToken: String): RecyclerView.Adapter<TopsAdapter.TopsViewHolder>(),
    Filterable {

    private val webClient = WebClient().getApi()
    private var type = Type.BOOK
    private var topsList = listOf<ContentIdName>()
    private var topsListFull = listOf<ContentIdName>()

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
            top_item_name.text = top.getTopName()
            top_item_author.text = top.getTopAuthor()
//            val l = resources.getDrawable(R.drawable.nav_header_backgroud, context.theme) as ShapeDrawable
//            l.mutate().s
//            val g = GradientDrawable()
//            g.gradientType
        }
    }

    override fun getItemCount(): Int = topsList.size

    fun getTopByPosition(position: Int) = topsList[position]

    fun setContent(type: Type) {
        this.type = type

        val call = webClient.getTopsByType(type.t, userToken)

        call.enqueue(object : Callback<List<ContentIdName>> {
            override fun onResponse(call: Call<List<ContentIdName>>, response: Response<List<ContentIdName>>) {
                topsList = response.body()!!
                topsListFull = response.body()!!
                notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
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

    override fun getFilter(): Filter {
        return topFilter
    }

    private val topFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterList = arrayListOf<ContentIdName>()

            if (constraint.isEmpty()){
                filterList.addAll(topsListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                for (top in topsListFull) {
                    if (top.name.toLowerCase().contains(filterPattern))
                        filterList.add(top)
                }
            }

            val result = FilterResults()
            result.values = filterList

            return result
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            topsList = results.values as List<ContentIdName>
            notifyDataSetChanged()
        }
    }
}

