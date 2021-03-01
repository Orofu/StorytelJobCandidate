package app.storytel.candidate.com.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.storytel.candidate.com.adapters.CommentsAdapter
import app.storytel.candidate.com.R
import app.storytel.candidate.com.data.Comment
import app.storytel.candidate.com.data.Post
import app.storytel.candidate.com.factories.PostsViewModelFactory
import app.storytel.candidate.com.network.Resource
import app.storytel.candidate.com.utils.loadWithAgent
import app.storytel.candidate.com.viewmodels.PostsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

private const val INVALID_ID = -1

/**
 * Shows detailed information about a [Post].
 * This fragment uses [PostsViewModel] and requires that it contains a [PostsViewModel.selectedItem]
 * to work, or else an exception will be thrown.
 */
class PostDetailFragment : Fragment(R.layout.fragment_post_details) {
    private lateinit var progressBar: ProgressBar
    private var commentsData: LiveData<Resource<List<Comment>>>? = null
    private val adapter = CommentsAdapter { composeEmail(it) }
    private var postId = INVALID_ID

    private val postViewModel: PostsViewModel by activityViewModels {
        PostsViewModelFactory(activity?.application!!)
    }

    private val observer = Observer<Resource<List<Comment>>> {
        when (it.status) {
            Resource.Status.SUCCESS -> {
                if (it.data != null) adapter.setComments(it.data.take(3))
                progressBar.visibility = View.GONE
            }

            Resource.Status.ERROR -> {
                if (it.data != null) adapter.setComments(it.data.take(3))
                progressBar.visibility = View.GONE
                showErrorDialog()
            }

            Resource.Status.LOADING -> {
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        val postsWithPhotos = requireNotNull(postViewModel.selectedItem)

        progressBar = activity.findViewById(R.id.comments_progressBar)

        val imageView = activity.findViewById<ImageView>(R.id.posts_details_imageView)
        val body = activity.findViewById<TextView>(R.id.posts_details_body)

        activity.title = postsWithPhotos.post?.title
        body.text = postsWithPhotos.post?.body

        val imageLoader: RequestManager = Glide.with(this).apply {
            setDefaultRequestOptions(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
        }
        imageLoader.loadWithAgent(postsWithPhotos.photo?.url)
                .error(R.drawable.ic_generic_error)
                .into(imageView)

        val recyclerView: RecyclerView = activity.findViewById(R.id.posts_details_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        ViewCompat.setNestedScrollingEnabled(recyclerView, false)

        postsWithPhotos.post?.id?.let { postId ->
            this.postId = postId
            observeComments(postId)
        }
    }

    private fun showErrorDialog() {
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.network_error_generic_title)
                .setMessage(R.string.network_error_generic_message)
                .setPositiveButton(R.string.network_error_retry) { dialog, _ ->
                    refresh()
                    dialog.dismiss()
                }.setNeutralButton(R.string.network_error_cancel) { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    private fun observeComments(postId: Int) {
        commentsData?.removeObservers(viewLifecycleOwner)
        if (postId == INVALID_ID) return
        commentsData = postViewModel.getCommentsFromPost(postId)
        commentsData?.observe(viewLifecycleOwner, observer)
    }

    private fun refresh() {
        progressBar.visibility = View.VISIBLE
        observeComments(postId)
    }

    private fun composeEmail(address: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address)) // We need to send an array since
        // multiple addresses can be sent to.
        startActivity(intent)
    }
}