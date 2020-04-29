package com.example.movieapp30.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieapp30.view.MovieListAdapter
import com.example.movieapp30.R
import com.example.movieapp30.view_model.AuthorizationViewModel
import com.example.movieapp30.view_model.MovieViewModel
import com.example.movieapp30.view_model.ViewModelProviderFactory

class FavouriteFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var appBarTitle: TextView
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel
    private var movieListAdapter: MovieListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(container?.context)
            .inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
    }

    private fun bindViews(view: View) = with(view) {
        appBarTitle = view.findViewById(R.id.appbar_title)
        recyclerView = view.findViewById(R.id.favourite_recycle_view)
        swipeRefreshLayout = view.findViewById(R.id.favourite_films_refresh)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val viewModelProviderFactory = ViewModelProviderFactory(this@FavouriteFragment.context)
        movieViewModel = ViewModelProvider(this@FavouriteFragment, viewModelProviderFactory).get(
            MovieViewModel::class.java)
        authorizationViewModel = ViewModelProvider(this@FavouriteFragment, viewModelProviderFactory).get(
            AuthorizationViewModel::class.java)
        swipeRefreshLayout.setOnRefreshListener {
            getFavouritesMovies()
        }

        getFavouritesMovies()
    }

    private fun getFavouritesMovies() {
        swipeRefreshLayout.isRefreshing = true
        movieViewModel.getFavouriteMovies()
        movieViewModel.favouriteMovies.observe(viewLifecycleOwner, Observer { movies ->
            movieListAdapter = MovieListAdapter(movies, context)
            recyclerView.adapter = movieListAdapter
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun onResume() {
        super.onResume()
        Log.e("FavFragment", "onResume")
        if (authorizationViewModel.userLoggedIn()) {
            appBarTitle.setText("My favourite films")
            getFavouritesMovies()
        } else {
            appBarTitle.setText("Please, log in!")
            movieListAdapter?.moviesList = null
            movieListAdapter?.notifyDataSetChanged()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if(authorizationViewModel.userLoggedIn()) {
                appBarTitle.setText("My favourite films")
                getFavouritesMovies()
            } else {
                appBarTitle.setText("Please, log in!")
                movieListAdapter?.moviesList = null
                movieListAdapter?.notifyDataSetChanged()
            }
        }
    }
}