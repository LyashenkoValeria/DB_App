package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.Type
import com.example.db_app.dataClasses.TypeLayout
import kotlinx.android.synthetic.main.fragment_content_list.*

class ViewedFragment : Fragment() {
    private var type = Type.BOOK

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
        type = (requireActivity() as MainActivity).typeViewedList ?: Type.BOOK

        val contentAdapter = ContentAdapter(requireActivity() as MainActivity)

        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).typeViewedList = type
                (requireActivity() as MainActivity).toContent(type, content.id)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter
        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.VIEWED)

        // Установка листенеров ни нижную навигацию
        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        navigationView.setOnNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.books_str) -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.VIEWED)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }

                resources.getString(R.string.films_str) -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.VIEWED)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }

                resources.getString(R.string.mus_film_str) -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.VIEWED)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }
            }
            true
        }

        super.onViewCreated(view, savedInstanceState)
    }
}