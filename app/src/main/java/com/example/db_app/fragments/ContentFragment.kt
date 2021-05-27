package com.example.db_app.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.adapters.MusicForFilmAdapter
import com.example.db_app.dataClasses.Book
import com.example.db_app.dataClasses.Film
import com.example.db_app.dataClasses.Music
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.content_item.view.*
import kotlinx.android.synthetic.main.fragment_content.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentFragment : Fragment() {
    private var type = Type.BOOK
    private val webClient = WebClient().getApi()
    private var userToken: String? = null
    private var userPermission = 1
    private var notViewedMsg = ""
    private var notRatedMsg = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userToken = (requireActivity() as MainActivity).getUserToken()
        userPermission = (requireActivity() as MainActivity).getUserPermission()
//        userPermission = 2

        if (userPermission == 2) {
            content_id_row.visibility = View.VISIBLE
            content_id.visibility = View.VISIBLE
        }

        val id = arguments?.getInt("id") ?: 1
        when (arguments?.getString("type")) {
            Type.FILM.t -> {
                type = Type.FILM
                notViewedMsg = resources.getString(R.string.film_not_viewed)
                notRatedMsg = resources.getString(R.string.film_not_rated)
                drawFilm(id)
            }
            Type.MUSIC.t -> {
                type = Type.MUSIC
                notViewedMsg = resources.getString(R.string.music_not_viewed)
                notRatedMsg = resources.getString(R.string.music_not_rated)
                drawMusic(id)
            }
            else -> {
                type = Type.BOOK
                notViewedMsg = resources.getString(R.string.book_not_viewed)
                notRatedMsg = resources.getString(R.string.book_not_rated)
                drawBook(id)
            }
        }
    }


    private fun drawBook(id: Int) {
        val call = webClient.getBookForUser(id, userToken!!)
        call.enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                val book = response.body()!!
                drawViewBook(book)
                initListeners(id)
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun drawViewBook(book: Book) {
        // отображение названия в toolbar
        (requireActivity() as MainActivity).setToolbarTitle(book.name)

//        content_poster.setImageResource(R.drawable.book_poster)
        Glide
            .with(this)
            .load(book.poster)
            .placeholder(R.drawable.book_poster)
            .error(R.drawable.book_poster)
            .into(content_poster)
        if (userPermission == 2)
            content_id.text = book.id.toString()
        content_name.text = book.name

        content_year.text = book.year.toString()
        book_music_author.text = book.getAuthorsString()
        content_genre.text = book.getGenreString()

        // Скрытие и отображение серии книг
        content_series.run {
            val bookSeries = book.bookSeries
            visibility = if (bookSeries != null) {
                content_series_row.visibility = View.VISIBLE
                content_series_row.text = "Серия книг"
                text = bookSeries.name
                View.VISIBLE
            } else {
                content_series_row.visibility = View.GONE
                View.GONE
            }
        }

        // Скрытие и отображение топов
        val bookTop = book.top
        if (bookTop == null) {
            content_top_row.visibility = View.GONE
            content_top.visibility = View.GONE
            content_pos_row.visibility = View.GONE
            content_pos.visibility = View.GONE
        } else {
            val topName = bookTop.getTopName()
            val spannableString = SpannableString(topName)
            spannableString.setSpan(
                UnderlineSpan(),
                0,
                topName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            content_top.text = spannableString
            content_pos.text = book.topPosition.toString()
            // листенер для перехода к топу
            content_top.setOnClickListener {
                (requireActivity() as MainActivity).toTop(type, bookTop.id, bookTop.getTopName(), bookTop.getTopAuthor())
            }
        }

        content_rating.text = book.rating.toString()
        book_film_desc.text = book.description

        // Отображение необходимого текста, оценки пользователя и иконки "Просмотрено"
        val bookViewed = book.viewed
        if (bookViewed) {
            val bookUserRating = book.ratingUser
            content_info.text = if (bookUserRating == null)
                notRatedMsg
            else
                resources.getString(R.string.your_rating, book.ratingUser.toString())
            content_user_rating.setIsIndicator(false)
            content_user_rating.rating = bookUserRating?.toFloat() ?: 0f
            content_user_viewed.isChecked = true
        } else {
            content_info.text = notViewedMsg
            content_user_rating.setIsIndicator(true)
            content_user_rating.rating = 0f
            content_user_viewed.isChecked = false
        }

        // Скрываем не нужное
        film_music_dur_row.visibility = View.GONE
        film_music_dur.visibility = View.GONE
        film_actor_row.visibility = View.GONE
        film_actor.visibility = View.GONE
        film_maker_row.visibility = View.GONE
        film_maker.visibility = View.GONE
        film_book_row.visibility = View.GONE
        film_book.visibility = View.GONE
        film_music_row.visibility = View.GONE
        film_music.visibility = View.GONE
    }

    private fun drawFilm(id: Int) {
        val call = webClient.getFilmForUser(id, userToken!!)
        call.enqueue(object : Callback<Film> {
            override fun onResponse(call: Call<Film>, response: Response<Film>) {
                val film = response.body()!!
                drawViewFilm(film)
                initListeners(id)
            }

            override fun onFailure(call: Call<Film>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun drawViewFilm(film: Film) {
        // отображение названия в toolbar
        (requireActivity() as MainActivity).setToolbarTitle(film.name)

//        content_poster.setImageResource(R.drawable.film_poster)
        Glide
            .with(this)
            .load(film.poster)
            .placeholder(R.drawable.film_poster)
            .error(R.drawable.film_poster)
            .into(content_poster)
        if (userPermission == 2)
            content_id.text = film.id.toString()
        content_name.text = film.name

        content_year.text = film.year.toString()
        film_music_dur.text = resources.getString(R.string.duration, film.duration)

        // Скрытие и отображение актёров фильма
        val actors = film.getActorsString()
        if (actors == null) {
            film_actor_row.visibility = View.GONE
            film_actor.visibility = View.GONE
        } else
            film_actor.text = actors

        // Скрытие и отображение создателей фильма
        val makers = film.getMakersString()
        if (makers == null) {
            film_maker_row.visibility = View.GONE
            film_maker.visibility = View.GONE
        } else
            film_maker.text = makers

        content_genre.text = film.getGenreString()

        // Скрытие и отображение серии фильма
        content_series.run {
            val filmSeries = film.filmSeries
            visibility = if (filmSeries != null) {
                content_series_row.visibility = View.VISIBLE
                content_series_row.text = "Серия фильмов"
                text = filmSeries.name
                View.VISIBLE
            } else {
                content_series_row.visibility = View.GONE
                View.GONE
            }
        }

        // Скрытие и отображение книги фильма
        val filmsBook = film.book
        if (filmsBook == null) {
            film_book_row.visibility = View.GONE
            film_book.visibility = View.GONE
        } else {
            val spannableString = SpannableString(filmsBook.name)
            spannableString.setSpan(
                UnderlineSpan(),
                0,
                filmsBook.name.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            film_book.text = spannableString
            // листенер для перехода к книге
            film_book.setOnClickListener {
                (requireActivity() as MainActivity).toContent(Type.BOOK, filmsBook.id)
            }
        }

        // Скрытие и отображение музыки фильма
        val filmsMusic = film.music
        if (filmsMusic.isEmpty()) {
            film_music_row.visibility = View.GONE
            film_music.visibility = View.GONE
        } else {
            val musicAdapter = MusicForFilmAdapter(filmsMusic)
            // листенер для перехода к музыке
            val musicListener = object : MusicForFilmAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val musicId = musicAdapter.getMusicIdByPosition(position)
                    (requireActivity() as MainActivity).toContent(Type.MUSIC, musicId)
                }
            }
            musicAdapter.setOnItemClickListener(musicListener)
            film_music.layoutManager = LinearLayoutManager(requireContext())
            film_music.adapter = musicAdapter
        }

        // Скрытие и отображение топов
        val filmTop = film.top
        if (filmTop == null) {
            content_top_row.visibility = View.GONE
            content_top.visibility = View.GONE
            content_pos_row.visibility = View.GONE
            content_pos.visibility = View.GONE
        } else {
            val topName = filmTop.getTopName()
            val spannableString = SpannableString(topName)
            spannableString.setSpan(
                UnderlineSpan(),
                0,
                topName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            content_top.text = spannableString
            content_pos.text = film.topPosition.toString()
            // листенер для перехода к топу
            content_top.setOnClickListener {
                (requireActivity() as MainActivity).toTop(type, filmTop.id, filmTop.getTopName(), filmTop.getTopAuthor())
            }
        }

        content_rating.text = film.rating.toString()
        book_film_desc.text = film.description

        // Отображение необходимого текста, оценки пользователя и иконки "Просмотрено"
        val filmViewed = film.viewed
        if (filmViewed) {
            val bookUserRating = film.ratingUser
            content_info.text = if (bookUserRating == null)
                notRatedMsg
            else
                resources.getString(R.string.your_rating, film.ratingUser.toString())
            content_user_rating.setIsIndicator(false)
            content_user_rating.rating = bookUserRating?.toFloat() ?: 0f
            content_user_viewed.isChecked = true
        } else {
            content_info.text = notViewedMsg
            content_user_rating.setIsIndicator(true)
            content_user_rating.rating = 0f
            content_user_viewed.isChecked = false
        }

        // Скрываем не нужное
        book_music_author_row.visibility = View.GONE
        book_music_author.visibility = View.GONE
    }

    private fun drawMusic(id: Int) {
        val call = webClient.getMusicForUser(id, userToken!!)
        call.enqueue(object : Callback<Music> {
            override fun onResponse(call: Call<Music>, response: Response<Music>) {
                val music = response.body()!!
                drawViewMusic(music)
                initListeners(id)
            }

            override fun onFailure(call: Call<Music>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })
    }

    private fun drawViewMusic(music: Music) {
        // отображение названия в toolbar
        (requireActivity() as MainActivity).setToolbarTitle(music.name)

//        content_poster.setImageResource(R.drawable.music_poster)
        Glide
            .with(this)
            .load(music.poster)
            .placeholder(R.drawable.music_poster)
            .error(R.drawable.music_poster)
            .into(content_poster)
        if (userPermission == 2)
            content_id.text = music.id.toString()
        content_name.text = music.name

        content_year.text = music.year.toString()
        book_music_author.text = music.getArtistString()
        film_music_dur.text = resources.getString(R.string.duration, music.duration)
        content_genre.text = music.getGenreString()

        // Скрытие и отображение музыкальных альбомов
        val albums = music.albums
        if (albums.isEmpty()) {
            content_series_row.visibility = View.GONE
            content_series.visibility = View.GONE
        } else {
            content_series.text = music.getAlbumsString()
            content_series_row.text = "Альбом"
        }

        // Скрытие и отображение топов
        val musicTop = music.top
        if (musicTop == null) {
            content_top_row.visibility = View.GONE
            content_top.visibility = View.GONE
            content_pos_row.visibility = View.GONE
            content_pos.visibility = View.GONE
        } else {
            val topName = musicTop.getTopName()
            val spannableString = SpannableString(topName)
            spannableString.setSpan(
                UnderlineSpan(),
                0,
                topName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            content_top.text = spannableString
            content_pos.text = music.topPosition.toString()
            // листенер для перехода к топу
            content_top.setOnClickListener {
                (requireActivity() as MainActivity).toTop(type, musicTop.id, musicTop.getTopName(), musicTop.getTopAuthor())
            }
        }

        content_rating.text = music.rating.toString()

        // Отображение необходимого текста, оценки пользователя и иконки "Просмотрено"
        val musicViewed = music.viewed
        if (musicViewed) {
            val bookUserRating = music.ratingUser
            content_info.text = if (bookUserRating == null)
                notRatedMsg
            else
                resources.getString(R.string.your_rating, music.ratingUser.toString())
            content_user_rating.setIsIndicator(false)
            content_user_rating.rating = bookUserRating?.toFloat() ?: 0f
            content_user_viewed.isChecked = true
        } else {
            content_info.text = notViewedMsg
            content_user_rating.setIsIndicator(true)
            content_user_rating.rating = 0f
            content_user_viewed.isChecked = false
        }

        // скрываем не нужное
        film_actor_row.visibility = View.GONE
        film_actor.visibility = View.GONE
        film_maker_row.visibility = View.GONE
        film_maker.visibility = View.GONE
        film_book_row.visibility = View.GONE
        film_book.visibility = View.GONE
        film_music_row.visibility = View.GONE
        film_music.visibility = View.GONE
        book_film_desc.visibility = View.GONE
    }

    fun initListeners(id: Int) {
        content_user_viewed.setOnCheckedChangeListener { _, isChecked ->
            val callUserViewed =
                webClient.changeViewedContent(type.t, id, userToken!!, viewed = isChecked)

            callUserViewed.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        if (isChecked) {
                            content_user_rating.setIsIndicator(false)
                            content_info.text = notRatedMsg
                        } else {
                            content_user_rating.setIsIndicator(true)
                            content_user_rating.rating = 0f
                            content_info.text = notViewedMsg
                        }
                    } else
                        (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_unknown))
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
        }

        content_user_rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (content_user_viewed.isChecked) {
                val rtng = rating.toInt()
                val callChangeRating = webClient.changeViewedContent(
                    type.t,
                    id,
                    userToken!!,
                    viewed = true,
                    rating = if (rtng == 0) null else rtng
                )

                callChangeRating.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.code() == 200) {
                            content_info.text =
                                if (rtng == 0)
                                    notRatedMsg
                                else
                                    resources.getString(R.string.your_rating, rating.toString())
                        } else
                            (requireActivity() as MainActivity).makeToast(resources.getString(R.string.err_unknown))
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("db", "Response = $t")
                    }
                })
            }
        }
    }
}