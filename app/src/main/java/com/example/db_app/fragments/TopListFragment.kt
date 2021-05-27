/*
 * Copyright (c) 2021. Code by Juniell.
 */

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
import com.example.db_app.adapters.TopsAdapter
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_content_list.*

class TopListFragment : Fragment() {

    private var type = Type.BOOK
    private var changeList = MutableLiveData(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_content_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userToken = (requireActivity() as MainActivity).getUserToken()

        val topsAdapter = TopsAdapter(userToken)
        topsAdapter.setOnItemClickListener(object : TopsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val top = topsAdapter.getTopByPosition(position)
                (requireActivity() as MainActivity).toTop(type, top.id, top.getTopName(), top.getTopAuthor())
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = topsAdapter
        topsAdapter.setContent(Type.BOOK)

        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        navigationView.setOnNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.books_str) ->
                {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        topsAdapter.setContent(type)
                    }
                }

                resources.getString(R.string.films_str) ->
                {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        topsAdapter.setContent(type)
                    }
                }

                resources.getString(R.string.mus_film_str) ->
                {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        topsAdapter.setContent(type)
                    }
                }
            }
            changeList.value = true
            true
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        menu.findItem(R.id.toolbar_filter).isVisible = false
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
                   (recycler.adapter as TopsAdapter).filter.filter(newText)
                    return false
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }
}