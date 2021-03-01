package app.storytel.candidate.com.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment_table")
data class Comment(
        @PrimaryKey
        var id: Int = 0,
        var postId: Int = 0,
        var name: String? = null,
        var email: String? = null,
        var body: String? = null,
        var timeStamp: Long = System.currentTimeMillis()
)