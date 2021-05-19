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
import kotlinx.android.synthetic.main.fragment_content_list.*
import java.util.*


class ContentListFragment : Fragment() {

    private var type = Type.BOOK
    var changeList = MutableLiveData(false)

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
                val content = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).toContent(type, content.getId())
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter
        (recycler.adapter as ContentAdapter).setContent(Type.BOOK)

        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        navigationView.setOnNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.books_str) -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        (recycler.adapter as ContentAdapter).setContent(type)
                    }
                }

                resources.getString(R.string.films_str) -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        (recycler.adapter as ContentAdapter).setContent(type)
                    }
                }

                resources.getString(R.string.mus_film_str) -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        (recycler.adapter as ContentAdapter).setContent(type)
                    }
                }
            }
            changeList.value = true
            true
        }

        super.onViewCreated(view, savedInstanceState)
    }

    /** =================================    Работа с меню   ================================ **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_search) {
            val searchView = item.actionView as SearchView
            // необходимо для того, чтобы SearchView не "сворачивался" в иконку
            searchView.isIconified = false
            searchView.queryHint = "Введите название" // TODO: 19.05.2021 Не работатет?

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
        return super.onOptionsItemSelected(item)
    }
}