package de.wuebeli.qrorganizer.util

import de.wuebeli.qrorganizer.model.LendArticle

class DownloadCallbackLendArticleList (val listener : DownloadCallbackInterface){

    // callback to control download of LendArticleList from MongoDB

    interface DownloadCallbackInterface{
        fun onError()

        fun onFinish(lendArticleList: List<LendArticle>)
    }
}