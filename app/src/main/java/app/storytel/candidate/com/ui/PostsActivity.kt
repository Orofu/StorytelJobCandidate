package app.storytel.candidate.com.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.storytel.candidate.com.R

/**
 * [AppCompatActivity] for showing all things related to posts.
 */
class PostsActivity : AppCompatActivity(R.layout.activity_posts) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        val navController = getNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun getNavController(): NavController {
        return (supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }
}