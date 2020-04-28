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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FavouriteFragment: Fragment()  {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var appBarTitle: TextView
    private var movieListAdapter: MovieListAdapter? = null
    var movies: List<Movie>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = LayoutInflater.from(container?.context)
            .inflate(R.layout.fragment_favorite, container, false)

        appBarTitle = view.findViewById(R.id.appbar_title)
        recyclerView = view.findViewById(R.id.favourite_recycle_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        swipeRefreshLayout = view.findViewById(R.id.favourite_films_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            getFavouritesMovies()
        }

        return view
    }

    fun getFavouritesMovies(){
        swipeRefreshLayout.isRefreshing = true
        val lang: String = "en-US"
        RetrofitService.getPostApi().getFavouritesMoviesList(CurrentUser.account_id, CurrentUser.api_key, CurrentUser.session_id,1, lang).enqueue(object :
            Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                Log.d(
                    "markAsFavourite",
                    response.body().toString()
                )

                if (response.isSuccessful) {
                    movies = response.body()?.results
                    movieListAdapter = MovieListAdapter(movies, context)
                    recyclerView.adapter = movieListAdapter
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.d(
                    "CreateGuestSession",
                    t.message
                )
                Toast.makeText(this@FavouriteFragment.context, "Please, check your connection!", Toast.LENGTH_LONG).show()
                movieListAdapter = MovieListAdapter(movies, context)
                recyclerView.adapter = movieListAdapter
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.e("FavFragment", "onResume")
        if(CurrentUser.session_id != "") {
            appBarTitle.setText("My favourite films")
            getFavouritesMovies()
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
                getFavouritesMovies()
            }else {
                appBarTitle.setText("Please, log in!")
                movieListAdapter?.moviesList = null
                movieListAdapter?.notifyDataSetChanged()
            }
        }
    }
}