package app.storytel.candidate.com.db

import androidx.lifecycle.LiveData
import androidx.room.*
import app.storytel.candidate.com.data.Comment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: List<Comment>)

    @Query("SELECT * from comment_table where postId = :postId ")
    fun getAllComments(postId: Int): LiveData<List<Comment>>
}