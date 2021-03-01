package app.storytel.candidate.com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.storytel.candidate.com.R
import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.utils.underline

/**
 * An [RecyclerView.Adapter] for [Comment]
 *
 * @param onItemClicked Callback for when an item in the adapter is clicked. The callback contains
 * a string with the mail url of the [Comment].
 */
class CommentsAdapter(
        private val onItemClicked: (String?) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    private val comments: ArrayList<Comment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(itemView) { onItemClicked(comments[it].email) }
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    /**
     * Sets a [List] of [Comment] that this adapter should display and updates the adapter after.
     *
     * @param comments A [List] of [Comment] to display.
     */
    fun setComments(comments: List<Comment>) {
        this.comments.clear()
        this.comments.addAll(comments)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(
            itemView: View,
            onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.comment_item_name)
        private val body: TextView = itemView.findViewById(R.id.comment_item_body)
        private val email: TextView = itemView.findViewById(R.id.comment_item_email_field)

        init {
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(item: Comment) {
            name.text = item.name
            body.text = item.body
            email.underline(item.email)
        }
    }
}