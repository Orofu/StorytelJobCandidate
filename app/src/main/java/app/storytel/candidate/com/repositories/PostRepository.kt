package app.storytel.candidate.com.repositories

import androidx.lifecycle.LiveData
import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.data.PostAndPhoto
import app.storytel.candidate.com.db.CommentDao
import app.storytel.candidate.com.db.PhotoDao
import app.storytel.candidate.com.db.PostDao
import app.storytel.candidate.com.db.PostDatabase
import app.storytel.candidate.com.network.PostsService
import app.storytel.candidate.com.network.Resource
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * Indicates how many days to wait before data should be fetched again.
 */
private const val TIME_TO_REFRESH = 1 // Days

/**
 * Repository for getting content related to posts.
 */
class PostRepository(private val postsService: PostsService, database: PostDatabase) {
    private var postDao: PostDao = database.postDao
    private var photoDao: PhotoDao = database.photoDao
    private var commentDao: CommentDao = database.commentDao

    /**
     * Gets a [LiveData] containing a [List] of [Comment] from posts based on the given [id].
     *
     * @param id The id of the post that the [Comment] should come from.
     */
    fun getCommentsFromPost(id: Int): LiveData<Resource<List<Comment>>> =
            object : NetworkBoundResource<List<Comment>, List<Comment>>() {
                override fun saveCallResult(item: List<Comment>) {
                    commentDao.insert(item)
                }

                override fun shouldFetch(data: List<Comment>?): Boolean {
                    if (data == null || data.isEmpty()) return true
                    val currentTime = System.currentTimeMillis()
                    val result = data.find { shouldRefreshData(currentTime, it.timeStamp) }
                    return result != null
                }

                override fun loadFromDb(): LiveData<List<Comment>> = commentDao.getAllComments(id)

                override suspend fun createCall(): Response<List<Comment>> {
                    return postsService.getCommentsFromPost(id)

                }
            }.asLiveData()

    /**
     * Gets a [LiveData] containing a [List] of [PostAndPhoto].
     *
     * @param refresh If the data should be refreshed, i.e fetched from the network again.
     */
    fun getAllPostsWithPhoto(refresh: Boolean = false): LiveData<Resource<List<PostAndPhoto>>> =
            object : NetworkBoundResource<List<PostAndPhoto>, List<PostAndPhoto>>() {
                override fun saveCallResult(item: List<PostAndPhoto>) {
                    postDao.insert(item.mapNotNull { it.post })
                    photoDao.insert(item.mapNotNull { it.photo })
                }

                override fun shouldFetch(data: List<PostAndPhoto>?): Boolean {
                    if (refresh || data == null || data.isEmpty()) return true
                    val currentTime = System.currentTimeMillis()
                    val result = data.find {
                        shouldRefreshData(currentTime, it.post?.timeStamp ?: 0)
                    }
                    return result != null
                }

                override fun loadFromDb(): LiveData<List<PostAndPhoto>> =
                        postDao.getPostsAndPhotos()

                override suspend fun createCall(): Response<List<PostAndPhoto>> {
                    val posts = postsService.getPosts()
                    val photos = postsService.getPhotos()

                    if (posts.isSuccessful && photos.isSuccessful) {
                        // Map the post with its id.
                        val map = posts.body()?.associateBy { it.id }

                        // filter out all photos that doesnt match an id from the posts.
                        val filteredPhotos = photos.body()?.filter {
                            map?.containsKey(it.id) ?: false
                        }

                        val result = filteredPhotos?.map { PostAndPhoto(map?.get(it.id), it) }
                        return Response.success(posts.code(), result)
                    }

                    return if (!posts.isSuccessful) {
                        Response.error(posts.code(), posts.errorBody()!!)
                    } else {
                        Response.error(photos.code(), photos.errorBody()!!)
                    }
                }

            }.asLiveData()

    /**
     * Checks if the given params makes up for a refresh of the data.
     *
     * @param currentTime The current time of the check.
     * @param timeStamp The timestamp to compare against.
     */
    private fun shouldRefreshData(currentTime: Long, timeStamp: Long): Boolean {
        return TimeUnit.MILLISECONDS.toDays(currentTime - timeStamp) >= TIME_TO_REFRESH
    }
}