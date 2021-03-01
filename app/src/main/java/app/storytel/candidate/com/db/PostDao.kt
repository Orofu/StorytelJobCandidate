package app.storytel.candidate.com.db

import androidx.lifecycle.LiveData
import androidx.room.*
import app.storytel.candidate.com.data.Post
import app.storytel.candidate.com.data.PostAndPhoto

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: List<Post>)

    @Transaction
    @Query("SELECT * FROM post_table")
    fun getPostsAndPhotos(): LiveData<List<PostAndPhoto>>
}