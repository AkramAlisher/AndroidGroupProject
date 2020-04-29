package com.example.movieapp30

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp30.model.Movie
import java.text.SimpleDateFormat
import java.util.*

class MovieListAdapter(
    var moviesList: List<Movie>? = null,
    var context: Context?,
    var cnt: Int = 0
) : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovieViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.movie_list_content, p0, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moviesList?.size ?: 0
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(moviesList?.get(position))
    }

    inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var dateFormat = SimpleDateFormat("MMMM d, YYYY", Locale.ENGLISH)
        var initialFormat = SimpleDateFormat("YY-MM-DD", Locale.ENGLISH)
        fun bind(movie: Movie?) {
            val title = view.findViewById<TextView>(R.id.movie_title)
            val date = view.findViewById<TextView>(R.id.movie_date)
            val origTitle = view.findViewById<TextView>(R.id.original_title)
            val vote_cnt = view.findViewById<TextView>(R.id.vote_count)
            val rating = view.findViewById<TextView>(R.id.movie_rating)
            val poster = view.findViewById<ImageView>(R.id.movie_poster)
            val id = view.findViewById<TextView>(R.id.movieId)
            val parent = view.findViewById<ConstraintLayout>(R.id.movie_parent)

            id.text = postCnt().toString()
            title.text = movie?.title
            vote_cnt.text = movie?.vote_count.toString()
            origTitle.text = movie?.original_title
            rating.text = movie?.vote_average.toString()
            vote_cnt.text = movie?.vote_count.toString()
            val dateTime = initialFormat.parse(movie?.release_date)
            date.text = dateFormat.format(dateTime)

            Glide.with(view.context)
                .load(movie?.getPosterPath())
                .into(poster)

            parent.setOnClickListener {
                val intent = Intent(it.context, movieDetails::class.java)
                intent.putExtra("movieId", movie?.id)
                context?.startActivity(intent)
            }
        }

        fun postCnt(): Int {
            cnt = cnt + 1
            return cnt
        }
    }
}

