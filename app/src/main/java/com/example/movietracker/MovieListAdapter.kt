package com.example.movietracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movietracker.data.Movie

class MovieListAdapter(private val onItemClicked: (Movie) -> Unit) :
    ListAdapter<Movie, MovieListAdapter.MovieViewHolder>(MoviesDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = getItem(position)
        holder.bind(currentMovie, onItemClicked)
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieTitleTextView: TextView = itemView.findViewById(R.id.textViewMovieTitle)
        private val movieCategoryTextView: TextView = itemView.findViewById(R.id.textViewMovieCategory)
        private val movieRatingBar: RatingBar = itemView.findViewById(R.id.ratingBarMovie)

        fun bind(movie: Movie, onItemClicked: (Movie) -> Unit) {
            movieTitleTextView.text = movie.title
            movieCategoryTextView.text = movie.category
            movieRatingBar.rating = movie.rating
            itemView.setOnClickListener { onItemClicked(movie) }
        }
    }

    companion object {
        private val MoviesDiffCallback = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}