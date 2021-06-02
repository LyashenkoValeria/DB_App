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
import com.example.db_app.dataClasses.Top
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_top.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopContentListFragment : Fragment() {

    private val webClient = WebClient().getApi()
//    private lateinit var llManager: RecyclerView.LayoutManager
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        llManager = LinearLayoutManager(requireContext())
//        if (savedInstanceState != null && savedInstanceState.containsKey("ll"))
//            llManager.onRestoreInstanceState(savedInstanceState.getParcelable("ll"))
        if (savedInstanceState != null && savedInstanceState.containsKey("pos"))
            position = savedInstanceState.getInt("pos")
        return inflater.inflate(R.layout.fragment_top, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userToken = (requireActivity() as MainActivity).getUserToken()
        val topId = arguments?.getInt("id") ?: 1
        val topName = arguments?.getString("name")
        (requireActivity() as MainActivity).setToolbarTitle(topName ?: "")
        val type = when (arguments?.getString("type")) {
            Type.FILM.t -> Type.FILM
            Type.MUSIC.t -> Type.MUSIC
            else -> Type.BOOK
        }

        top_name.text = topName
        top_author.text = arguments?.getString("author")

//        if ((requireActivity() as MainActivity).needInitTopContent) {
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
                            this@TopContentListFragment.position = position
                            val content = topContentAdapter.getContentByPosition(position)
                            (requireActivity() as MainActivity).toContent(type, content.id)
                        }
                    })

                    top_recycler.layoutManager = LinearLayoutManager(requireContext())
//                    top_recycler.layoutManager = llManager
                    top_recycler.adapter = topContentAdapter
                    if (!(requireActivity() as MainActivity).needInitTopContent)
                        (top_recycler.layoutManager as LinearLayoutManager).scrollToPosition(position)
                }

                override fun onFailure(call: Call<Top>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
//            (requireActivity() as MainActivity).needInitTopContent = false
//        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
//        if (this :: llManager.isInitialized)
//            outState.putParcelable("ll", llManager.onSaveInstanceState())
         outState.putInt("pos", position)
        super.onSaveInstanceState(outState)
    }

}