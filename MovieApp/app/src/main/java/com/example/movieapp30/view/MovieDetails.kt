package com.example.movieapp30.view

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movieapp30.R
import com.example.movieapp30.model.Movie
import com.example.movieapp30.view_model.AuthorizationViewModel
import com.example.movieapp30.view_model.MovieViewModel
import com.example.movieapp30.view_model.ViewModelProviderFactory
import java.text.SimpleDateFormat
import java.util.*

class MovieDetails : AppCompatActivity() {

    private var movieId: Int = 0
    private var isLiked = false
    private var movie: Movie? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var likeButton: Button
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_movie_detail)

        swipeRefreshLayout = findViewById(R.id.movie_detail_refresh)
        val viewModelProviderFactory = ViewModelProviderFactory(this)
        movieViewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieViewModel::class.java)
        authorizationViewModel = ViewModelProvider(this, viewModelProviderFactory).get(AuthorizationViewModel::class.java)
        movieId = intent.getIntExtra("movieId", 0)

        swipeRefreshLayout.setOnRefreshListener {
            getMovie()
        }

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

        if (authorizationViewModel.userLoggedIn()) {
            checkFavouriteMovie()

            likeButton.setOnClickListener {
                movieViewModel.markAsFavourite(movieId, isLiked)
                movieViewModel.markAsFavouriteDone.observe(this, Observer { done ->
                    if (done)
                        checkFavouriteMovie()
                })
            }
        } else {
            swipeRefreshLayout.isRefreshing = false
            likeButton.setOnClickListener {
                Toast.makeText(this@MovieDetails, "Please, log in!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkFavouriteMovie() {
        swipeRefreshLayout.isRefreshing = true
        var favouriteMovies: List<Movie>? = null
        movieViewModel.getFavouriteMovies()
        movieViewModel.favouriteMovies.observe(this, Observer { movies ->
            favouriteMovies = movies
            favouriteMovies.let {
                if (it != null) {
                    for (film in it) {
                        if (movieId == film.id) {
                            likeButton.setBackgroundColor(Color.rgb(227, 94, 1))
                            isLiked = true
                            swipeRefreshLayout.isRefreshing = false
                            return@Observer
                        }
                    }
                }
            }
            likeButton.setBackgroundColor(Color.WHITE)
            isLiked = false
            swipeRefreshLayout.isRefreshing = false
        })
    }

    private fun getMovie() {
        swipeRefreshLayout.isRefreshing = true
        movieViewModel.getMovie(movieId)
        movieViewModel.movie.observe(this, Observer {
            movie = it
            initVariables()
        })
    }
}


