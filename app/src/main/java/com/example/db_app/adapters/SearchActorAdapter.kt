package com.example.db_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.db_app.dataClasses.People
import java.util.*
import kotlin.collections.ArrayList


class SearchActorAdapter(ctx: Context, allPeople: List<People>) : ArrayAdapter<People>(ctx, 0, allPeople), Filterable {

    private val fullPeopleList = ArrayList(allPeople)
    private var selectedPeople = arrayListOf<People>()

    override fun getFilter(): Filter {
        return peopleFilter
    }

    private val peopleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterList = arrayListOf<People>()

            if ((constraint == null || constraint.isEmpty()) && selectedPeople.size == 0){
                filterList.addAll(fullPeopleList)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                for (people in fullPeopleList) {
                    if (people.getFullName().toLowerCase(Locale.getDefault()).startsWith(
                            filterPattern
                        ) && !selectedPeople.contains(people))
                        filterList.add(people)
                }
            }

            val result = FilterResults()
            result.values = filterList

            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            addAll(results.values as ArrayList<People>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as People).getFullName()
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

        val people = getItem(position)

        people?.let {
            textLine.text = people.getFullName()
        }

        textLine.setOnTouchListener { v, event ->
            selectedPeople.add(people!!)
            false
        }

        return textLine
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }

    fun getSelectedPeople(): ArrayList<People>{
        return selectedPeople
    }

    fun resetSelectedPeople(){
        with(selectedPeople){
            clear()
        }
    }

    fun setSelectedPeople(newList: ArrayList<People>){
        this.selectedPeople = newList
    }

}