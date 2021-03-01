package app.storytel.candidate.com.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.storytel.candidate.com.adapters.PostsAdapter
import app.storytel.candidate.com.R
import app.storytel.candidate.com.factories.PostsViewModelFactory
import app.storytel.candidate.com.network.Resource
import app.storytel.candidate.com.viewmodels.PostsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Shows all posts and a short summary of them.
 */
class PostsFragment : Fragment(R.layout.fragment_posts) {
    private lateinit var progressBar: ProgressBar

    private val postViewModel: PostsViewModel by activityViewModels {
        PostsViewModelFactory(activity?.application!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return
        progressBar = activity.findViewById(R.id.progressBar)

        val recyclerView: RecyclerView = activity.findViewById(R.id.posts_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        val imageLoader: RequestManager = Glide.with(this).apply {
            setDefaultRequestOptions(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
        }

        val adapter = PostsAdapter(imageLoader) {
            // PostDetailFragment also uses the postViewModel, so we dont need to pass any arguments
            // to it. Just ensure that the model contains the selected item.
            postViewModel.selectItem(it)
            getNavController().navigate(R.id.action_postsFragment_to_postDetailFragment)
        }
        recyclerView.adapter = adapter

        val swipeRefreshLayout: SwipeRefreshLayout = activity.findViewById(R.id.posts_swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener { refresh() }

        postViewModel.getAllPostsAndPhotos().observe(viewLifecycleOwner, {
            swipeRefreshLayout.isRefreshing = false
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data != null) adapter.setPostsWithPhotos(it.data)
                    progressBar.visibility = View.GONE
                }

                Resource.Status.ERROR -> {
                    if (it.data != null) adapter.setPostsWithPhotos(it.data)
                    progressBar.visibility = View.GONE
                    showErrorDialog()
                }

                Resource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun getNavController(): NavController {
        return (requireActivity().supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    private fun showErrorDialog() {
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.network_error_generic_title)
                .setMessage(R.string.network_error_generic_message)
                .setPositiveButton(R.string.network_error_retry) { dialog, which ->
                    refresh()
                    dialog.dismiss()
                }.setNeutralButton(R.string.network_error_cancel) { dialog, which ->
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    private fun refresh() {
        postViewModel.refreshPostAndPhotos()
        progressBar.visibility = View.VISIBLE
    }
}