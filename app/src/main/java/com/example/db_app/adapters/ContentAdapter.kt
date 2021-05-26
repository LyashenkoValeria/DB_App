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
import com.example.db_app.dataClasses.*
import kotlinx.android.synthetic.main.content_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ContentAdapter(private val userToken: String) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>(), Filterable {

    private val webClient = WebClient().getApi()
    var type = Type.BOOK
    private var contentList: List<ContentIdName> = listOf()
    var contentListFull: List<ContentIdName> = listOf()
    private lateinit var listener: OnItemClickListener

    var filterChanges = true


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

    fun setContent(type: Type, layout: TypeLayout) {
        this.type = type
        val call = when(layout) {
            TypeLayout.LIST -> webClient.getContentListByType(type.t, userToken)
            TypeLayout.VIEWED -> webClient.getViewedByType(type.t, userToken)
            TypeLayout.RECOMMEND -> webClient.getRecommend(type.t, userToken)
        }

        call.enqueue(object : Callback<List<ContentIdName>> {
            override fun onResponse(
                call: Call<List<ContentIdName>>,
                response: Response<List<ContentIdName>>
            ) {
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
            val contentId = contentList[position].id
            val call = webClient.getContentById(type.t, contentId, userToken)

            call.enqueue(object : Callback<Content> {
                override fun onResponse(call: Call<Content>, response: Response<Content>) {
                    holder.itemView.run {
                        val item = response.body()
                        if (item != null) { // TODO: 20.05.2021 Удалить или обрабатывать по-другому
                            poster.setImageResource(
                                when (type) {
                                    Type.BOOK -> R.drawable.book_poster
                                    Type.FILM -> R.drawable.film_poster
                                    Type.MUSIC -> R.drawable.music_poster
                                }
                            )
                            content_name.text = item.name
                            content_year.text = item.year.toString()
                            content_genre.text = item.getGenreString()
                            rating_bar.rating = item.rating.toFloat()
                            content_rating.text = item.rating.toString()
                        }
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

            if (constraint.isEmpty() && filterChanges){
                filterList.addAll(contentListFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                for (content in contentListFull) {
                    if (content.name.toLowerCase().startsWith(filterPattern))
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

    fun setFilter(
        genres: List<Genre>,
        actors: ArrayList<ContentIdName>,
        makers: ArrayList<ContentIdName>,
        rangeBars: IntArray,
        notChanged: Boolean,
        typeFilter: Type
    ) {
        this.type = typeFilter

        val genresId = arrayListOf<Int>()
        for (genre in genres) {
            genresId.add(genre.id)
        }

        val makersId = arrayListOf<Int>()
        for (maker in makers) {
            makersId.add(maker.id)
        }

        this.filterChanges = notChanged

        when (type) {
            Type.FILM -> {
                val actorsId = arrayListOf<Int>()
                for (actor in actors) {
                    actorsId.add(actor.id)
                }

                val call = webClient.getFilterFilm(rangeBars[0], rangeBars[1],
                    rangeBars[4], rangeBars[5], rangeBars[2], rangeBars[3], actorsId, makersId, genresId, userToken)

                call.enqueue(object : Callback<List<ContentIdName>> {
                    override fun onResponse(
                        call: Call<List<ContentIdName>>,
                        response: Response<List<ContentIdName>>
                    ) {
                        contentList = response.body()!!
                        contentListFull = response.body()!!
                        notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })
            }
            Type.BOOK -> {

                val call = webClient.getFilterBook(rangeBars[0], rangeBars[1],
                    rangeBars[4], rangeBars[5],  makersId, genresId, userToken)

                call.enqueue(object : Callback<List<ContentIdName>> {
                    override fun onResponse(
                        call: Call<List<ContentIdName>>,
                        response: Response<List<ContentIdName>>
                    ) {
                        contentList = response.body()!!
                        contentListFull = response.body()!!
                        notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })
            }
            Type.MUSIC -> {
                val call = webClient.getFilterMusic(genresId, makersId, rangeBars[2], rangeBars[3],
                    rangeBars[0], rangeBars[1], rangeBars[4], rangeBars[5], userToken)

                call.enqueue(object : Callback<List<ContentIdName>> {
                    override fun onResponse(
                        call: Call<List<ContentIdName>>,
                        response: Response<List<ContentIdName>>
                    ) {
                        contentList = response.body()!!
                        contentListFull = response.body()!!
                        notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })
            }
        }

    }
}