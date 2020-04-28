package com.example.movieapp30.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Movie (

    @SerializedName("id")
    val id: Int,
    @SerializedName("vote_count")
    val vote_count: Int? = null,
    @SerializedName("poster_path")
    val poster_path: String? = null,
    @SerializedName("original_title")
    val original_title: String? = null,
    @SerializedName("title")
    val title:String? = null,
    @SerializedName("genre_ids")
    val genre_ids: List<Int>? = null,
    @SerializedName("overview")
    val overview: String? = null,
    @SerializedName("release_date")
    val release_date: String? = null,
    @SerializedName("genres")
    val genres: List<Genre>? = null,
    @SerializedName("vote_average")
    val vote_average: Double? = null

    ) {
    fun getPosterPath(): String {
        return "https://image.tmdb.org/t/p/w500" + poster_path
    }
}