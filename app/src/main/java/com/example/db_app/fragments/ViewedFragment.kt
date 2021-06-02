package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.ViewModelContentList
import com.example.db_app.adapters.ContentAdapter

import com.example.db_app.dataClasses.Type
import com.example.db_app.dataClasses.TypeLayout
import kotlinx.android.synthetic.main.fragment_content_list.*

class ViewedFragment : Fragment() {

    private val viewModelContentList: ViewModelContentList by activityViewModels()
    private var type = Type.BOOK
    var userToken = ""

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
        viewModelContentList.layoutType = TypeLayout.VIEWED
        viewModelContentList.filterPattern = ""
        viewModelContentList.filterChanges = false

        if ((requireActivity() as MainActivity).needInitViewed) {
            viewModelContentList.init()
        }

        userToken = (requireActivity() as MainActivity).getUserToken()

//        viewModelContentList.updateContentForType(type)

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
        viewModelContentList.currentViewedList.observe(viewLifecycleOwner) {
            // Обновляем содержимое recycler
            contentAdapter.setContent(
                type,
                mutableListOf<Int>().apply { addAll(it) }
            )
            if ((requireActivity() as MainActivity).needInitViewed) {
                contentAdapter.notifyDataSetChanged()
                (requireActivity() as MainActivity).needInitViewed = false
            } else
                if (viewModelContentList.newTypeFlag) {     // При обновлении типа
                    contentAdapter.notifyDataSetChanged()   // перерисовываем содержимое recycler
                    if (viewModelContentList.emptyFlag)     // Если получили пустой список
                        printEmptyMessage()                 // выводим сообщение
                    else                                    // Иначе - скроллим к 0 позиции
                        if (!viewModelContentList.initFlag) {
                            (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                            viewModelContentList.newTypeFlag = false
                        }
                }
        }


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
                viewModelContentList.filterPattern = ""
                viewModelContentList.filterChanges = false
                viewModelContentList.updateContentForType(type)
            }
            true
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun printEmptyMessage() {
        val msg = "Вы ещё не просмотрели " + when (type) {
            Type.BOOK -> "ни одну книгу."
            Type.MUSIC -> "ни одну песню."
            Type.FILM -> "ни один фильм."
        }

        (requireActivity() as MainActivity).makeToast(msg)
    }

}
