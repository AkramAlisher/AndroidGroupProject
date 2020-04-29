package com.example.movieapp30.view_model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieapp30.model.*
import com.example.movieapp30.view.RetrofitService
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class MovieViewModel(
    private val context: Context
): ViewModel(), CoroutineScope {

    private val job = Job()
    private val movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
    val movie = MutableLiveData<Movie>()
    val movies = MutableLiveData<List<Movie>>()
    val favouriteMovies = MutableLiveData<List<Movie>>()
    val markAsFavouriteDone = MutableLiveData<Boolean>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getAllMovies() {
        val lang = "en-US"
        launch {
            try {
                val response: Response<MovieResponse> =
                    RetrofitService.getPostApi().getPopularMoviesList(CurrentUser.apiKey, 1, lang)
                if (response.isSuccessful){
                    val result = response.body()?.results
                    if(!result.isNullOrEmpty()) {
                        movies.postValue(result)
                        withContext(Dispatchers.IO) {
                            movieDao?.insertAll(result)
                        }
                    }
                }
            } catch (e: Exception) {
                if (context != null)
                    Toast.makeText(context, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
                withContext(Dispatchers.IO) {
                    movies.postValue(movieDao?.getAll())
                }
            }
        }
    }

    fun getFavouriteMovies() {
        val lang: String = "en-US"
        launch {
            try {
                val response: Response<MovieResponse> = RetrofitService.getPostApi().
                getFavouritesMoviesList(CurrentUser.accountId, CurrentUser.apiKey, CurrentUser.sessionId,1, lang)
                if (response.isSuccessful) {
                    favouriteMovies.postValue(response.body()?.results)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Please, check your internet connection and try again!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun markAsFavourite(movieId: Int, isLiked: Boolean) {
        launch {
            try {
                markAsFavouriteDone.postValue(false)
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", movieId)
                    addProperty("favorite", !isLiked)
                }
                val response: Response<JsonObject> =
                    RetrofitService.getPostApi().markAsFavourite(
                        CurrentUser.accountId, CurrentUser.apiKey, CurrentUser.sessionId, body)
                if (response.isSuccessful) {
                    if(!isLiked)
                        Toast.makeText(this@MovieViewModel.context, "Movie was added!", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(this@MovieViewModel.context,"Movie was deleted!", Toast.LENGTH_LONG).show()
                    markAsFavouriteDone.postValue(true)
                }
            } catch (e: Exception) {
                Toast.makeText(this@MovieViewModel.context, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getMovie(movieId: Int) {
        launch {
            try {
                val lang = "en-US"
                val response: Response<Movie> =
                    RetrofitService.getPostApi().getMovie(movieId, CurrentUser.apiKey, lang)
                if (response.isSuccessful) {
                    movie.postValue(response.body())

                }
            } catch (e: Exception) {
                Toast.makeText(this@MovieViewModel.context, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
                var movies: List<Movie>? = null
                withContext(Dispatchers.IO) {
                    movies = movieDao?.getAll()
                }
                movies.let {
                    if (it != null) {
                        for (film in it) {
                            if (movieId == film.id) {
                                movie.postValue(film)
                            }
                        }
                    }
                }
            }
        }
    }
}