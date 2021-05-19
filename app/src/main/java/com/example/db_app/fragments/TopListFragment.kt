/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.adapters.TopsAdapter
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_content_list.*

class TopListFragment : Fragment() {

    private var type = Type.BOOK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        search_content.visibility = View.GONE

        val topsAdapter = TopsAdapter()
        topsAdapter.setOnItemClickListener(object : TopsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = topsAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).toTop(type, content.getId())
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = topsAdapter
        (recycler.adapter as TopsAdapter).setContent(Type.BOOK)

        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        navigationView.setOnNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.books_str) ->
                {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        (recycler.adapter as TopsAdapter).setContent(type)
                    }
                }

                resources.getString(R.string.films_str) ->
                {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        (recycler.adapter as TopsAdapter).setContent(type)
                    }
                }

                resources.getString(R.string.mus_film_str) ->
                {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        (recycler.adapter as TopsAdapter).setContent(type)
                    }
                }
            }
            true
        }



        super.onViewCreated(view, savedInstanceState)

    }
}