package com.example.newsfresh.app.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsfresh.R
import com.example.newsfresh.app.fragments.ArticleFragmentArgs
import com.example.newsfresh.app.ui.NewsActivity
import com.example.newsfresh.app.ui.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel

    val args: ArticleFragmentArgs by navArgs()
    lateinit var webView: WebView
    lateinit var fab:FloatingActionButton
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        webView = view.findViewById(R.id.webView)
        fab = view.findViewById(R.id.fab)
        val article = args.article
        webView.apply{
          webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }
        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article saved Successfully",Snackbar.LENGTH_SHORT).show()
        }

    }


}