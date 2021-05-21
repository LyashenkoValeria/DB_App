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
import java.util.*
import kotlin.collections.ArrayList

class ContentAdapter(private val userToken: String) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>(), Filterable {

    private val webClient = WebClient().getApi()
    var type = Type.BOOK
    private var typeDB = "book"
    private var contentList: List<ContentIdName> = listOf()
    var contentListFull: List<ContentIdName> = listOf()
    private lateinit var listener: OnItemClickListener

    var filterGenre = listOf<String>()
    var filterMakers = arrayListOf<People>()
    var filterArtists = arrayListOf<Artist>()
    var filterSeekBars = intArrayOf()
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

    fun setContent(type: Type) {
        this.type = type

        val call = when (type) {
            Type.BOOK -> {
                typeDB = "book"
                webClient.getBookList(userToken)
            }
            Type.FILM -> {
                typeDB = "film"
                webClient.getFilmList(userToken)
            }
            Type.MUSIC -> {
                typeDB = "music"
                webClient.getMusicList(userToken)
            }
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
            val call = webClient.getContentById(typeDB, contentId, userToken)

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
            val result = FilterResults()

            val filterList = arrayListOf<ContentIdName>()

            if (constraint.isEmpty() && filterChanges)
                filterList.addAll(contentListFull)
            else {
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                for (content1 in contentListFull) {
                    val callContent = webClient.getContentById(typeDB, content1.id, userToken)

                    callContent.enqueue(object : Callback<Content> {
                        override fun onResponse(call: Call<Content>, response: Response<Content>) {
                            val content = response.body()!!
                            if (content.name.toLowerCase(Locale.getDefault())
                                    .startsWith(filterPattern) && checkFilter(content)) {
                                filterList.add(content.toContentIdName())
                                result.values = filterList
                            }
                        }

                        override fun onFailure(call: Call<Content>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }
            }

            // todo подумать над возвратом result
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            if (constraint != null) {
                contentList = results.values as List<ContentIdName>
                notifyDataSetChanged()
            }
        }
    }

    fun setFilter(
        genres: List<String>,
        makers: ArrayList<People>,
        artists: ArrayList<Artist>,
        rangeBars: IntArray,
        notChanged: Boolean
    ) {
        this.filterGenre = genres
        if (type == Type.FILM || type == Type.BOOK) {
            this.filterMakers = makers
        } else {
            this.filterArtists = artists
        }
        this.filterSeekBars = rangeBars
        this.filterChanges = notChanged
    }


    //TODO: Поправить получение из базы в соответствии с новым кодом
    fun checkFilter(content: Content): Boolean {
        val existGenre = if (filterGenre.isNotEmpty()) {
            val contentGenres = content.genres.toMutableList()
            contentGenres.retainAll(filterGenre)
            contentGenres.isNotEmpty()
        } else
            true
        val boundYears =
            filterSeekBars[0] <= content.year && content.year <= filterSeekBars[1]
        val boundRating =
            filterSeekBars[4] <= content.rating && content.rating <= filterSeekBars[5]
        var addToFilter = existGenre && boundYears && boundRating

        when (type) {
            Type.FILM -> {
                if (filterMakers.isNotEmpty()) {
                    val callFilm = webClient.getFilmForUser(content.id, userToken)

                    callFilm.enqueue(object : Callback<Film> {
                        override fun onResponse(call: Call<Film>, response: Response<Film>) {
                            val film = response.body()!!
                            //checkDur
                            val checkDur = filterSeekBars[2] <= film.duration && film.duration <= filterSeekBars[3]
                            //checkMakers
                            val contentMakers = film.peoples.toMutableList()
                            contentMakers.retainAll(filterMakers)
                            addToFilter = addToFilter && contentMakers.isNotEmpty() && checkDur
                        }
                        override fun onFailure(call: Call<Film>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }
            }

            Type.MUSIC -> {
                if (filterArtists.isNotEmpty()) {
                    val callMusic = webClient.getMusicForUser(content.id, userToken)

                    callMusic.enqueue(object : Callback<Music> {
                        override fun onResponse(call: Call<Music>, response: Response<Music>) {
                            val music = response.body()!!
                            //checkDur
                            val checkDur =  filterSeekBars[2] <= music.duration && music.duration <= filterSeekBars[3]
                            //checkMakers
                            val contentArtists = music.artists.toMutableList()
                            contentArtists.retainAll(filterArtists)
                            addToFilter = addToFilter && contentArtists.isNotEmpty() && checkDur
                        }
                        override fun onFailure(call: Call<Music>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }
            }

            Type.BOOK -> {
                if (filterMakers.isNotEmpty()) {
                    val callBook = webClient.getBookForUser(content.id, userToken)

                    callBook.enqueue(object : Callback<Book> {
                        override fun onResponse(call: Call<Book>, response: Response<Book>) {
                            val book = response.body()!!
                            val contentMakers = book.authors.toMutableList()
                            contentMakers.retainAll(filterMakers)
                            addToFilter = addToFilter && contentMakers.isNotEmpty()
                        }
                        override fun onFailure(call: Call<Book>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }
            }
        }

        // todo: подумать над return
        return addToFilter
    }
}