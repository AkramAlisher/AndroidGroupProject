package com.example.movieapp30

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.login.CurrentUser
import com.example.movieapp30.model.Movie
import com.example.movieapp30.model.MovieResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class movieDetails : AppCompatActivity(), CoroutineScope {

    var movieId: Int = 0
    var isLiked = false
    var movie: Movie? = null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var favouriteMovie: List<Movie>? = null
    lateinit var likeButton: Button

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_movie_detail)

        swipeRefreshLayout = findViewById(R.id.movie_detail_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            getMovie()
        }

        movieId = intent.getIntExtra("movieId", 0)

        getMovie()
    }

    private fun initVariables() {
        var dateFormat = SimpleDateFormat("MMMM d, YYYY", Locale.ENGLISH)
        var initialFormat = SimpleDateFormat("YY-MM-DD", Locale.ENGLISH)
        var title = findViewById<TextView>(R.id.movie_title)
        var originalTitle = findViewById<TextView>(R.id.original_title)
        var date = findViewById<TextView>(R.id.movie_date)
        var description = findViewById<TextView>(R.id.description)
        var movieRating = findViewById<TextView>(R.id.movie_rating)
        var voteCount = findViewById<TextView>(R.id.vote_count)
        var movieRatingBar = findViewById<RatingBar>(R.id.ratingBar)
        var image = findViewById<ImageView>(R.id.movie_poster)
        var backButton = findViewById<ImageView>(R.id.backButton)
        likeButton = findViewById(R.id.likeButton)

        title.text = (movie?.title ?: String).toString()
        originalTitle.text = (movie?.original_title ?: String).toString()
        description.text = (movie?.overview ?: String).toString()
        movieRating.text = movie?.vote_average.toString()
        movieRatingBar.rating = (movie?.vote_average?.toFloat() ?: Float) as Float

        val dateTime = initialFormat.parse(movie?.release_date)
        date.text = dateFormat.format(dateTime)
        voteCount.text = movie?.vote_count.toString()
        Glide.with(this)
            .load(movie?.getPosterPath())
            .into(image)

        backButton.setOnClickListener {
            finish()
        }

        if(CurrentUser.sessionId != "") {
            getFavouritesMovies()

            likeButton.setOnClickListener {
                markAsFavourite()
            }
        } else {
            swipeRefreshLayout.isRefreshing = false
            likeButton.setOnClickListener {
                Toast.makeText(this@movieDetails, "Please, log in!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun markAsFavourite() {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", movieId)
                    addProperty("favorite", !isLiked)
                }
                val response: Response<JsonObject> =
                    RetrofitService.getPostApi().markAsFavourite(CurrentUser.accountId, CurrentUser.apiKey, CurrentUser.sessionId, body)
                if (response.isSuccessful){
                    if(!isLiked)
                        Toast.makeText(this@movieDetails, "Movie was added!", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(this@movieDetails,"Movie was deleted!", Toast.LENGTH_LONG).show()
                    getFavouritesMovies()
                }
            } catch (e: Exception) {
                Toast.makeText(this@movieDetails, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getMovie() {
        swipeRefreshLayout.isRefreshing = true
        launch {
            try {
                val lang = "en-US"
                val response: Response<Movie> =
                    RetrofitService.getPostApi().getMovie(movieId, CurrentUser.apiKey, lang)
                if (response.isSuccessful) {
                    movie = response.body()
                    initVariables()
                }
            } catch (e: Exception) {
                Toast.makeText(this@movieDetails, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getFavouritesMovies() {
        launch {
            try {
                val lang = "en-US"
                val response: Response<MovieResponse> =
                    RetrofitService.getPostApi().getFavouritesMoviesList(CurrentUser.accountId, CurrentUser.apiKey, CurrentUser.sessionId,1, lang)
                if (response.isSuccessful) {
                    favouriteMovie = response.body()?.results
                    favouriteMovie.let {
                        if (it != null) {
                            for (film in it) {
                                if (movieId == film.id) {
                                    likeButton.setBackgroundColor(Color.rgb(227, 94, 1))
                                    isLiked = true
                                    swipeRefreshLayout.isRefreshing = false
                                    return@launch
                                }
                            }
                        }
                    }
                    likeButton.setBackgroundColor(Color.WHITE)
                    swipeRefreshLayout.isRefreshing = false
                }
            } catch (e: Exception) {
                Toast.makeText(this@movieDetails, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
            }
        }
    }
}


