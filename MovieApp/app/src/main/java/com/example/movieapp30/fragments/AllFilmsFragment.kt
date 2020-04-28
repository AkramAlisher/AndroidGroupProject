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
import com.example.movieapp30.MainActivity
import com.example.movieapp30.MovieListAdapter
import com.example.movieapp30.R
import com.example.movieapp30.api.PostApi
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.login.CurrentUser
import com.example.movieapp30.model.Movie
import com.example.movieapp30.model.MovieResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class AllFilmsFragment: Fragment(), CoroutineScope {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieListAdapter: MovieListAdapter? = null
    var movies: List<Movie>? = ArrayList()

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

        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.all_films_refresh)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()

        swipeRefreshLayout.setOnRefreshListener {
            getAllMoviesCoroutine()
        }

        getAllMoviesCoroutine()

        return view
    }

    private fun getAllMoviesCoroutine(){
        swipeRefreshLayout.isRefreshing = true
        val lang = "en-US"
        launch {
            try {
                val response: Response<MovieResponse> =
                    RetrofitService.getPostApi().getPopularMoviesList(CurrentUser.api_key, 1, lang)
                if (response.isSuccessful){
                    movies = response.body()?.results
                }
            } catch (e: Exception){
                Toast.makeText(this@AllFilmsFragment.context, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            } finally {
                movieListAdapter = MovieListAdapter(movies, this@AllFilmsFragment.context)
                recyclerView.adapter = movieListAdapter
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("AllFilmsFragment", "onDestroy")
        job.cancel()
    }
}