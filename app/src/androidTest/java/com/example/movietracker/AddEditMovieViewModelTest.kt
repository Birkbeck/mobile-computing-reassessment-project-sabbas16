package com.example.movietracker

import com.example.movietracker.data.Movie
import com.example.movietracker.data.MovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class FakeMovieRepository : MovieRepositoryLike {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: List<Movie> get() = _movies.value

    override fun getMovie(id: Int): Flow<Movie> =
        _movies.map { list -> list.first { it.id == id } }

    override suspend fun insert(movie: Movie) {
        // simulate Room auto-ID when id == 0
        val newId = if (movie.id == 0) (movies.maxOfOrNull { it.id } ?: 0) + 1 else movie.id
        _movies.update { it + movie.copy(id = newId) }
    }

    override suspend fun update(movie: Movie) {
        _movies.update { list -> list.map { if (it.id == movie.id) movie else it } }
    }

    override suspend fun delete(movie: Movie) {
        _movies.update { list -> list.filterNot { it.id == movie.id } }
    }
}

/**
 * A minimal interface to decouple the ViewModel from the concrete repository in unit tests.
 * In production, MovieRepository already matches this shape.
 */
private interface MovieRepositoryLike {
    fun getMovie(id: Int): Flow<Movie>
    suspend fun insert(movie: Movie)
    suspend fun update(movie: Movie)
    suspend fun delete(movie: Movie)
}

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditMovieViewModelTest {

    private lateinit var fakeRepo: FakeMovieRepository
    private lateinit var vm: AddEditMovieViewModel

    // Adapter class to use our Fake in the ViewModel that expects MovieRepository
    private inner class RepoAdapter : MovieRepository(
        movieDao = throw UnsupportedOperationException("Not used in unit test")
    ) {
        // Delegate to fakeRepo
        override fun getMovie(id: Int): Flow<Movie> = fakeRepo.getMovie(id)
        override suspend fun insert(movie: Movie) = fakeRepo.insert(movie)
        override suspend fun update(movie: Movie) = fakeRepo.update(movie)
        override suspend fun delete(movie: Movie) = fakeRepo.delete(movie)
    }

    @Before
    fun setup() {
        fakeRepo = FakeMovieRepository()
        // Use an adapter that satisfies the MovieRepository type expected by the ViewModel
        vm = (null as? Nothing)?.let {
            AddEditMovieViewModel(object : MovieRepository(it) {
                override fun getMovie(id: Int): Flow<Movie> = fakeRepo.getMovie(id)
                override suspend fun insert(movie: Movie) = fakeRepo.insert(movie)
                override suspend fun update(movie: Movie) = fakeRepo.update(movie)
                override suspend fun delete(movie: Movie) = fakeRepo.delete(movie)
            })
        }!!
    }

    @Test
    fun insert_addsMovie() = runTest {
        val m = Movie(title = "Inception", category = "Sci-Fi", description = "Dreams", rating = 4.5f)
        vm.insert(m)

        assertEquals(1, fakeRepo.movies.size)
        assertEquals("Inception", fakeRepo.movies.first().title)
        // id should be auto-assigned (>0)
        assert(fakeRepo.movies.first().id > 0)
    }

    @Test
    fun update_modifiesMovie() = runTest {
        val m = Movie(title = "Interstellar", category = "Sci-Fi", description = "Space", rating = 4.0f)
        vm.insert(m)
        val inserted = fakeRepo.movies.first()
        val updated = inserted.copy(title = "Interstellar (2014)", rating = 5.0f)

        vm.update(updated)

        val after = fakeRepo.movies.first()
        assertEquals("Interstellar (2014)", after.title)
        assertEquals(5.0f, after.rating)
        assertEquals(inserted.id, after.id) // id unchanged
    }

    @Test
    fun delete_removesMovie() = runTest {
        val m1 = Movie(title = "Movie A", category = "Drama", description = "", rating = 3.0f)
        val m2 = Movie(title = "Movie B", category = "Action", description = "", rating = 4.0f)
        vm.insert(m1)
        vm.insert(m2)

        val toDelete = fakeRepo.movies.first { it.title == "Movie A" }
        vm.delete(toDelete)

        assertEquals(1, fakeRepo.movies.size)
        assertEquals("Movie B", fakeRepo.movies.first().title)
    }
}
