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
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.adapters.TopContentAdapter
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_top.*

class TopContentListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val topId = arguments?.getInt("id") ?: 1
        val t = arguments?.getInt("type")
        val type = when (t) {
            1 -> Type.FILM
            2 -> Type.MUSIC
            else -> Type.BOOK
        }

        val topContentAdapter = TopContentAdapter(topId, type)

        topContentAdapter.setOnItemClickListener(object : TopContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = topContentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).toContent(type, content.getId())
            }
        })

        top_recycler.layoutManager = LinearLayoutManager(requireContext())
        top_recycler.adapter = topContentAdapter
        (top_recycler.adapter as ContentAdapter).setContent(Type.BOOK)
    }
}