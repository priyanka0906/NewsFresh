package com.example.newsfresh.app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.ui.setupWithNavController
import com.example.newsfresh.R
import com.example.newsfresh.app.db.ArticleDatabase
import com.example.newsfresh.app.repository.NewsRepository

import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val repository= NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

       val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // for bottom navigation to change fragments according to the option selected
//        val navController = this.findNavController(R.id.newsNavHostFragment)
//        bottomNavigationView.setupWithNavController(navController)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }
}