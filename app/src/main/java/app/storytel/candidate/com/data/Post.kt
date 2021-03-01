package app.storytel.candidate.com.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_table")
data class Post(
        @PrimaryKey
        var id: Int = 0,
        var userId: Int = 0,
        var title: String? = null,
        var body: String? = null,
        var timeStamp: Long = System.currentTimeMillis()
)