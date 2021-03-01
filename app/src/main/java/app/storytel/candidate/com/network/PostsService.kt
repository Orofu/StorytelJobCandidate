package app.storytel.candidate.com.network

import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.data.Photo
import app.storytel.candidate.com.data.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service for getting all content related to posts.
 */
interface PostsService {

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("photos")
    suspend fun getPhotos(): Response<List<Photo>>

    @GET("posts/{id}/comments")
    suspend fun getCommentsFromPost(@Path("id") id: Int): Response<List<Comment>>
}