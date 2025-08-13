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
import com.example.movietracker.data.Movie

class AddMovieActivity : AppCompatActivity() {

    private val addEditViewModel: AddEditMovieViewModel by viewModels {
        AddEditMovieViewModelFactory((application as MovieTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        val editTextTitle: EditText = findViewById(R.id.editTextMovieTitle)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val editTextDescription: EditText = findViewById(R.id.editTextMovieDescription)
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val buttonSave: Button = findViewById(R.id.buttonSave)

        ArrayAdapter.createFromResource(
            this,
            R.array.movie_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val description = editTextDescription.text.toString().trim()
            val rating = ratingBar.rating

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a movie title.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newMovie = Movie(title = title, category = category, description = description, rating = rating)
            addEditViewModel.insert(newMovie)

            Toast.makeText(this, "Movie saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}