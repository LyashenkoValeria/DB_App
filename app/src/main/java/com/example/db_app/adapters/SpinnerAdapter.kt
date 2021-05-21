package com.example.db_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.db_app.dataClasses.Genre
import kotlinx.android.synthetic.main.genre_item.view.*


class SpinnerAdapter(ctx: Context, genres: ArrayList<Genre>) : ArrayAdapter<Genre>(ctx, 0, genres) {

    private var selectedList = arrayListOf<Genre>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(
                    com.example.db_app.R.layout.spinner_text_layout,
                    parent,
                    false
                )
        }
        return convertView!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val genre = getItem(position)

        val view = LayoutInflater.from(context).inflate(
            com.example.db_app.R.layout.genre_item,
            parent,
            false
        )

        val checkBox = view.check_box

        genre?.let {
            checkBox.text = genre.getName()
        }

        checkBox.isChecked = selectedList.contains(genre)
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                selectedList.add(genre!!)
            } else {
                selectedList.remove(genre!!)
            }
        }
        return view
    }

    fun getSelectedItems(): ArrayList<Genre> {
        return selectedList
    }

    fun resetGenre() {
        selectedList.clear()
    }

    fun setGenreList(list: ArrayList<Genre>){
        this.selectedList = list
    }

}
