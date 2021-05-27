package com.example.db_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.example.db_app.FilterViewModel
import com.example.db_app.MainActivity
import com.example.db_app.adapters.SearchActorAdapter
import com.example.db_app.adapters.SpinnerAdapter
import com.example.db_app.dataClasses.*
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.Chip
import com.hootsuite.nachos.chip.ChipInfo
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.fragment_filter.view.*


class FilterFragment : Fragment() {

    private lateinit var viewModel: FilterViewModel
    var type = Type.BOOK
    private var selectedGenre = arrayListOf<Genre>()
    private var selectedActors = arrayListOf<ContentIdName>()
    private var selectedMakers = arrayListOf<ContentIdName>()
    private var rangeSeekbars = IntArray(6)
    private var notChanges = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.db_app.R.layout.fragment_filter, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity()).get(FilterViewModel::class.java)
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val listType = when (arguments?.getString("type")) {
            Type.FILM.t -> Type.FILM
            Type.MUSIC.t -> Type.MUSIC
            else -> Type.BOOK
        }

        (requireActivity() as MainActivity).typeContentList = type

        val yearBar = year_filter_bar
        val durationBar = duration_filter_bar
        val ratingBar = rating_filter_bar

        type = listType
        when (type) {
            Type.MUSIC -> {
                view.actors_filter_text.visibility = View.GONE
                view.search_actors_for_filter.visibility = View.GONE
                view.makers_filter_text.text = "Исполнители"
                yearBar.setMinValue(1500.toFloat())
                durationBar.setMinValue(2.toFloat())
                durationBar.setMaxValue(12.toFloat())
            }
            Type.BOOK -> {
                view.actors_filter_text.visibility = View.GONE
                view.search_actors_for_filter.visibility = View.GONE
                view.duration_filter_text.visibility = View.GONE
                view.duration_filter.visibility = View.GONE
                view.makers_filter_text.text = "Авторы"
                yearBar.setMinValue(1500.toFloat())
            }
            else -> {
            }
        }

        //Запись начальных значений слайдеров для сброса
        val listOfSliders = intArrayOf(
            yearBar.selectedMinValue.toInt(), yearBar.selectedMaxValue.toInt(),
            durationBar.selectedMinValue.toInt(), durationBar.selectedMaxValue.toInt(),
            ratingBar.selectedMinValue.toInt(), ratingBar.selectedMaxValue.toInt()
        )

        //Заполняем спиннер жанрами

        var genreList = listOf<Genre>()
        viewModel.getGenresForFilter(type).observe(requireActivity(), {
            genreList = it
        })

        val spinnerAdapter = SpinnerAdapter(requireActivity(), genreList)
        spinner_for_genre_filter.adapter = spinnerAdapter

        if (arguments?.getBoolean("restore")!!) {
            viewModel.getGenres().observe(requireActivity(), {
                selectedGenre = it
            })
            (spinner_for_genre_filter.adapter as SpinnerAdapter).setGenreList(selectedGenre)
        }


        //Формирование списка для выбора актёров/создателей
        search_actors_for_filter.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        search_makers_for_filter.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)

        when (type) {
            Type.FILM -> {

                var allActors = listOf<ContentIdName>()
                viewModel.getActorsForFilter().observe(requireActivity(), {
                    allActors = it
                })

                //Чипы с поиском актёров
                val adapterActors = SearchActorAdapter(requireActivity(), allActors)
                search_actors_for_filter.setAdapter(adapterActors)

                //Восстановление актёров/создателей
                if (arguments?.getBoolean("restore")!!) {
                    viewModel.getActors().observe(requireActivity(), {
                        selectedActors = it
                    })

                    adapterActors.setSelectedPeople(selectedActors)
                    setChips(selectedActors, search_actors_for_filter)
                }


                var allMakers = listOf<ContentIdName>()
                viewModel.getPeopleForFilter(type).observe(requireActivity(), {
                    allMakers = it
                })

                //Чипы с поиском создателей
                val adapterMakers = SearchActorAdapter(requireActivity(), allMakers)
                search_makers_for_filter.setAdapter(adapterMakers)

                //Восстановление актёров/создателей
                if (arguments?.getBoolean("restore")!!) {

                    viewModel.getMakers().observe(requireActivity(), {
                        selectedMakers = it
                    })

                    adapterMakers.setSelectedPeople(selectedMakers)
                    setChips(selectedMakers, search_makers_for_filter)
                }

            }

            else -> {
                var allMakers = listOf<ContentIdName>()
                viewModel.getPeopleForFilter(type).observe(requireActivity(), {
                    allMakers = it
                })

                //Чипы с поиском создателей
                val adapterMakers = SearchActorAdapter(requireActivity(), allMakers)
                search_makers_for_filter.setAdapter(adapterMakers)

                //Восстановление создателей
                if (arguments?.getBoolean("restore")!!) {
                    viewModel.getMakers().observe(requireActivity(), {
                        selectedMakers = it
                    })

                    adapterMakers.setSelectedPeople(selectedMakers)
                    setChips(selectedMakers, search_makers_for_filter)
                }
            }
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

        //Восстановаление данных, если повторно открыта фильтрация для того же листа
        if (arguments?.getBoolean("restore")!!) {

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


        //Удаление создателей
        search_actors_for_filter.setOnChipClickListener { chip, motionEvent ->
            removeChip(search_actors_for_filter, chip)
        }

        search_makers_for_filter.setOnChipClickListener { chip, motionEvent ->
            removeChip(search_makers_for_filter, chip)
        }

        //Кнопка фильтрации
        button_choose_filter.setOnClickListener {
            selectedGenre =
                (spinner_for_genre_filter.adapter as SpinnerAdapter).getSelectedItems()
            when (type) {
                Type.FILM -> {
                    selectedActors =
                        (search_actors_for_filter.adapter as SearchActorAdapter).getSelectedPeople()
                    selectedMakers =
                        (search_makers_for_filter.adapter as SearchActorAdapter).getSelectedPeople()

                    viewModel.setActors(selectedActors)
                    viewModel.setMakers(selectedMakers)
                }
                else -> {
                    selectedMakers =
                        (search_makers_for_filter.adapter as SearchActorAdapter).getSelectedPeople()

                    viewModel.setMakers(selectedMakers)
                }
            }

            rangeSeekbars = intArrayOf(
                yearBar.selectedMinValue.toInt(),
                yearBar.selectedMaxValue.toInt(),
                durationBar.selectedMinValue.toInt(),
                durationBar.selectedMaxValue.toInt(),
                ratingBar.selectedMinValue.toInt(),
                ratingBar.selectedMaxValue.toInt()
            )

            notChanges = isNotExistChanges(
                selectedGenre,
                selectedActors,
                selectedMakers,
                rangeSeekbars,
                listOfSliders
            )

            viewModel.setGenres(selectedGenre)
            viewModel.setYears(Pair(rangeSeekbars[0], rangeSeekbars[1]))
            viewModel.setDuration(Pair(rangeSeekbars[2], rangeSeekbars[3]))
            viewModel.setRating(Pair(rangeSeekbars[4], rangeSeekbars[5]))


            (requireActivity() as MainActivity).fromFilter(
                selectedGenre,
                selectedActors,
                selectedMakers,
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

            when (type) {
                Type.FILM -> {
                    (search_actors_for_filter.adapter as SearchActorAdapter).resetSelectedPeople()
                    (search_makers_for_filter.adapter as SearchActorAdapter).resetSelectedPeople()
                }
                else -> {
                    (search_makers_for_filter.adapter as SearchActorAdapter).resetSelectedPeople()
                }
            }

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
        slider.setMinStartValue(range.first.toFloat() - 0.001.toFloat())
            .setMaxStartValue(range.second.toFloat())
            .apply()
        textMin.text = range.first.toString()
        textMax.text = range.second.toString()
    }

    private fun isNotExistChanges(
        genres: ArrayList<Genre>,
        actors: ArrayList<ContentIdName>,
        makers: ArrayList<ContentIdName>,
        seekBarsCurrent: IntArray,
        seekBarsOld: IntArray
    ): Boolean {
        return when (type) {
            Type.FILM -> {
                actors.isEmpty() && makers.isEmpty()
            }
            else -> {
                makers.isEmpty()
            }
        } && genres.isEmpty() && seekBarsCurrent.contentEquals((seekBarsOld))

    }

    private fun setChips(listOfChips: ArrayList<ContentIdName>, viewWithChips: NachoTextView) {
        val saveChips = mutableListOf<ChipInfo>()
        for (chip in listOfChips) {
            saveChips.add(ChipInfo(chip.name, chip))
        }
        viewWithChips.setTextWithChips(saveChips)
    }

    private fun removeChip(filter: NachoTextView, chip: Chip) {
        val pos = filter.allChips.indexOf(chip)
        val delChipList = (filter.adapter as SearchActorAdapter).getSelectedPeople()

        delChipList.removeAt(pos)
        (filter.adapter as SearchActorAdapter).setSelectedPeople(delChipList)
        filter.setText("")
        setChips(delChipList, filter)
    }

}