package de.wuebeli.qrorganizer.util

import de.wuebeli.qrorganizer.model.ArticleMaster

class DownloadCallbackArticleList (val listener : DownloadCallbackInterface){

    // callback to control download of ArticleList from MongoDB

    interface DownloadCallbackInterface{
        fun onError()

        fun onFinish(articleList: List<ArticleMaster>)
    }
}