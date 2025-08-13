package com.example.movietracker.data

import kotlinx.coroutines.flow.Flow

open class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    open fun getMovie(id: Int): Flow<Movie> = movieDao.getMovie(id)

    open suspend fun insert(movie: Movie) {
        movieDao.insert(movie)
    }

    open suspend fun update(movie: Movie) {
        movieDao.update(movie)
    }

    open suspend fun delete(movie: Movie) {
        movieDao.delete(movie)
    }
}
