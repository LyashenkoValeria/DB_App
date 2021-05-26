package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.activity_main.*
import com.example.db_app.dataClasses.Artist
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.People
import kotlinx.android.synthetic.main.fragment_content_list.*
import java.util.*


class ContentListFragment : Fragment() {

    private var type = Type.BOOK
    private var changeList = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userToken = (requireActivity() as MainActivity).getUserToken()
        type = (requireActivity() as MainActivity).typeContentList ?: Type.BOOK

        val contentAdapter = ContentAdapter(userToken)

        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).typeContentList = type
                (requireActivity() as MainActivity).toContent(type, content.id)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter
        (recycler.adapter as ContentAdapter).setContent(type)

        // Установка листенера на toolbar
        val toolbarListener = View.OnClickListener {
            if ((requireActivity() as MainActivity).navController.currentDestination?.id == R.id.contentListFragment)
                (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
        }
        (requireActivity() as MainActivity).setToolbarListener(toolbarListener)


        // Установка листенеров ни нижную навигацию
        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        navigationView.setOnNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.books_str) -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        (recycler.adapter as ContentAdapter).setContent(type)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }

                resources.getString(R.string.films_str) -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        (recycler.adapter as ContentAdapter).setContent(type)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }

                resources.getString(R.string.mus_film_str) -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        (recycler.adapter as ContentAdapter).setContent(type)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }
            }
            changeList.value = true
//            search_content.setQuery("", false)
//            search_content.clearFocus()
            true
        }


        if (arguments?.getBoolean("fromFilter") != null && arguments?.getBoolean("fromFilter") == true) {
            val filterGenre = arguments?.getParcelableArrayList<Genre>("genresList")!!
            val filterGenreStrings = mutableListOf<String>()
            filterGenre.forEach {
                filterGenreStrings.add(it.name)
            }

            var filterMakers = arrayListOf<People>()
            var filterArtists = arrayListOf<Artist>()

            when (arguments?.getString("typeFromFilter")) {
                Type.FILM.t -> {
                    type = Type.FILM
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
                Type.MUSIC.t -> {
                    type = Type.MUSIC
                    filterArtists = arguments?.getParcelableArrayList("artistsList")!!
                }
                else -> {
                    type = Type.BOOK
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
            }
            val filterSeekBars = arguments?.getIntArray("seekBars")!!
            val notChanged = arguments?.getBoolean("notChanged")!!

            contentAdapter.setContent(type)
            contentAdapter.setFilter(filterGenreStrings.toList(), filterMakers, filterArtists, filterSeekBars, notChanged)
            changeList.value = true
        }

        super.onViewCreated(view, savedInstanceState)
    }

    /** =================================    Работа с toolbar   ================================ **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_search) {
            val searchView = item.actionView as SearchView
            searchView.queryHint = "Введите название"
            // необходимо для того, чтобы SearchView не "сворачивался" в иконку
            searchView.isIconified = false
            searchView.setOnCloseListener {
                searchView.isIconified = false
                true
            }

            // observer для очищения и скрытия searchView
            changeList.observe(this) {
                if (changeList.value == true) {
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                    changeList.value = false
                    item.collapseActionView()
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchView.clearFocus()
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    (recycler.adapter as ContentAdapter).filter.filter(newText)
                    return false
                }
            })
        }

        if (item.itemId == R.id.toolbar_filter) {
            var needRestoreFilters = false
            if (arguments?.getString("typeFromFilter") != null) {
                needRestoreFilters = arguments?.getString("typeFromFilter") == type.t
            }
            (requireActivity() as MainActivity).toFilter((recycler.adapter as ContentAdapter).type, needRestoreFilters)
        }
        return super.onOptionsItemSelected(item)
    }
}