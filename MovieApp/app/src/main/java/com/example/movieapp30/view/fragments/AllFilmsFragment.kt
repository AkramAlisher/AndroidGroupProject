package com.example.movieapp30.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieapp30.view.MovieListAdapter
import com.example.movieapp30.R
import com.example.movieapp30.view_model.MovieViewModel
import com.example.movieapp30.view_model.ViewModelProviderFactory

class AllFilmsFragment: Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieListAdapter: MovieListAdapter? = null
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(container?.context)
            .inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.all_films_refresh)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()

        val viewModelProviderFactory = ViewModelProviderFactory(this@AllFilmsFragment.context)
        movieViewModel = ViewModelProvider(this@AllFilmsFragment, viewModelProviderFactory).get(MovieViewModel::class.java)

        swipeRefreshLayout.setOnRefreshListener {
            getAllMovies()
        }

        getAllMovies()
    }

    private fun getAllMovies() {
        swipeRefreshLayout.isRefreshing = true
        movieViewModel.getAllMovies()
        movieViewModel.movies.observe(viewLifecycleOwner, Observer { movies ->
            movieListAdapter = MovieListAdapter(movies, context)
            recyclerView.adapter = movieListAdapter
            swipeRefreshLayout.isRefreshing = false
        })
    }
}