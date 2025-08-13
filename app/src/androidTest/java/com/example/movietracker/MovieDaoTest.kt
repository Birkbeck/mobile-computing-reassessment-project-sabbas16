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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MovieDaoTest {

    private lateinit var db: MovieDatabase
    private lateinit var dao: MovieDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries() // safe for tests
            .build()
        dao = db.movieDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_and_queryAll() = runTest {
        val movie = Movie(title = "Dune", category = "Sci-Fi", description = "Arrakis", rating = 4.5f)
        dao.insert(movie)

        val all = dao.getAllMovies().first()
        assertEquals(1, all.size)
        assertEquals("Dune", all.first().title)
    }

    @Test
    fun update_changesPersist() = runTest {
        val movie = Movie(title = "Blade Runner", category = "Sci-Fi", description = "Replicants", rating = 4.0f)
        dao.insert(movie)
        val inserted = dao.getAllMovies().first().first()

        val updated = inserted.copy(title = "Blade Runner (Final Cut)", rating = 5.0f)
        dao.update(updated)

        val after = dao.getMovie(inserted.id).first()
        assertEquals("Blade Runner (Final Cut)", after.title)
        assertEquals(5.0f, after.rating)
        assertEquals(inserted.id, after.id)
    }

    @Test
    fun delete_removesFromDb() = runTest {
        val m1 = Movie(title = "A", category = "Drama", description = "", rating = 3f)
        val m2 = Movie(title = "B", category = "Action", description = "", rating = 4f)
        dao.insert(m1)
        dao.insert(m2)

        val listBefore = dao.getAllMovies().first()
        val toDelete = listBefore.first { it.title == "A" }
        dao.delete(toDelete)

        val listAfter = dao.getAllMovies().first()
        assertEquals(1, listAfter.size)
        assertTrue(listAfter.none { it.title == "A" })
    }
}
