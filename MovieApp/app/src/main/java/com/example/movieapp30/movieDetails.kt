package com.example.movieapp30

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.login.CurrentUser
import com.example.movieapp30.model.Movie
import com.example.movieapp30.model.MovieResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class movieDetails : AppCompatActivity() {

    var movieId: Int = 0
    var isLiked = false
    lateinit var movie: Movie
    lateinit var favouriteMovie: List<Movie>
    lateinit var likeButton: Button
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_movie_detail)

        movieId = intent.getIntExtra("movieId", 0)

        swipeRefreshLayout = findViewById(R.id.movie_details_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            getMovie()
        }

        getMovie()
    }

    fun initVariables(){
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
        likeButton = findViewById<Button>(R.id.likeButton)

        title.text = movie.title
        originalTitle.text = movie.original_title
        description.text = movie.overview
        movieRating.text = movie.vote_average.toString()
        movieRatingBar.rating = movie.vote_average!!.toFloat()

        val dateTime = initialFormat.parse(movie?.release_date)
        date.text = dateFormat.format(dateTime)
        voteCount.text = movie?.vote_count.toString()
        //voteAverage.text = movie?.vote_average.toString()
        Glide.with(this)
            .load(movie?.getPosterPath())
            .into(image)

        backButton.setOnClickListener {
            finish()
        }
        
        if(CurrentUser.session_id != "") {
            getFavouritesMovies()

            likeButton.setOnClickListener {
                markAsFavourite()
            }
        }else{
            swipeRefreshLayout.isRefreshing = false
            likeButton.setOnClickListener {
                Toast.makeText( this@movieDetails, "Please, log in!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun markAsFavourite(){
        val body = JsonObject().apply {
            addProperty("media_type", "movie")
            addProperty("media_id", movieId)
            addProperty("favorite", !isLiked)
        }
        RetrofitService.getPostApi().markAsFavourite(CurrentUser.account_id, CurrentUser.api_key, CurrentUser.session_id, body).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d(
                    "markAsFavourite",
                    response.body().toString()
                )
                if (response.isSuccessful) {
                    if(!isLiked)
                        Toast.makeText(
                            this@movieDetails,
                            "Movie was added!",
                            Toast.LENGTH_LONG
                        ).show()
                    else
                        Toast.makeText(
                            this@movieDetails,
                            "Movie was deleted!",
                            Toast.LENGTH_LONG
                        ).show()
                    getFavouritesMovies()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(
                    "CreateGuestSession",
                    t.message
                )
                Toast.makeText(
                    this@movieDetails,
                    "Retry once more!",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    fun getFavouritesMovies(){
        val lang: String = "en-US"
        RetrofitService.getPostApi().getFavouritesMoviesList(CurrentUser.account_id, CurrentUser.api_key, CurrentUser.session_id,1, lang).enqueue(object :
            Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                Log.d(
                    "markAsFavourite",
                    response.body().toString()
                )

                if (response.isSuccessful) {
                    favouriteMovie = response.body()?.results!!
                    for (film in favouriteMovie){
                        if(movieId == film.id){
                            likeButton.setBackgroundColor(Color.rgb(227, 94, 1))
                            isLiked = true
                            swipeRefreshLayout.isRefreshing = false
                            return;
                        }
                    }
                    likeButton.setBackgroundColor(Color.WHITE)
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.d(
                    "CreateGuestSession",
                    t.message
                )
                Toast.makeText(
                    this@movieDetails,
                    "Retry once more!",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }

    fun getMovie(){
        swipeRefreshLayout.isRefreshing = true
        val lang = "en-US"
        RetrofitService.getPostApi().getMovie(movieId, CurrentUser.api_key, lang).enqueue(object :
            Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                Log.d(
                    "markAsFavourite",
                    response.body().toString()
                )

                if (response.isSuccessful) {
                    movie = response.body()!!
                    initVariables()
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                Log.d(
                    "CreateGuestSession",
                    t.message
                )
                Toast.makeText(
                    this@movieDetails,
                    "Retry once more!",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }
}


