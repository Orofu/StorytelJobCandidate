package app.storytel.candidate.com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.storytel.candidate.com.R
import app.storytel.candidate.com.data.PostAndPhoto
import app.storytel.candidate.com.utils.loadWithAgent
import com.bumptech.glide.RequestManager

/**
 * An [RecyclerView.Adapter] for [PostAndPhoto]
 *
 * @param imageLoader [RequestManager] that should load the images.
 * @param onItemClicked Callback for when an item in the adapter is clicked. The callback contains
 * the [PostAndPhoto] object of the clicked view.
 */
class PostsAdapter(
        private var imageLoader: RequestManager,
        private val onItemClicked: (PostAndPhoto) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    private val posts: ArrayList<PostAndPhoto> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent,
                false)
        return PostsViewHolder(itemView) { onItemClicked(posts[it]) }
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(posts[position], imageLoader)
    }

    override fun getItemCount(): Int = posts.size

    /**
     * Sets a [List] of [PostAndPhoto] that this adapter should display and updates the adapter
     * after.
     *
     * @param posts A [List] of [PostAndPhoto] to display.
     */
    fun setPostsWithPhotos(posts: List<PostAndPhoto>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    inner class PostsViewHolder(
            itemView: View,
            onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.posts_item_title)
        private val body: TextView = itemView.findViewById(R.id.posts_item_body)
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(item: PostAndPhoto, loader: RequestManager) {
            title.text = item.post?.title
            body.text = item.post?.body

            // Our url uses User-Agent as header, so we need to do a workaround to be able to load
            // the images with glide.
            loader.loadWithAgent(item.photo?.thumbnailUrl)
                    .error(R.drawable.ic_generic_error)
                    .into(imageView)
        }
    }
}