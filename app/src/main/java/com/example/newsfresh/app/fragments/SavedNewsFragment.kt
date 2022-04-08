package com.example.newsfresh.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsfresh.R
import com.example.newsfresh.app.adapter.NewsAdapter
import com.example.newsfresh.app.ui.NewsActivity
import com.example.newsfresh.app.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var  rvSavedNews:RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel

          rvSavedNews = view.findViewById(R.id.rvSavedNews)
          setupRecyclerView()
          newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {

                putSerializable("article",it)

            }

            findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,bundle
            )

        }
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        )
        {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article= newsAdapter.differ.currentList[position]

                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted",Snackbar.LENGTH_LONG).apply{
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }

            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply{
            attachToRecyclerView(rvSavedNews)
        }
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {articles->
        newsAdapter.differ.submitList(articles)

        })

    }

    private fun setupRecyclerView()
    {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}