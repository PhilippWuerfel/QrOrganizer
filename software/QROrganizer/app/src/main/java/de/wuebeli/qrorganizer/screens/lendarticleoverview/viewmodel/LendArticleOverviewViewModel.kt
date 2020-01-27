package de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.zxing.WriterException
import de.wuebeli.qrorganizer.model.LendArticle
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.*
import kotlinx.coroutines.launch

class LendArticleOverviewViewModel(application: Application) : BaseViewModel(application) {

    val articleId = MutableLiveData<String>()

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

    fun onQrImageLongClicked(context : Context) :  Boolean {
        // share intent on qr image long click
        Log.d("onQrImageLongClicked", "click")
        val articleId = articleId.value
        if(articleId != null){
            try {
                val bitmap = textToImageEncode(articleId)
                val imageFile = onSaveImage(articleId, bitmap, context)  //permission handling for use of external storage
                onShare(articleId,imageFile,context)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }else{
            Log.e("onQrImageLongClicked", "Couldn't implement sharing")
        }

        return true
    }
}