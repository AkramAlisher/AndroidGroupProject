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
import com.example.movieapp30.api.PostApi
import com.example.movieapp30.api.RetrofitService
import com.example.movieapp30.login.CurrentUser
import com.example.movieapp30.model.Movie
import com.example.movieapp30.model.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class AllFilmsFragment: Fragment()  {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieListAdapter: MovieListAdapter? = null
    var movies: List<Movie>? = ArrayList()

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
            getMovieList()
        }

        getMovieList()

        return view
    }

    private fun getMovieList() {
        swipeRefreshLayout.isRefreshing = true
        val lang = "en-US"
        val api: PostApi? = RetrofitService.getPostApi()
        api?.getPopularMoviesList(CurrentUser.api_key, 1, lang)?.enqueue(object :
            Callback<MovieResponse> {
            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                if (response.isSuccessful()) {
                    movies = response.body()?.results
                    movieListAdapter = MovieListAdapter(movies, this@AllFilmsFragment.context)
                    recyclerView.adapter = movieListAdapter
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            override fun onFailure(
                call: Call<MovieResponse>,
                t: Throwable
            ) {
                Log.e(AllFilmsFragment::class.java.simpleName, t.toString())
                Toast.makeText(this@AllFilmsFragment.context, "Please, check your connection!", Toast.LENGTH_LONG).show()
                movieListAdapter = MovieListAdapter(movies, this@AllFilmsFragment.context)
                recyclerView.adapter = movieListAdapter
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}