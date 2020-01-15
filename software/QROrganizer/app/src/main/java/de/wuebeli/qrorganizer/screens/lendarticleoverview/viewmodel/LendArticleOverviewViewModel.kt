package de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.wuebeli.qrorganizer.model.LendArticle
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import de.wuebeli.qrorganizer.util.DownloadCallbackLendArticleList
import kotlinx.coroutines.launch

class LendArticleOverviewViewModel(application: Application) : BaseViewModel(application) {

    // implement MutableLiveData encapsulated by LiveData
    private val _lendArticleList = MutableLiveData<List<LendArticle>>()
    val lendArticleList: LiveData<List<LendArticle>>
        get() = _lendArticleList

    private val _lendArticleListLoadError = MutableLiveData<Boolean>()
    val lendArticleListLoadError: LiveData<Boolean>
        get() = _lendArticleListLoadError

    private val _eventLoadingFinish = MutableLiveData<Boolean>()
    val eventLoadingFinish : LiveData<Boolean>
        get() = _eventLoadingFinish

    fun refresh(articleId: String) {
        fetchLendArticlesFromDatabase(articleId)
    }

    private fun fetchLendArticlesFromDatabase(articleId: String) {
        // download data from MongoDB
        _eventLoadingFinish.value = false
        // launch coroutine from background thread to receive lendArticleList from MongoDB
        launch {
            MongoDBStitchManager.downloadLendArticleList(articleId, object :
                DownloadCallbackLendArticleList.DownloadCallbackInterface {
                override fun onError() {
                    _lendArticleListLoadError.value = true
                    _eventLoadingFinish.value = true
                }

                override fun onFinish(lendArticleList: List<LendArticle>) {
                    val lendArticles = lendArticleList
                    onLendArticlesRetrieved(lendArticles)
                }
            })
        }
    }

    private fun onLendArticlesRetrieved(lendArticles: List<LendArticle>) {
        _lendArticleList.value = lendArticles
        _lendArticleListLoadError.value = false
        _eventLoadingFinish.value = true
    }

    fun onFillUpStock(){
        // ToDo
        //  TBD
    }
}