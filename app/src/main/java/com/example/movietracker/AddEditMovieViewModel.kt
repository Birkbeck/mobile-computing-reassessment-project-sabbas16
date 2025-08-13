package com.example.movietracker


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movietracker.data.Movie
import com.example.movietracker.data.MovieRepository
import kotlinx.coroutines.launch

class AddEditMovieViewModel(private val repository: MovieRepository) : ViewModel() {

    fun insert(movie: Movie) = viewModelScope.launch {
        repository.insert(movie)
    }

    fun update(movie: Movie) = viewModelScope.launch {
        repository.update(movie)
    }

    fun delete(movie: Movie) = viewModelScope.launch {
        repository.delete(movie)
    }
}

class AddEditMovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditMovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditMovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}