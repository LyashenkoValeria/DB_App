package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.Artist
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.People
import kotlinx.android.synthetic.main.fragment_content_list.*

class ContentListFragment : Fragment() {

    private var type = ContentAdapter.Type.BOOK

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

        val contentAdapter = ContentAdapter()
        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO: 13.05.2021 Выдирать id и хранить type
                (requireActivity() as MainActivity).toContent(ContentAdapter.Type.BOOK, 1)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter

        search_content.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                contentAdapter.filter.filter(newText)
                return false
            }
        })


        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_books -> {
                    type = ContentAdapter.Type.BOOK
                    contentAdapter.setContent(ContentAdapter.Type.BOOK)
                    search_content.setQuery("", false)
                    search_content.clearFocus()
                }
                R.id.navigation_films -> {
                    type = ContentAdapter.Type.FILM
                    contentAdapter.setContent(ContentAdapter.Type.FILM)
                    search_content.setQuery("", false)
                    search_content.clearFocus()
                }
                R.id.navigation_music -> {
                    type = ContentAdapter.Type.MUSIC
                    contentAdapter.setContent(ContentAdapter.Type.MUSIC)
                    search_content.setQuery("", false)
                    search_content.clearFocus()
                }
            }
            false
        }

        filter_button.setOnClickListener {
            var needRestoreFilters = false
            if (arguments?.getInt("typeFromFilter") != null) {
                needRestoreFilters = arguments?.getInt("typeFromFilter") == type.t
            }
            (requireActivity() as MainActivity).toFilter(contentAdapter.type, needRestoreFilters)
        }


        if (arguments?.getBoolean("fromFilter") != null && arguments?.getBoolean("fromFilter") == true) {
            var filterGenre = arguments?.getParcelableArrayList<Genre>("genresList")!!
            var filterMakers = arrayListOf<People>()
            var filterArtists = arrayListOf<Artist>()

            when (arguments?.getInt("typeFromFilter")) {
                1 -> {
                    type = ContentAdapter.Type.FILM
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
                2 -> {
                    type = ContentAdapter.Type.MUSIC
                    filterArtists = arguments?.getParcelableArrayList("artistsList")!!
                }
                else -> {
                    type = ContentAdapter.Type.BOOK
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
            }
            var filterSeekBars = arguments?.getIntArray("seekBars")!!
            val notChanged = arguments?.getBoolean("notChanged")!!

            contentAdapter.setContent(type)
            contentAdapter.setFilter(filterGenre, filterMakers, filterArtists, filterSeekBars, notChanged)
            search_content.setQuery("", false)
        }


        super.onViewCreated(view, savedInstanceState)

    }

    /** =================================    Работа с меню   ================================ **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_prof -> {
                (requireActivity() as MainActivity).listToProfile()
            }
            R.id.menu_out -> {
                (requireActivity() as MainActivity).listToAuthorization()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}