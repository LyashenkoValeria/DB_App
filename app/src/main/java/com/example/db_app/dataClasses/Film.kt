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
    val music: List<ContentIdName>,   // TODO: 13.05.2021 надо это вообще?
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

    fun getMusicString() =
        music.joinToString(separator = ",\n") { it.name } // TODO: 20.05.2021 Добавить получение из бд и исполнителя

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

//    fun getMakersString(): String? {
//        val makers = emptyList<People>().toMutableList()
//        initMakers(makers)
//
//        return if (makers.isEmpty())
//            null
//        else
//            peoplesToString(makers)
//    }
//
//    fun getActorsString(): String? {
//        val actors = mutableListOf<People>()
//        initActors(actors)
//
//        return if (actors.isEmpty())
//            null
//        else
//            peoplesToString(actors)
//    }
//
//    fun getGenreString() = genres.joinToString(separator = ", ")
//
//    fun getMusicString(): String {
//        val sb = StringBuilder()
//
//        music.forEach {
//            sb.append(it.name)
//            if (music.indexOf(it) !== music.size - 1)
//                sb.append("\n")
//        }
//        return sb.toString()
//    }
//
//    private fun peoplesToString(peoples: List<People>): String {
//        val sb = StringBuilder()
//
//        peoples.forEach {
//            sb.append(it.getFullName())
//            if (peoples.indexOf(it) !== peoples.size - 1)
//                sb.append("\n")
//        }
//        return sb.toString()
//    }
}

class FilmSeries(
    val id: Int,
    val name: String,
    val description: String
)