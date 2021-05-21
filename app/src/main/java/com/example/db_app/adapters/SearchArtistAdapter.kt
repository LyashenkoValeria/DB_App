package com.example.db_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.db_app.dataClasses.Artist
import java.util.*
import kotlin.collections.ArrayList


class SearchArtistAdapter(ctx: Context, allArtist: List<Artist>) : ArrayAdapter<Artist>(ctx, 0, allArtist), Filterable {

    private val fullArtistList = ArrayList(allArtist)
    private var selectedArtists = arrayListOf<Artist>()

    override fun getFilter(): Filter {
        return artistFilter
    }

    private val artistFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterList = arrayListOf<Artist>()

            if ((constraint == null || constraint.isEmpty()) && selectedArtists.size == 0){
                filterList.addAll(fullArtistList)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                for (artist in fullArtistList) {
                    if (artist.getName().toLowerCase(Locale.getDefault()).startsWith(
                            filterPattern
                        ) && !selectedArtists.contains(artist))
                        filterList.add(artist)
                }
            }

            val result = FilterResults()
            result.values = filterList

            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            addAll(results.values as ArrayList<Artist>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as Artist).getName()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false
                )
        }
        var textLine = super.getView(position, convertView, parent) as TextView

        val artist = getItem(position)

        artist?.let {
            textLine.text = artist.getName()
        }

        textLine.setOnTouchListener { v, event ->
            selectedArtists.add(artist!!)
            false
        }

        return textLine
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }

    fun getSelectedArtists(): ArrayList<Artist>{
        return selectedArtists
    }

    fun resetSelectedArtists(){
        with(selectedArtists){
            clear()
        }
    }

    fun setSelectedArtists(newList: ArrayList<Artist>){
        this.selectedArtists= newList
    }

}