package com.example.newsfresh.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsfresh.R
import com.example.newsfresh.app.adapter.NewsAdapter
import com.example.newsfresh.app.ui.NewsActivity
import com.example.newsfresh.app.ui.NewsViewModel
import com.example.newsfresh.app.util.Constants
import com.example.newsfresh.app.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsfresh.app.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
   lateinit var newsAdapter: NewsAdapter
    lateinit var  rvSearchNews: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var etSearch:EditText
    val TAG = "SearchNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSearchNews= view.findViewById(R.id.rvSearchNews)
        progressBar=view.findViewById(R.id.paginationProgressBar)
        etSearch = view.findViewById(R.id.etSearch)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {

                putSerializable("article",it)

            }

            findNavController().navigate(
                    R.id.action_searchNewsFragment_to_articleFragment,bundle
            )

        }
        var job:Job? = null
        etSearch.addTextChangedListener { editable->
            job?.cancel()
            job = MainScope().launch{
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let{
                    if(editable.toString().isNotEmpty())
                    {
                        viewModel.searchForNews(editable.toString())
                    }
                }


            }

        }

        viewModel = (activity as NewsActivity).viewModel
        viewModel.searchNews.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()

                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/ Constants.QUERY_PAGE_SIZE +2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage)
                        {
                            rvSearchNews.setPadding(0,0,0,0)

                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occurred:$message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })

    }

private fun hideProgressBar()
{
    progressBar.visibility = View.INVISIBLE
    isLoading=false

}
private fun showProgressBar()
{
    progressBar.visibility = View.VISIBLE
    isLoading = true

}
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
            {
                isScrolling=true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotAtTheLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition>=0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginated = isNotLoadingAndNotAtTheLastPage && isAtLastItem && isNotAtBeginning&&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginated)
            {
                viewModel.searchForNews(etSearch.text.toString())
                isScrolling=false
            }

        }
    }
private fun setupRecyclerView()
{
    newsAdapter = NewsAdapter()
    rvSearchNews.apply {
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(activity)
        addOnScrollListener(this@SearchNewsFragment.scrollListener)
    }
}
}