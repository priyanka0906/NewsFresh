package com.example.newsfresh.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsfresh.R
import com.example.newsfresh.app.adapter.NewsAdapter
import com.example.newsfresh.app.ui.NewsActivity
import com.example.newsfresh.app.ui.NewsViewModel
import com.example.newsfresh.app.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsfresh.app.util.Resource


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var  rvBreakingNews:RecyclerView
    lateinit var progressBar: ProgressBar

    val TAG = "BreakingNews Fragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
        rvBreakingNews= view.findViewById(R.id.rvBreakingNews)
        progressBar=view.findViewById(R.id.paginationProgressBar)
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {

                putSerializable("article",it)

            }

            findNavController().navigate(
                    R.id.action_breakingNewsFragment_to_articleFragment,bundle
            )

        }
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response->
            when(response)
            {
                is Resource.Success ->{
                    hideProgressBar()

                    response.data?.let{
                        newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/ QUERY_PAGE_SIZE+2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                       if(isLastPage)
                       {
                           rvBreakingNews.setPadding(0,0,0,0)

                       }
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let{
                        message->
                        Log.e(TAG,"An error occured:$message")
                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }
            }

         })

    }

    private fun hideProgressBar()
    {
        progressBar.visibility = View.INVISIBLE
        isLoading = false

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
                   if(newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
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
                    val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

                    val shouldPaginated = isNotLoadingAndNotAtTheLastPage && isAtLastItem && isNotAtBeginning&&
                            isTotalMoreThanVisible && isScrolling
                    if(shouldPaginated)
                    {
                        viewModel.getBreakingNews("ind")
                        isScrolling=false
                    }

                }
            }
    private fun setupRecyclerView()
    {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

}