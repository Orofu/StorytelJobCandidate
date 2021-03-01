package app.storytel.candidate.com.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_table")
data class Photo(
        @PrimaryKey
        var id: Int = 0,
        var albumId: Int = 0,
        var title: String? = null,
        var url: String? = null,
        var thumbnailUrl: String? = null
)