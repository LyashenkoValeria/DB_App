package com.example.db_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.example.db_app.FilterViewModel
import com.example.db_app.MainActivity
import com.example.db_app.WebClient
import com.example.db_app.adapters.SearchActorAdapter
import com.example.db_app.adapters.SearchArtistAdapter
import com.example.db_app.adapters.SpinnerAdapter
import com.example.db_app.dataClasses.*
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.Chip
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.fragment_filter.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FilterFragment : Fragment() {

    private lateinit var viewModel: FilterViewModel
    private val webClient = WebClient().getApi()
    var type = Type.BOOK
    var selectedGenre = arrayListOf<Genre>()
    var selectedActors = arrayListOf<People>()
    var selectedMakers = arrayListOf<People>()
    var selectedArtists = arrayListOf<Artist>()
    var rangeSeekbars = IntArray(6)
    var notChanges = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.db_app.R.layout.fragment_filter, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(requireActivity()).get(FilterViewModel::class.java)

        viewModel.getGenres().observe(requireActivity(), {
            selectedGenre = it
        })

        viewModel.getYears().observe(requireActivity(), {
            rangeSeekbars[0] = it.first
            rangeSeekbars[1] = it.second
        })

        viewModel.getDuration().observe(requireActivity(), {
            rangeSeekbars[2] = it.first
            rangeSeekbars[3] = it.second
        })

        viewModel.getRating().observe(requireActivity(), {
            rangeSeekbars[4] = it.first
            rangeSeekbars[5] = it.second
        })
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userToken = (requireActivity() as MainActivity).getUserToken()!!

        val listType = when (arguments?.getString("type")) {
            Type.FILM.t -> Type.FILM
            Type.MUSIC.t -> Type.MUSIC
            else -> Type.BOOK
        }

//        val genreList = arrayListOf<Genre>()
        val peopleListTest = arrayListOf<People>()
        val artistListTest = arrayListOf<Artist>()

        //Тестовые данные. Дропнуть позже
//        genreList.add(Genre(1, "genre 1", "Это genre 1"))
//        genreList.add(Genre(2, "genre 2", "Это genre 2"))
//        genreList.add(Genre(3, "genre 3", "Это genre 3"))
//        genreList.add(Genre(4, "genre 4", "Это genre 4"))
//        genreList.add(Genre(5, "genre 5", "Это genre 5"))
//        genreList.add(Genre(6, "genre 6", "Это genre 6"))

        peopleListTest.add(People(1, "Author 1", 2000, "Режиссёр"))
        peopleListTest.add(People(2, "Author 2", 2000, "Сценарист"))
        peopleListTest.add(People(3, "Author 3", 2000, "Актёр"))
        peopleListTest.add(People(4, "Author 4", 2000, "Актёр"))
        peopleListTest.add(People(5, "Author 5", 2000, "Актёр"))

        artistListTest.add(Artist(1, "Singer1", "Певец"))
        artistListTest.add(Artist(2, "Singer2", "Певец"))
        artistListTest.add(Artist(3, "Group1", "Группа"))
        artistListTest.add(Artist(4, "Group2", "Группа"))
        artistListTest.add(Artist(5, "Group3", "Группа"))

        //---------------------------------------------------------------------------------

        val yearBar = year_filter_bar
        val durationBar = duration_filter_bar
        val ratingBar = rating_filter_bar

        type = listType
        when (type) {
            Type.FILM -> {
                view.artists_filter_text.visibility = View.GONE
                view.search_artists_for_filter.visibility = View.GONE
            }
            Type.MUSIC -> {
                view.actors_filter_text.visibility = View.GONE
                view.search_actors_for_filter.visibility = View.GONE
                view.makers_filter_text.visibility = View.GONE
                view.search_makers_for_filter.visibility = View.GONE
                yearBar.setMinValue(1500.toFloat())
                durationBar.setMinValue(2.toFloat())
                durationBar.setMaxValue(12.toFloat())
            }
            else -> {
                view.actors_filter_text.visibility = View.GONE
                view.search_actors_for_filter.visibility = View.GONE
                view.artists_filter_text.visibility = View.GONE
                view.search_artists_for_filter.visibility = View.GONE
                view.duration_filter_text.visibility = View.GONE
                view.duration_filter.visibility = View.GONE
                view.makers_filter_text.text = "Авторы"
                yearBar.setMinValue(1500.toFloat())
            }
        }

        //Запись начальных значений слайдеров для сброса
        val listOfSliders = intArrayOf(
            yearBar.selectedMinValue.toInt(), yearBar.selectedMaxValue.toInt(),
            durationBar.selectedMinValue.toInt(), durationBar.selectedMaxValue.toInt(),
            ratingBar.selectedMinValue.toInt(), ratingBar.selectedMaxValue.toInt()
        )

        //Заполняем спиннер жанрами
        val callGenre = webClient.getGenreByType(type.t, userToken)
        callGenre.enqueue(object : Callback<List<Genre>> {
            override fun onResponse(call: Call<List<Genre>>, response: Response<List<Genre>>) {
                val genreList = response.body()!!
                val spinnerAdapter = SpinnerAdapter(requireActivity(), genreList)
                spinner_for_genre_filter.adapter = spinnerAdapter
            }

            override fun onFailure(call: Call<List<Genre>>, t: Throwable) {
                Log.d("db", "Response = $t")
            }
        })


        //Формирование списка для выбора актёров/создателей
        search_actors_for_filter.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        search_makers_for_filter.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        search_artists_for_filter.addChipTerminator(
            '\n',
            ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL
        )

        val allActors = mutableListOf<People>()
        var allMakers = mutableListOf<People>()
        var allArtists = mutableListOf<Artist>()

        when (type) {
            Type.FILM -> {
                //TODO: Достать из базы все пиплов для фильмов
                val peopleList = peopleListTest
                for (people in peopleList) {
                    if (people.function != null && !people.function.toString()
                            .contains("Актёр")
                    ) {
                        allActors.add(people)
                    } else {
                        allMakers.add(people)
                    }
                }
            }
            Type.BOOK -> {
                //TODO: Достать из базы все пиплов для книг
                allMakers = peopleListTest
            }

            Type.MUSIC -> {
                //TODO: Достать из базы всех артистов
                allArtists = artistListTest
            }
        }

        //Чипы с поиском актёров
        val adapterActors = SearchActorAdapter(requireActivity(), allActors)
        search_actors_for_filter.setAdapter(adapterActors)

        //Чипы с поиском создателей
        val adapterMakers = SearchActorAdapter(requireActivity(), allMakers)
        search_makers_for_filter.setAdapter(adapterMakers)

        //Чипы с поиском артиста
        val adapterArtists = SearchArtistAdapter(requireActivity(), allArtists)
        search_artists_for_filter.setAdapter(adapterArtists)

        //Восстановаление данных, если повторно открыта фильтрация для того же листа
        if (arguments?.getBoolean("restore")!!) {
            (spinner_for_genre_filter.adapter as SpinnerAdapter).setGenreList(selectedGenre)

            when (type) {
                Type.FILM -> {
                    viewModel.getActors().observe(requireActivity(), {
                        selectedActors = it
                    })

                    selectedActors.removeAll(allMakers)
                    adapterActors.setSelectedPeople(selectedActors)
                    setChips(selectedActors, search_actors_for_filter)

                    viewModel.getMakers().observe(requireActivity(), {
                        selectedMakers = it
                    })

                    adapterMakers.setSelectedPeople(selectedMakers)
                    setChips(selectedMakers, search_makers_for_filter)
                }
                Type.MUSIC -> {
                    viewModel.getArtists().observe(requireActivity(), {
                        selectedArtists = it
                    })
                    adapterArtists.setSelectedArtists(selectedArtists)
                    setChipsArtists(selectedArtists, search_artists_for_filter)
                }
                Type.BOOK -> {
                    viewModel.getMakers().observe(requireActivity(), {
                        selectedMakers = it
                    })

                    adapterMakers.setSelectedPeople(selectedMakers)
                    setChips(selectedMakers, search_makers_for_filter)
                }
            }

            resetSeekBar(
                Pair(rangeSeekbars[0], rangeSeekbars[1]),
                yearBar,
                min_value_year,
                max_value_year
            )

            resetSeekBar(
                Pair(rangeSeekbars[2], rangeSeekbars[3]),
                durationBar,
                min_value_duration,
                max_value_duration
            )

            resetSeekBar(
                Pair(rangeSeekbars[4], rangeSeekbars[5]),
                ratingBar,
                min_value_rating,
                max_value_rating
            )
        }

        //Отображение по бокам значений слайдеров
        yearBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            min_value_year.text = minValue.toString()
            max_value_year.text = maxValue.toString()
        }

        durationBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            min_value_duration.text = minValue.toString()
            max_value_duration.text = maxValue.toString()
        }

        ratingBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            min_value_rating.text = minValue.toString()
            max_value_rating.text = maxValue.toString()
        }


        //Удаление создателей
        search_actors_for_filter.setOnChipClickListener { chip, motionEvent ->
            removeChip(search_actors_for_filter, chip, adapterActors)
        }

        search_makers_for_filter.setOnChipClickListener { chip, motionEvent ->
            removeChip(search_makers_for_filter, chip, adapterMakers)
        }

        search_artists_for_filter.setOnChipClickListener { chip, motionEvent ->
            removeChipArtists(search_artists_for_filter, chip, adapterArtists)
        }

        //Кнопка фильтрации
        button_choose_filter.setOnClickListener {
            selectedGenre = (spinner_for_genre_filter.adapter as SpinnerAdapter).getSelectedItems()
            selectedActors = adapterActors.getSelectedPeople()
            selectedMakers = adapterMakers.getSelectedPeople()
            selectedArtists = adapterArtists.getSelectedArtists()

            rangeSeekbars = intArrayOf(
                yearBar.selectedMinValue.toInt(), yearBar.selectedMaxValue.toInt(),
                durationBar.selectedMinValue.toInt(), durationBar.selectedMaxValue.toInt(),
                ratingBar.selectedMinValue.toInt(), ratingBar.selectedMaxValue.toInt()
            )

            notChanges = isNotExistChanges(
                selectedGenre,
                selectedActors,
                selectedMakers,
                selectedArtists,
                rangeSeekbars,
                listOfSliders
            )

            viewModel.setGenres(selectedGenre)
            viewModel.setActors(selectedActors)
            viewModel.setMakers(selectedMakers)
            viewModel.setArtists(selectedArtists)
            viewModel.setYears(Pair(rangeSeekbars[0], rangeSeekbars[1]))
            viewModel.setDuration(Pair(rangeSeekbars[2], rangeSeekbars[3]))
            viewModel.setRating(Pair(rangeSeekbars[4], rangeSeekbars[5]))


            (requireActivity() as MainActivity).fromFilter(
                selectedGenre,
                selectedActors,
                selectedMakers,
                selectedArtists,
                rangeSeekbars,
                type,
                true,
                notChanges
            )
        }

        //Кнопка очистки
        button_clear_filter.setOnClickListener {
            (spinner_for_genre_filter.adapter as SpinnerAdapter).resetGenre()

            search_actors_for_filter.setText("")
            search_makers_for_filter.setText("")
            search_artists_for_filter.setText("")

            adapterActors.resetSelectedPeople()
            adapterMakers.resetSelectedPeople()
            adapterArtists.resetSelectedArtists()

            resetSeekBar(
                Pair(listOfSliders[0], listOfSliders[1]),
                yearBar,
                min_value_year,
                max_value_year
            )
            resetSeekBar(
                Pair(listOfSliders[2], listOfSliders[3]),
                durationBar,
                min_value_duration,
                max_value_duration
            )
            resetSeekBar(
                Pair(listOfSliders[4], listOfSliders[5]),
                ratingBar,
                min_value_rating,
                max_value_rating
            )
        }
    }

    private fun resetSeekBar(
        range: Pair<Int, Int>,
        slider: CrystalRangeSeekbar,
        textMin: TextView,
        textMax: TextView
    ) {
        slider.setMinStartValue(range.first.toFloat()).setMaxStartValue(range.second.toFloat())
            .apply()
        textMin.text = range.first.toString()
        textMax.text = range.second.toString()
    }

    private fun isNotExistChanges(
        genres: ArrayList<Genre>,
        actors: ArrayList<People>,
        makers: ArrayList<People>,
        artists: ArrayList<Artist>,
        seekBarsCurrent: IntArray,
        seekBarsOld: IntArray
    ): Boolean {
        return when (type) {
            Type.FILM -> {
                actors.isEmpty() && makers.isEmpty()
            }
            Type.BOOK -> {
                makers.isEmpty()
            }

            Type.MUSIC -> {
                artists.isEmpty()
            }
            //todo: проверить
//        } && genres.isEmpty() && seekBarsCurrent == (seekBarsOld)
        } && genres.isEmpty() && seekBarsCurrent.contentEquals((seekBarsOld))

    }

    private fun setChips(listOfChips: ArrayList<People>, viewWithChips: NachoTextView) {
        val saveChips = mutableListOf<ChipInfo>()
        for (chip in listOfChips) {
            saveChips.add(ChipInfo(chip.fullname, chip))
        }
        viewWithChips.setTextWithChips(saveChips)
    }

    private fun setChipsArtists(listOfChips: ArrayList<Artist>, viewWithChips: NachoTextView) {
        val saveChips = mutableListOf<ChipInfo>()
        for (chip in listOfChips) {
            saveChips.add(ChipInfo(chip.name, chip))
        }
        viewWithChips.setTextWithChips(saveChips)
    }

    private fun removeChip(filter: NachoTextView, chip: Chip, adapter: SearchActorAdapter) {
        val pos = filter.allChips.indexOf(chip)
        val delChipList = adapter.getSelectedPeople()

        delChipList.removeAt(pos)
        adapter.setSelectedPeople(delChipList)
        filter.setText("")
        setChips(delChipList, filter)
    }

    private fun removeChipArtists(filter: NachoTextView, chip: Chip, adapter: SearchArtistAdapter) {
        val pos = filter.allChips.indexOf(chip)
        val delChipList = adapter.getSelectedArtists()

        delChipList.removeAt(pos)
        adapter.setSelectedArtists(delChipList)
        filter.setText("")
        setChipsArtists(delChipList, filter)
    }

}