package app.storytel.candidate.com.db

import androidx.lifecycle.LiveData
import androidx.room.*
import app.storytel.candidate.com.data.Photo

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: List<Photo>)

    @Query("SELECT * from photo_table")
    fun getAllPhotos() : LiveData<List<Photo>>
}