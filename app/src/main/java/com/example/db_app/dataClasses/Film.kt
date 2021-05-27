package com.example.db_app.dataClasses

data class Film(
    val id: Int,
    val name: String,
    val year: Int,
    val duration: Int,
    val description: String,
    val poster: String?,
    val rating: Double,
    val filmSeries: ContentIdName?,
    val book: ContentIdName?,
    val music: List<ContentIdName>,
    val peoples: List<People>,
    val genres: List<String>,
    val viewed: Boolean,
    val ratingUser: Double?,
    val top: ContentIdName?,
    val topPosition: Int?
) {

    fun getMakersString(): String? {
        val makers = emptyList<People>().toMutableList()
        initMakers(makers)

        return if (makers.isEmpty())
            null
        else
            makers.joinToString(separator = ",\n") { it.fullname }
    }

    fun getActorsString(): String? {
        val actors = mutableListOf<People>()
        initActors(actors)

        return if (actors.isEmpty())
            null
        else
            actors.joinToString(separator = ",\n") { it.fullname }
    }

    fun getGenreString() = genres.joinToString(separator = ", ")

    private fun initActors(actorsList: MutableList<People>) {
        for (people in peoples)
            if (people.function == "Актёр")
                actorsList.add(people)
    }

    private fun initMakers(makersList: MutableList<People>) {
        for (people in peoples)
            if (people.function != "Актёр")
                makersList.add(people)
    }
}

//class FilmSeries(
//    val id: Int,
//    val name: String,
//    val description: String
//)