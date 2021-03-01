package app.storytel.candidate.com.viewmodels

import android.app.Application
import androidx.lifecycle.*
import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.data.PostAndPhoto
import app.storytel.candidate.com.network.Resource
import app.storytel.candidate.com.repositories.PostRepository

/**
 * [AndroidViewModel] for getting information about posts.
 */
class PostsViewModel(
        application: Application,
        private val repository: PostRepository
) : AndroidViewModel(application) {
    private var isRefreshing = false

    var selectedItem: PostAndPhoto? = null
        private set

    private val reload = MutableLiveData(Unit)
    private val allPostsWithPhoto: LiveData<Resource<List<PostAndPhoto>>> = reload.switchMap {
        val refreshing = isRefreshing
        isRefreshing = false
        repository.getAllPostsWithPhoto(refreshing)
    }

    /**
     * Selects the item.
     */
    fun selectItem(item: PostAndPhoto) {
        selectedItem = item;
    }

    /**
     * Gets a [LiveData] containing a [List] of [PostAndPhoto].
     */
    fun getAllPostsAndPhotos(): LiveData<Resource<List<PostAndPhoto>>> {
        return allPostsWithPhoto
    }

    /**
     * Gets a [LiveData] containing a [List] of [Comment] from posts based on the given [id].
     *
     * @param id The id of the post that the [Comment] should come from.
     */
    fun getCommentsFromPost(id: Int): LiveData<Resource<List<Comment>>> {
        return repository.getCommentsFromPost(id)
    }

    /**
     * Refreshes the [LiveData] associated with [PostAndPhoto] returned from [getAllPostsAndPhotos]
     */
    fun refreshPostAndPhotos() {
        if (isRefreshing) return
        isRefreshing = true
        reload.value = Unit
    }
}