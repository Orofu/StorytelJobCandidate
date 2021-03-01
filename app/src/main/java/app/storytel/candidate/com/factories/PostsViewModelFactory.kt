package app.storytel.candidate.com.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.storytel.candidate.com.db.PostDatabase
import app.storytel.candidate.com.network.PostsService
import app.storytel.candidate.com.repositories.PostRepository
import app.storytel.candidate.com.utils.createRetrofitInstance
import app.storytel.candidate.com.viewmodels.PostsViewModel

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

/**
 * [ViewModelProvider.Factory] for creating a [PostsViewModel] with its needed dependencies.
 */
class PostsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    private val service = createRetrofitInstance(BASE_URL).create(PostsService::class.java)
    private val database = PostDatabase.getInstance(application)
    private val repository = PostRepository(service, database)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostsViewModel(application, repository) as T
    }
}