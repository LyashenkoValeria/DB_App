/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.ViewModelTopsList
import com.example.db_app.adapters.TopsAdapter
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Type
import com.example.db_app.dataClasses.TypeLayout
import kotlinx.android.synthetic.main.fragment_content_list.*

class TopListFragment : Fragment() {

    private var type = Type.BOOK
    private var changeList = MutableLiveData(false)
    private val viewModel: ViewModelTopsList by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_content_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if ((requireActivity() as MainActivity).needInitTops)
            viewModel.init()

        val topsAdapter = TopsAdapter(viewModel)
        topsAdapter.setOnItemClickListener(object : TopsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val top = topsAdapter.getTopByPosition(position)
                (requireActivity() as MainActivity)
                    .toTop(type, top.id, top.getTopName(), top.getTopAuthor())
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = topsAdapter

        // Observer для отслеживания изменений в подгруженном списке контента
        viewModel.currentList.observe(viewLifecycleOwner) {
            // Обновляем содержимое recycler
            topsAdapter.setContent(type, mutableListOf<ContentIdName>().apply { addAll(it) }, it.size)

            if ((requireActivity() as MainActivity).needInitTops) {
                topsAdapter.notifyDataSetChanged()
                (requireActivity() as MainActivity).needInitTops = false
            } else
                if (viewModel.newTypeFlag) {     // При обновлении типа
                    topsAdapter.notifyDataSetChanged()   // перерисовываем содержимое recycler
                        // Иначе - скроллим к 0 позиции
                        if (!viewModel.initFlag) {
                            (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                            viewModel.newTypeFlag = false
                        }
                }
        }

        // Установка листенера на toolbar
        val toolbarListener = View.OnClickListener {
            if ((requireActivity() as MainActivity).navController.currentDestination?.id == R.id.topListFragment)
                (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
        }
        (requireActivity() as MainActivity).setToolbarListener(toolbarListener)


        // Установка листенеров ни нижную навигацию
        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        navigationView.setOnNavigationItemSelectedListener {
            var new = false
            when (it.title) {
                resources.getString(R.string.books_str) -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        new = true
                    }
                }

                resources.getString(R.string.films_str) -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        new = true
                    }
                }

                resources.getString(R.string.mus_film_str) -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        new = true
                    }
                }
            }

            if (new) {
                viewModel.updateTopsForType(type)
                changeList.value = true
            }
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
                    viewModel.filterPattern = newText
                    viewModel.updateTopsForType(type)
                    return false
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }
}