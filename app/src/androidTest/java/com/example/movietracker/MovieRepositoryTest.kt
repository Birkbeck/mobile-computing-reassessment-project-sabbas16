package com.example.movietracker.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryTest {

    private lateinit var db: MovieDatabase
    private lateinit var dao: MovieDao
    private lateinit var repo: MovieRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.movieDao()
        repo = MovieRepository(dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insert_update_delete_flowThroughRepository() = runTest {
        // Insert
        val m = Movie(title = "The Matrix", category = "Sci-Fi", description = "Neo", rating = 5f)
        repo.insert(m)
        val firstList = repo.allMovies.first()
        val inserted = firstList.first()
        assertEquals("The Matrix", inserted.title)

        // Update
        val updated = inserted.copy(rating = 4.5f)
        repo.update(updated)
        val afterUpdate = repo.getMovie(inserted.id).first()
        assertEquals(4.5f, afterUpdate.rating, 0.0f)

        // Delete
        repo.delete(afterUpdate)
        val afterDelete = repo.allMovies.first()
        assertEquals(0, afterDelete.size)
    }
}
