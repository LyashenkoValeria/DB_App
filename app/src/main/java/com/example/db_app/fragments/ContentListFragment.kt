package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.db_app.*
import com.example.db_app.adapters.ContentAdapter
import com.example.db_app.dataClasses.ContentIdName
import com.example.db_app.dataClasses.Genre
import com.example.db_app.dataClasses.Type
import com.example.db_app.dataClasses.TypeLayout
import kotlinx.android.synthetic.main.fragment_content_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContentListFragment : Fragment() {

    private val webClient = WebClient().getApi()
    private lateinit var viewModel: FilterViewModel
    private val viewModelContentList: ViewModelContentList by activityViewModels()
    private var type = Type.BOOK
    private var changeList = MutableLiveData(false)
    var userToken = ""

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
        viewModelContentList.layoutType = TypeLayout.LIST
        if ((requireActivity() as MainActivity).needInit) {
            type = Type.BOOK
            viewModelContentList.init()
        } else
            type = viewModelContentList.type

        userToken = (requireActivity() as MainActivity).getUserToken()

        if (arguments?.getBoolean("fromFilter") != null && arguments?.getBoolean("fromFilter") == true) {
            arguments?.putBoolean("fromFilter", false)
            type = when (arguments?.getString("typeFromFilter")) {
                Type.FILM.t -> {
                    Type.FILM
                }
                Type.MUSIC.t -> {
                    Type.MUSIC
                }
                else -> {
                    Type.BOOK
                }
            }

            when (type) {
                Type.BOOK -> navigationView.selectedItemId = R.id.navigation_book
                Type.FILM -> navigationView.selectedItemId = R.id.navigation_film
                Type.MUSIC -> navigationView.selectedItemId = R.id.navigation_music
            }

//            if ((requireActivity() as MainActivity).needUpdateFilter)
            viewModelContentList.updateContentForType(type)
//            (requireActivity() as MainActivity).needUpdateFilter = false
        }


        val contentAdapter = ContentAdapter(userToken, viewModelContentList)
        contentAdapter.setOnItemClickListener(object : ContentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val contentId = contentAdapter.getContentByPosition(position)
                (requireActivity() as MainActivity).typeContentList = type
                (requireActivity() as MainActivity).toContent(type, contentId)
            }
        })

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = contentAdapter

        // Observer для отслеживания изменений в подгруженном списке контента
        viewModelContentList.currentList.observe(viewLifecycleOwner) {
            // Обновляем содержимое recycler
            contentAdapter.setContent(
                type,
                mutableListOf<Int>().apply { addAll(it) }
            )
            if ((requireActivity() as MainActivity).needUpdateFilter || (requireActivity() as MainActivity).needInit) {
                contentAdapter.notifyDataSetChanged()
                (requireActivity() as MainActivity).needUpdateFilter = false
                (requireActivity() as MainActivity).needInit = false
            } else
                if (viewModelContentList.newTypeFlag) {     // При обновлении типа
                    contentAdapter.notifyDataSetChanged()   // перерисовываем содержимое recycler

                    if (!viewModelContentList.initFlag) {
                        (recycler.layoutManager as LinearLayoutManager).scrollToPosition(0)
                        viewModelContentList.newTypeFlag = false
                    }
                }
        }

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
            var new = false
            when (it.title) {
                resources.getString(R.string.books_str) -> {
                    if (type != Type.BOOK) {
                        type = Type.BOOK
                        new = true
                    }
                }

                resources.getString(R.string.films_str) -> {
                    if (type != Type.FILM) {
                        type = Type.FILM
                        new = true
                    }
                }

                resources.getString(R.string.mus_film_str) -> {
                    if (type != Type.MUSIC) {
                        type = Type.MUSIC
                        new = true
                    }
                }
            }
            if (new) {
                loadDataForFilter()
                viewModelContentList.filterPattern = ""
                viewModelContentList.filterChanges = false
                viewModelContentList.updateContentForType(type)
                changeList.value = true
            }
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
                    viewModelContentList.filterPattern = newText
                    viewModelContentList.updateContentForType(type)
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
                type,
                needRestoreFilters
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDataForFilter() {
        var checkGenres = listOf<Genre>()
        viewModel.getGenresForFilter(type).observe(requireActivity(), {
            checkGenres = it
        })

        if (checkGenres.isEmpty()) {

            val callGenre = webClient.getGenreByType(type.t, userToken)
            callGenre.enqueue(object : Callback<List<Genre>> {
                override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                    if (response.body() != null)
                        viewModel.setGenresForFilter(type, response.body()!!)
                }

                override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                    Log.d("db", "Response = $t")
                }
            })
        }

        when (type) {
            Type.FILM -> {
                val callActors = webClient.getPeopleForFilm(true, userToken)
                callActors.enqueue(object : Callback<List<ContentIdName>> {
                    override fun onResponse(
                        call: Call<List<ContentIdName>>,
                        response: Response<List<ContentIdName>>
                    ) {
                        if (response.body() != null)
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
                        if (response.body() != null)
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
                        if (response.body() != null)
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