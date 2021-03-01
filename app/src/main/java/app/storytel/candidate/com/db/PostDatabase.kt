package app.storytel.candidate.com.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.data.Photo
import app.storytel.candidate.com.data.Post

/**
 * Database for interacting with all content related to posts.
 */
@Database(entities = [Post::class, Photo::class, Comment::class], version = 1)
abstract class PostDatabase : RoomDatabase() {
    abstract val postDao: PostDao
    abstract val photoDao: PhotoDao
    abstract val commentDao: CommentDao

    companion object {
        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getInstance(context: Context): PostDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            // We dont want the database to be connected to an activities context,
                            // since an activity can be destroyed and removed. Therefore it is
                            // important to use the applicationContext.
                            context.applicationContext,
                            PostDatabase::class.java,
                            "post_database")
                            // No need for an advanced fallback strategy now, just destroy and
                            // rebuild if a migration is needed.
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}