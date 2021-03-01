package app.storytel.candidate.com.data

import androidx.room.Embedded
import androidx.room.Relation

data class PostAndPhoto(
        @Embedded
        val post: Post?,

        @Relation(parentColumn = "id", entityColumn = "id")
        val photo: Photo?
)