package com.example.db_app.adapters

import android.util.Log
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
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.content_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ContentAdapter :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>(), Filterable
{

    private val webClient = WebClient().getApi()
    var type = Type.BOOK
//    private var contentList: List<Content> = listOf()
    private var contentList: List<ContentIdName> = listOf()
//    var contentListFull: List<Content> = listOf()
    var contentListFull: List<ContentIdName> = listOf()
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false)
        return ContentViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.updateViewElement(holder, position)
    }

    override fun getItemCount(): Int = contentList.size

    fun setContent(type: Type) {
        this.type = type

        val call = when (type) {
            Type.BOOK -> webClient.getBookList()
            Type.FILM -> webClient.getFilmList()
            Type.MUSIC -> webClient.getMusicList()
        }

        call.enqueue(object : Callback<List<ContentIdName>> {
            override fun onResponse(call: Call<List<ContentIdName>>, response: Response<List<ContentIdName>>) {
                contentList = response.body()!!
                contentListFull = response.body()!!
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

    inner class ContentViewHolder(itemView: View, listener: OnItemClickListener) :
        ViewHolder(itemView) {
        init {
            itemView.apply {
                setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION)
                        listener.onItemClick(pos)
                }
            }
        }

        fun updateViewElement(holder: ContentViewHolder, position: Int) {
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
                        name.text = item.getName()
                        year.text = item.getYear().toString()
                        genre.text = item.getGenreString()
                        rating.text = item.getRating().toString()

                    }
                }
                override fun onFailure(call: Call<Content>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
        }
    }

    override fun getFilter(): Filter {
        return contentFilter
    }

    private val contentFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterList = arrayListOf<ContentIdName>()

            if (constraint.isEmpty()){
                filterList.addAll(contentListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                for (content in contentListFull) {
                    if (content.getName().toLowerCase(Locale.getDefault()).contains(filterPattern))
                        filterList.add(content)
                }
            }

            val result = FilterResults()
            result.values = filterList

            return result
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            contentList = results.values as List<ContentIdName>
            notifyDataSetChanged()
        }
    }

}