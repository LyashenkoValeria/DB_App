package com.example.db_app.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.Type
import kotlinx.android.synthetic.main.fragment_content_list.*

class ContentListFragment : Fragment() {

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
        val contentAdapter = ContentAdapter()
        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).toContent(type, content.getId())
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter
        (recycler.adapter as ContentAdapter).setContent(Type.BOOK)

        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_book -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        (recycler.adapter as ContentAdapter).setContent(type)
                    }
                }
                R.id.navigation_film -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        (recycler.adapter as ContentAdapter).setContent(type)
                    }
                }
                R.id.navigation_music -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        (recycler.adapter as ContentAdapter).setContent(type)
                    }
                }
            }
            search_content.setQuery("", false)
            search_content.clearFocus()
            true
        }

        search_content.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                contentAdapter.filter.filter(newText)
                return false
            }
        })



//        navigationView.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_book -> {
//                    contentAdapter.setContent(Type.BOOK)
//                    search_content.setQuery("", false)
//                    search_content.clearFocus()
//                }
//                R.id.navigation_film -> {
//                    contentAdapter.setContent(Type.FILM)
//                    search_content.setQuery("", false)
//                    search_content.clearFocus()
//                }
//                R.id.navigation_music -> {
//                    contentAdapter.setContent(Type.MUSIC)
//                    search_content.setQuery("", false)
//                    search_content.clearFocus()
//                }
//            }
//            false
//        }

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