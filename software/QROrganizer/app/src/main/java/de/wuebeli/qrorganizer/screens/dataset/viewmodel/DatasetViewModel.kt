package de.wuebeli.qrorganizer.screens.dataset.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.DownloadCallbackArticleList
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import kotlinx.coroutines.launch

class DatasetViewModel(application: Application) : BaseViewModel(application) {

    // implement MutableLiveData encapsulated by LiveData
    private val _articleList = MutableLiveData<List<ArticleMaster>>()
    val articleList: LiveData<List<ArticleMaster>>
        get() = _articleList

    private val _articleListLoadError = MutableLiveData<Boolean>()
    val articleListLoadError: LiveData<Boolean>
        get() = _articleListLoadError

    private val _eventLoadingFinish = MutableLiveData<Boolean>()
    val eventLoadingFinish: LiveData<Boolean>
        get() = _eventLoadingFinish

//    private val _articleItemClicked = MutableLiveData<Boolean>()
//    val articleItemClicked: LiveData<Boolean>
//        get() = _articleItemClicked

//    val articelItemClicked = MutableLiveData<Boolean>()

    fun refresh() {
        fetchArticlesFromDatabase()
    }

    private fun fetchArticlesFromDatabase() {
        // download data from MongoDB
        _eventLoadingFinish.value = false
        // launch coroutine from background thread to receive ArticleList from MongoDB
        launch {
            MongoDBStitchManager.downloadArticleList(object :
                DownloadCallbackArticleList.DownloadCallbackInterface {
                override fun onError() {
                    _articleListLoadError.value = true
                    _eventLoadingFinish.value = true
                }

                override fun onFinish(articleList: List<ArticleMaster>) {
                    val articles = articleList
                    onArticlesRetrieved(articles)
                }
            })
        }
    }

    private fun onArticlesRetrieved(articles: List<ArticleMaster>) {
        _articleList.value = articles
        _articleListLoadError.value = false
        _eventLoadingFinish.value = true
    }
}