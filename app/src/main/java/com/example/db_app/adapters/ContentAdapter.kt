package com.example.db_app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.db_app.R
import com.example.db_app.ViewModelContentList
import com.example.db_app.WebClient
import com.example.db_app.dataClasses.*
import kotlinx.android.synthetic.main.content_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentAdapter(
    private val userToken: String,
    private val viewModel: ViewModelContentList
) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    private val webClient = WebClient().getApi()
    var type = Type.BOOK
    private lateinit var listener: OnItemClickListener
    private var contentList = listOf<Int>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_item, parent, false)
        return ContentViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        if (position == contentList.size - 11)      // Подгружаем новый список, когда остаётся 10 элементов
            viewModel.getMoreContent()
        holder.updateViewElement(holder, position)
    }

    override fun getItemCount(): Int = contentList.size

    fun getContentByPosition(position: Int) = contentList[position]

    fun setContent(type: Type, newList: List<Int>) {
        this.type = type
        contentList = newList
    }

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
            val call = webClient.getContentById(type.t, contentList[position], userToken)

            call.enqueue(object : Callback<Content> {
                override fun onResponse(call: Call<Content>, response: Response<Content>) {
                    holder.itemView.run {
                        val item = response.body()
                        if (item != null) {

                            val placeholder = when (type) {
                                Type.BOOK -> R.drawable.book_poster
                                Type.FILM -> R.drawable.film_poster
                                Type.MUSIC -> R.drawable.music_poster
                            }
                            Glide
                                .with(this)
                                .load(item.poster)
                                .placeholder(placeholder)
                                .error(placeholder)
                                .into(poster)
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

}