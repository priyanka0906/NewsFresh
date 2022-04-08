package com.example.newsfresh.app.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsfresh.app.models.Article
import com.example.newsfresh.app.models.NewsResponse
import com.example.newsfresh.app.repository.NewsRepository
import com.example.newsfresh.app.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
):ViewModel() {

   val breakingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

init{
 getBreakingNews("in")
}
    fun getBreakingNews(countryCode:String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)

        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchForNews(searchQuery:String)=viewModelScope.launch{
        searchNews.postValue(Resource.Loading())
       // Log.i("hi","inside th viewmodel function")
        val response = newsRepository.searchForNews(searchQuery ,searchNewsPage)
        Log.i("viewModel",response.toString())
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>
    {
        if(response.isSuccessful)
        {
            response.body()?.let{
                resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse==null)
                {
                    breakingNewsResponse = resultResponse
                }
                else
                {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticle  = resultResponse.articles
                   oldArticles?.addAll(newArticle)
                }

                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>
    {
        if(response.isSuccessful)
        {
            response.body()?.let{
                resultResponse ->
                searchNewsPage++
                if(searchNewsResponse==null)
                {
                    searchNewsResponse = resultResponse
                }
                else
                {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticle  = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article)=viewModelScope.launch{
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch{
        newsRepository.deleteArticle(article)
    }


}