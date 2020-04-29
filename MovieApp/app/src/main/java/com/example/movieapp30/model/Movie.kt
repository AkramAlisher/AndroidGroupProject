package com.example.movieapp30.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movie_table")
data class Movie (
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("vote_count")
    val voteCount: Int? = null,
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("original_title")
    val originalTitle: String? = null,
    @SerializedName("title")
    val title:String? = null,
    @SerializedName("overview")
    val overview: String? = null,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("vote_average")
    val voteAverage: Double? = null

    ) {
    fun getPosterPaths(): String {
        return "https://image.tmdb.org/t/p/w500" + posterPath
    }
}