package com.example.movietracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.movietracker.data.Movie
import com.example.movietracker.data.MovieDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MovieDetailsActivity : AppCompatActivity() {

    private val addEditViewModel: AddEditMovieViewModel by viewModels {
        AddEditMovieViewModelFactory((application as MovieTrackerApplication).repository)
    }

    private var currentMovieId: Int = 0
    private lateinit var currentMovie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val editTextTitle: EditText = findViewById(R.id.editTextMovieTitle)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val editTextDescription: EditText = findViewById(R.id.editTextMovieDescription)
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val buttonSave: Button = findViewById(R.id.buttonSave)
        val buttonDelete: Button = findViewById(R.id.buttonDelete)

        ArrayAdapter.createFromResource(
            this,
            R.array.movie_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        // Get the movie ID from the intent
        currentMovieId = intent.getIntExtra("movieId", 0)

        if (currentMovieId != 0) {
            // Load movie data from the database
            lifecycleScope.launch {
                val movieDao = MovieDatabase.getDatabase(this@MovieDetailsActivity).movieDao()
                val movieFlow = movieDao.getMovie(currentMovieId)
                currentMovie = movieFlow.first()

                // Populate the UI with existing movie data
                editTextTitle.setText(currentMovie.title)
                editTextDescription.setText(currentMovie.description)
                ratingBar.rating = currentMovie.rating

                val categoryPosition = (spinnerCategory.adapter as ArrayAdapter<String>).getPosition(currentMovie.category)
                spinnerCategory.setSelection(categoryPosition)
            }

            buttonSave.setOnClickListener {
                val updatedMovie = currentMovie.copy(
                    title = editTextTitle.text.toString().trim(),
                    category = spinnerCategory.selectedItem.toString(),
                    description = editTextDescription.text.toString().trim(),
                    rating = ratingBar.rating
                )
                addEditViewModel.update(updatedMovie)
                Toast.makeText(this, "Movie updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }

            buttonDelete.setOnClickListener {
                addEditViewModel.delete(currentMovie)
                Toast.makeText(this, "Movie deleted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}