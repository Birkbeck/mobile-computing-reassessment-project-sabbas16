package com.example.movietracker


import android.app.Application
import com.example.movietracker.data.MovieDatabase
import com.example.movietracker.data.MovieRepository

class MovieTrackerApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { MovieDatabase.getDatabase(this) }
    val repository by lazy { MovieRepository(database.movieDao()) }
}