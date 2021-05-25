/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.db_app.dataClasses.ContentIdName
import kotlinx.android.synthetic.main.genre_item.view.*
import kotlinx.android.synthetic.main.spinner_text_layout.view.*

class SpinnerAdapterIdName (ctx: Context, contents: List<ContentIdName>) : ArrayAdapter<ContentIdName>(ctx, 0, contents) {

    private var selectedList = mutableListOf<ContentIdName>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewRoot = convertView ?: LayoutInflater.from(context).inflate(
            com.example.db_app.R.layout.spinner_text_layout,
            parent,
            false
        )
        return viewRoot!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val content = getItem(position)

        val view = LayoutInflater.from(context).inflate(
            com.example.db_app.R.layout.genre_item,
            parent,
            false
        )

        view.auth_text.text = "Роль"

        view.genre_desc.visibility = View.GONE

        val checkBox = view.check_box

        content?.let {
            checkBox.text = content.name
        }

        checkBox.isChecked = selectedList.contains(content)
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                selectedList.add(content!!)
            } else {
                selectedList.remove(content!!)
            }
        }
        return view
    }

    fun getSelectedItems(): List<ContentIdName> {
        return selectedList
    }

    fun resetGenre() {
        selectedList.clear()
    }

    fun setGenreList(list: List<ContentIdName>) {
        this.selectedList = list.toMutableList()
    }

}
