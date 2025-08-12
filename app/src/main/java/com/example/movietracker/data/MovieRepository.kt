package com.example.movietracker.data

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    suspend fun insert(movie: Movie) {
        movieDao.insert(movie)
    }

    suspend fun update(movie: Movie) {
        movieDao.update(movie)
    }

    suspend fun delete(movie: Movie) {
        movieDao.delete(movie)
    }
}