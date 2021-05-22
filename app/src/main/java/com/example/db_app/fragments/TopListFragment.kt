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
        val userToken = (requireActivity() as MainActivity).getUserToken()!!

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
            true
        }



        super.onViewCreated(view, savedInstanceState)

    }
}