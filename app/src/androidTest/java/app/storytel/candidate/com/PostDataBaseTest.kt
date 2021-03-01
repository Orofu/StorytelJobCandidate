package app.storytel.candidate.com

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.data.Photo
import app.storytel.candidate.com.data.Post
import app.storytel.candidate.com.data.PostAndPhoto
import app.storytel.candidate.com.db.CommentDao
import app.storytel.candidate.com.db.PhotoDao
import app.storytel.candidate.com.db.PostDao
import app.storytel.candidate.com.db.PostDatabase
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Tests that the [PostDatabase] works as expected.
 */
@RunWith(AndroidJUnit4::class)
class PostDataBaseTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()
    private lateinit var postDao: PostDao
    private lateinit var photoDao: PhotoDao
    private lateinit var commentDao: CommentDao
    private lateinit var db: PostDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, PostDatabase::class.java)
                .allowMainThreadQueries().build()
        postDao = db.postDao
        photoDao = db.photoDao
        commentDao = db.commentDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPost() {
        val post = arrayListOf(createPost())
        postDao.insert(post)

        val posts = postDao.getPostsAndPhotos()
        val storedPost = posts.getOrAwaitValue().get(0)
        assertEquals(storedPost.post, post.get(0))
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPostWithPhoto() {
        val post = createPost()
        val photo = createPhoto()
        val postAndPhotoList = arrayListOf(PostAndPhoto(post, photo))
        photoDao.insert(arrayListOf(photo))
        postDao.insert(arrayListOf(post))

        val result = postDao.getPostsAndPhotos().getOrAwaitValue()
        assert(postAndPhotoList.size == result.size)
        assertEquals(postAndPhotoList, result)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPostWithNoPhoto() {
        val post = createPost()
        val photo = createPhoto().apply { id -= 1 }
        val postAndPhotoList = arrayListOf(PostAndPhoto(post, photo))
        photoDao.insert(arrayListOf(photo))
        postDao.insert(arrayListOf(post))

        val result = postDao.getPostsAndPhotos().getOrAwaitValue()
        assert(postAndPhotoList.size == result.size)
        assertNotEquals(postAndPhotoList, result)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPhoto() {
        val photo = arrayListOf(createPhoto())
        photoDao.insert(photo)

        val photos = photoDao.getAllPhotos().getOrAwaitValue()
        assert(photo.size == photos.size)
        assertEquals(photo, photos)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetComment() {
        val comment = createComment()
        val commentList = arrayListOf(comment)
        commentDao.insert(commentList)

        val comments = commentDao.getAllComments(comment.postId)
        val storedComments = comments.getOrAwaitValue()
        assert(commentList.size == storedComments.size)
        assertEquals(storedComments, commentList)
    }

    private fun createPost(): Post {
        return Post().apply {
            userId = 1337
            id = 12
            title = "Storytel"
            body = "Rulez"
        }
    }

    private fun createPhoto(): Photo {
        return Photo().apply {
            id = 12
            albumId = 13
            title = "A title"
            url = "An url"
            thumbnailUrl = "A thumbnail url"
        }
    }

    private fun createComment(): Comment {
        return Comment().apply {
            id = 0
            postId = 12
            name = "A name"
            email = "email@email.com"
            body = "A body"
            timeStamp = 1000L
        }
    }
}