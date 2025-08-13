package com.example.movietracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movietracker.data.Movie
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val movieViewModel: MovieViewModel by viewModels {
        MovieViewModelFactory((application as MovieTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMovies)
        val adapter = MovieListAdapter { movie ->
            // Handle movie click to edit/view details
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("movieId", movie.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        movieViewModel.allMovies.observe(this) { movies ->
            movies?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAddMovie)
        fab.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        }
    }
}