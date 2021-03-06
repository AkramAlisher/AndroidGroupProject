package com.example.movieapp30.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieapp30.MovieListAdapter
import com.example.movieapp30.R
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.login.CurrentUser
import com.example.movieapp30.model.Movie
import com.example.movieapp30.model.MovieDao
import com.example.movieapp30.model.MovieDatabase
import com.example.movieapp30.model.MovieResponse
import kotlinx.coroutines.*
import retrofit2.Response
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class AllFilmsFragment: Fragment(), CoroutineScope {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieListAdapter: MovieListAdapter? = null
    var movies: List<Movie>? = ArrayList()
    private var movieDao: MovieDao? = null

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = LayoutInflater.from(container?.context)
            .inflate(R.layout.fragment_all, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        getAllMovies()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.all_films_refresh)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()

        movieDao = MovieDatabase.getDatabase(context = this@AllFilmsFragment.context).movieDao()

        swipeRefreshLayout.setOnRefreshListener {
            getAllMovies()
        }
    }

    private fun getAllMovies() {
        swipeRefreshLayout.isRefreshing = true
        val lang = "en-US"
        launch {
            try {
                val response: Response<MovieResponse> =
                    RetrofitService.getPostApi().getPopularMoviesList(CurrentUser.apiKey, 1, lang)
                if (response.isSuccessful){
                    val result = response.body()?.results
                    if(!result.isNullOrEmpty()) {
                        withContext(Dispatchers.IO) {
                            movieDao?.insertAll(result)
                        }
                        movies = result
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    movies = movieDao?.getAll() ?: emptyList()
                }
                if (this@AllFilmsFragment.context != null)
                    Toast.makeText(this@AllFilmsFragment.context, "Please, check your internet connection!", Toast.LENGTH_LONG).show()
            } finally {
                movieListAdapter = MovieListAdapter(movies, this@AllFilmsFragment.context)
                recyclerView.adapter = movieListAdapter
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}