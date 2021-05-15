package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.adapters.ContentAdapter
import kotlinx.android.synthetic.main.fragment_content_list.*


class ContentListFragment: Fragment(){

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

        val contentAdapter = ContentAdapter()
        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO: 13.05.2021 Выдирать id и хранить type
                (requireActivity() as MainActivity).toContent(ContentAdapter.Type.BOOK, 1)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter

        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_books -> {
                    contentAdapter.setContent(ContentAdapter.Type.BOOK)
                }
                R.id.navigation_films -> {
                    contentAdapter.setContent(ContentAdapter.Type.FILM)
                }
                R.id.navigation_music -> {
                    contentAdapter.setContent(ContentAdapter.Type.MUSIC)
                }
            }
            false
        }

        super.onViewCreated(view, savedInstanceState)

    }

    /** =================================    Работа с меню   ================================ **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_prof -> {
                (requireActivity() as MainActivity).listToProfile()
            }
            R.id.menu_out -> {
                (requireActivity() as MainActivity).listToAuthorization()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}