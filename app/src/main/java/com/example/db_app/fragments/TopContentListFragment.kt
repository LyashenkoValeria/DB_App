/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.adapters.TopContentAdapter
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Top
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_top.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopContentListFragment : Fragment() {

    private val webClient = WebClient().getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userToken = (requireActivity() as MainActivity).getUserToken()
        val topId = arguments?.getInt("id") ?: 1
        val topName = arguments?.getString("name")
        (requireActivity() as MainActivity).setToolbarTitle(topName?:"")
        val t = arguments?.getString("type")
        val type = when (t) {
            Type.FILM.t -> Type.FILM
            Type.MUSIC.t -> Type.MUSIC
            else -> Type.BOOK
        }

        top_name.text = topName
        top_author.text = arguments?.getString("author")

        val call = webClient.getTopByTypeAndId(type.t, topId, userToken)

        call.enqueue(object : Callback<Top> {
            override fun onResponse(call: Call<Top>, response: Response<Top>) {
                val top = response.body()!!
                val topContentAdapter = TopContentAdapter(
                    top.content.sortedBy { it.position },
                    type,
                    userToken
                )

                topContentAdapter.setOnItemClickListener(object :
                    TopContentAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val content = topContentAdapter.getContentByPosition(position)
                        (requireActivity() as MainActivity).toContent(type, content.id)
                    }
                })

                top_recycler.layoutManager = LinearLayoutManager(requireContext())
                top_recycler.adapter = topContentAdapter
            }

            override fun onFailure(call: Call<Top>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })

    }
}