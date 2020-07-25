package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment)
        savedInstanceState?:prepare()
    }

    private fun prepare() {
        viewModel.syncData().observe(this, Observer<LoadResult<Boolean>> {
            when(it) {
                is LoadResult.Loading ->
                    navController.navigate(R.id.splashScreen)
                is LoadResult.Success -> {
                    val action: NavDirections = SplashScreenDirections.actionSplashScreenToChartersListScreen()
                    navController.navigate(action);
                }
                is LoadResult.Error -> {
                    Snackbar.make(root_container, it.error.toString(), Snackbar.LENGTH_INDEFINITE).show()
                    Handler().postDelayed(Runnable { finish() }, 3000)
                }
            }
        })
    }
}