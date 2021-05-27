package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.FilterViewModel
import com.example.db_app.MainActivity
import com.example.db_app.R
import com.example.db_app.WebClient
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.*
import kotlinx.android.synthetic.main.fragment_content_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ContentListFragment : Fragment() {

    private lateinit var viewModel: FilterViewModel
    private val webClient = WebClient().getApi()
    private var type = Type.BOOK
    private var changeList = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(FilterViewModel::class.java)
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
//        type = (requireActivity() as MainActivity).typeContentList ?: Type.BOOK

        val contentAdapter = ContentAdapter(userToken)

        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val content = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).typeContentList = type
                (requireActivity() as MainActivity).toContent(type, content.id)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter

        if (arguments?.getBoolean("fromFilter") != null && arguments?.getBoolean("fromFilter") == true) {
            arguments?.putBoolean("fromFilter", false)
            val filterGenre = arguments?.getParcelableArrayList<Genre>("genresList")!!

            var filterActors = arrayListOf<ContentIdName>()
            val filterMakers: ArrayList<ContentIdName>

            when (arguments?.getString("typeFromFilter")) {
                Type.FILM.t -> {
                    type = Type.FILM
                    filterActors = arguments?.getParcelableArrayList("actorsList")!!
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
                Type.MUSIC.t -> {
                    type = Type.MUSIC
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
                else -> {
                    type = Type.BOOK
                    filterMakers = arguments?.getParcelableArrayList("makersList")!!
                }
            }
            val filterSeekBars = arguments?.getIntArray("seekBars")!!
            val notChanged = arguments?.getBoolean("notChanged")!!


            (recycler.adapter as ContentAdapter).setFilter(
                filterGenre,
                filterActors,
                filterMakers,
                filterSeekBars,
                notChanged,
                type
            )
            changeList.value = true
        } else {
            (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.LIST)
        }

//        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(position)

        // Установка листенера на toolbar
        val toolbarListener = View.OnClickListener {
            if ((requireActivity() as MainActivity).navController.currentDestination?.id == R.id.contentListFragment)
                (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
        }
        (requireActivity() as MainActivity).setToolbarListener(toolbarListener)


        // Установка листенеров ни нижную навигацию
        navigationView.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }

        loadDataForFilter()

        navigationView.setOnNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.books_str) -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        loadDataForFilter()
                        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.LIST)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }

                resources.getString(R.string.films_str) -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        loadDataForFilter()
                        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.LIST)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }

                resources.getString(R.string.mus_film_str) -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        loadDataForFilter()
                        (recycler.adapter as ContentAdapter).setContent(type, TypeLayout.LIST)
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                    }
                }
            }
            changeList.value = true
            true
        }

        super.onViewCreated(view, savedInstanceState)
    }

    /** =================================    Работа с toolbar   ================================ **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_search) {
            val searchView = item.actionView as SearchView
            searchView.queryHint = "Введите название"
            // необходимо для того, чтобы SearchView не "сворачивался" в иконку
            searchView.isIconified = false
            searchView.setOnCloseListener {
                searchView.isIconified = false
                true
            }

            // observer для очищения и скрытия searchView
            changeList.observe(this) {
                if (changeList.value == true) {
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                    changeList.value = false
                    item.collapseActionView()
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    (recycler.adapter as ContentAdapter).filter.filter(newText)
                    return false
                }
            })
        }

        if (item.itemId == R.id.toolbar_filter) {
            var needRestoreFilters = false
            if (arguments?.getString("typeFromFilter") != null) {
                needRestoreFilters = arguments?.getString("typeFromFilter") == type.t
            }

            (requireActivity() as MainActivity).toFilter(
                (recycler.adapter as ContentAdapter).type,
                needRestoreFilters
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDataForFilter() {
        val userToken = (requireActivity() as MainActivity).getUserToken()

        var checkGenres = listOf<Genre>()
        viewModel.getGenresForFilter(type).observe(requireActivity(), {
            checkGenres = it
        })

        if (checkGenres.isEmpty()) {

            val callGenre = webClient.getGenreByType(type.t, userToken)
            callGenre.enqueue(object : Callback<List<Genre>> {
                override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                    viewModel.setGenresForFilter(type, response.body()!!)
                }

                override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
        }

        if (checkEmptyList()) {
            when (type) {
                Type.FILM -> {

                    val callActors = webClient.getPeopleForFilm(true, userToken)

                    callActors.enqueue(object : Callback<List<ContentIdName>> {
                        override fun onResponse(
                            call: Call<List<ContentIdName>>,
                            response: Response<List<ContentIdName>>
                        ) {
                            viewModel.setActorsForFilter(response.body()!!)
                        }

                        override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })

                    val callMakers = webClient.getPeopleForFilm(false, userToken)

                    callMakers.enqueue(object : Callback<List<ContentIdName>> {
                        override fun onResponse(
                            call: Call<List<ContentIdName>>,
                            response: Response<List<ContentIdName>>
                        ) {
                            viewModel.setPeopleForFilter(type, response.body()!!)
                        }

                        override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }

                else -> {
                    val callMakers = webClient.getPeopleByType(type.t, userToken)

                    callMakers.enqueue(object : Callback<List<ContentIdName>> {
                        override fun onResponse(
                            call: Call<List<ContentIdName>>,
                            response: Response<List<ContentIdName>>
                        ) {
                            viewModel.setPeopleForFilter(type, response.body()!!)
                        }

                        override fun onFailure(call: Call<List<ContentIdName>>, t: Throwable) {
                            Log.d("db", "Response = $t")
                        }
                    })
                }
            }
        }

    }

    private fun checkEmptyList(): Boolean {
        var makers = listOf<ContentIdName>()
        viewModel.getPeopleForFilter(type).observe(requireActivity(), {
            makers = it
        })

        var makerIsEmpty = makers.isEmpty()

        if (type == Type.FILM) {
            var actors = listOf<ContentIdName>()
            viewModel.getActorsForFilter().observe(requireActivity(), {
                actors = it
            })

            makerIsEmpty = makerIsEmpty && actors.isEmpty()
        }
        return makerIsEmpty
    }
}