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

        val userToken = (requireActivity() as MainActivity).getUserToken()!!
        // TODO: 21.05.2021 Привести топы на сервере к одинаковому виду!!!

        // TODO: 21.05.2021 Получать сам топ тут, а потом отдавать в адаптер?
        val topContentAdapter = TopContentAdapter(topId, type, userToken)

        topContentAdapter.setOnItemClickListener(object : TopContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = topContentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).toContent(type, content.id)
            }
        })

        top_recycler.layoutManager = LinearLayoutManager(requireContext())
        top_recycler.adapter = topContentAdapter
    }
}