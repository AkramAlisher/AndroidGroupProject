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
import com.example.movieapp30.model.MovieDatabase
import com.example.movieapp30.model.MovieResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class MovieDetails : AppCompatActivity(), CoroutineScope {

    private var movieId: Int = 0
    private var isLiked = false
    private var movie: Movie? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var favouriteMovie: List<Movie>? = null
    private lateinit var likeButton: Button
    private var movieDao = MovieDatabase.getDatabase(context = this).movieDao()

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
        originalTitle.text = (movie?.originalTitle ?: String).toString()
        description.text = (movie?.overview ?: String).toString()
        movieRating.text = movie?.voteAverage.toString()
        movieRatingBar.rating = (movie?.voteAverage?.toFloat() ?: Float) as Float

        val dateTime = initialFormat.parse(movie?.releaseDate)
        date.text = dateFormat.format(dateTime)
        voteCount.text = movie?.voteCount.toString()
        Glide.with(this)
            .load(movie?.getPosterPaths())
            .into(image)

        backButton.setOnClickListener {
            finish()
        }

        if (CurrentUser.sessionId != "") {
            getFavouritesMovies()

            likeButton.setOnClickListener {
                markAsFavourite()
            }
        } else {
            swipeRefreshLayout.isRefreshing = false
            likeButton.setOnClickListener {
                Toast.makeText(this@MovieDetails, "Please, log in!", Toast.LENGTH_LONG).show()
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
                if (response.isSuccessful) {
                    if(!isLiked)
                        Toast.makeText(this@MovieDetails, "Movie was added!", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(this@MovieDetails,"Movie was deleted!", Toast.LENGTH_LONG).show()
                    getFavouritesMovies()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MovieDetails, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
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
                Toast.makeText(this@MovieDetails, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
                var movies: List<Movie>? = null
                withContext(Dispatchers.IO) {
                    movies = movieDao?.getAll()
                }
                movies.let {
                    if (it != null) {
                        for (film in it) {
                            if (movieId == film.id) {
                                movie = film
                                initVariables()
                            }
                        }
                    }
                }
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
                Toast.makeText(this@MovieDetails, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}


