package com.example.movieapp30.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.example.movieapp30.model.MovieResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

class FavouriteFragment: Fragment(), CoroutineScope {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var appBarTitle: TextView
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
            .inflate(R.layout.fragment_favorite, container, false)

        appBarTitle = view.findViewById(R.id.appbar_title)
        recyclerView = view.findViewById(R.id.favourite_recycle_view)
        swipeRefreshLayout = view.findViewById(R.id.favourite_films_refresh)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()

        swipeRefreshLayout.setOnRefreshListener {
            getFavouriteMoviesCoroutine()
        }


        return view
    }

    private fun getFavouriteMoviesCoroutine(){
        swipeRefreshLayout.isRefreshing = true
        val lang: String = "en-US"
        launch {
            try {
                val response: Response<MovieResponse> =
                    RetrofitService.getPostApi().getFavouritesMoviesList(CurrentUser.account_id, CurrentUser.api_key, CurrentUser.session_id,1, lang)
                if (response.isSuccessful){
                    movies = response.body()?.results
                }
            } catch (e: Exception){
                Toast.makeText(this@FavouriteFragment.context, "We have problems with the internet!", Toast.LENGTH_LONG).show()
            } finally {
                movieListAdapter = MovieListAdapter(movies, this@FavouriteFragment.context)
                recyclerView.adapter = movieListAdapter
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("FavFragment", "onResume")
        if(CurrentUser.session_id != "") {
            appBarTitle.setText("My favourite films")
            getFavouriteMoviesCoroutine()
        }else{
            appBarTitle.setText("Please, log in!")
            movieListAdapter?.moviesList = null
            movieListAdapter?.notifyDataSetChanged()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){
            if(CurrentUser.session_id != "") {
                appBarTitle.setText("My favourite films")
                getFavouriteMoviesCoroutine()
            }else {
                appBarTitle.setText("Please, log in!")
                movieListAdapter?.moviesList = null
                movieListAdapter?.notifyDataSetChanged()
            }
        }
    }
}