package com.example.db_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.db_app.dataClasses.Genre
import kotlinx.android.synthetic.main.genre_item.view.*


class SpinnerAdapter(ctx: Context, genres: List<Genre>) : ArrayAdapter<Genre>(ctx, 0, genres) {

    private var selectedList = arrayListOf<Genre>()
    var customListener = false
    var listener = View.OnClickListener {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewRoot = convertView ?: LayoutInflater.from(context).inflate(
            com.example.db_app.R.layout.spinner_text_layout,
            parent,
            false
        )
        return viewRoot!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val genre = getItem(position)

        val view = LayoutInflater.from(context).inflate(
            com.example.db_app.R.layout.genre_item,
            parent,
            false
        )

        view.genre_desc.visibility = View.GONE

        val checkBox = view.check_box

        genre?.let {
            checkBox.text = genre.name
        }

        checkBox.isChecked = selectedList.contains(genre)

        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                selectedList.add(genre!!)
            } else {
                selectedList.remove(genre!!)
            }
            if (customListener)
                listener.onClick(it)
        }
        return view
    }

    fun getSelectedItems(): ArrayList<Genre> {
        return selectedList
    }

    fun resetGenre() {
        selectedList.clear()
    }

    fun setGenreList(list: ArrayList<Genre>) {
        this.selectedList = list
    }
}
