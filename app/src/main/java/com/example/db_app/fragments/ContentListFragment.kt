package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.*
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.Type
import com.example.db_app.dataClasses.TypeLayout
import kotlinx.android.synthetic.main.fragment_content_list.*


class ContentListFragment : Fragment() {

    private lateinit var viewModel: FilterViewModel
    private val viewModelContentList: ViewModelContentList by activityViewModels()
    private var type = Type.BOOK
    private var typeLayout = TypeLayout.LIST
    private var changeList = MutableLiveData(false)
    var userToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(FilterViewModel::class.java)
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
        userToken = (requireActivity() as MainActivity).getUserToken()

        val contentAdapter = ContentAdapter(userToken, viewModelContentList)
        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val contentId = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).typeContentList = type
                (requireActivity() as MainActivity).toContent(type, contentId)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter


        // Observer для отслеживания изменений в подгруженном списке контента
        viewModelContentList.currentList.observe(requireActivity() as MainActivity) {
            // Обновляем содержимое recycler
            contentAdapter.setContent(
                type,
                TypeLayout.LIST,
                mutableListOf<Int>().apply { addAll(it) }
            )
            if (viewModelContentList.newTypeFlag) {     // При обновлении типа
                contentAdapter.notifyDataSetChanged()    // перерисовываем содержимое recycler
                if (viewModelContentList.emptyFlag)     // Если получили пустой список
                    printEmptyMessage(typeLayout)       // выводим сообщение
                else                                    // Иначе - скроллим к 0 позиции
                    if (!viewModelContentList.initFlag)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
            }
        }

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
                viewModelContentList.updateContentForType(type)
                changeList.value = true
            }
            true
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun printEmptyMessage(layout: TypeLayout) {
        val msg = when (layout) {
            TypeLayout.LIST -> "Кажется, тут пусто."
            TypeLayout.RECOMMEND -> "Вы ещё не выбрали предпочтения в " + when (type) {
                Type.BOOK -> "книжных жанрах."
                Type.MUSIC -> "жанрах музыки."
                Type.FILM -> "жанрах фильмов."
            }
            TypeLayout.VIEWED -> "Вы ещё не просмотрели " + when (type) {
                Type.BOOK -> "ни одну книгу."
                Type.MUSIC -> "ни одну песню."
                Type.FILM -> "ни один фильм."
            }
        }
        (requireActivity() as MainActivity).makeToast(msg)
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
//                    (recycler.adapter as CursorAdapter).filter.filter(newText)
                    return false
                }
            })
        }

//        if (item.itemId == R.id.toolbar_filter) {
//            var needRestoreFilters = false
//            if (arguments?.getString("typeFromFilter") != null) {
//                needRestoreFilters = arguments?.getString("typeFromFilter") == type.t
//            }
//
//            (requireActivity() as MainActivity).toFilter(
//                (recycler.adapter as ContentAdapter).type,
//                needRestoreFilters
//            )
//        }
        return super.onOptionsItemSelected(item)
    }
}