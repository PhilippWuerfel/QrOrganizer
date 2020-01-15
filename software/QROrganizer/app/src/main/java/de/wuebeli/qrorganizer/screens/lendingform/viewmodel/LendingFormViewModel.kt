package de.wuebeli.qrorganizer.screens.lendingform.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.wuebeli.qrorganizer.model.ArticleLending
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.model.ArticleStorageLocation
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import java.util.*

class LendingFormViewModel : ViewModel() {

    val articleId = MutableLiveData<String>()

    val lending_who = MutableLiveData<String>()
    val lending_amount = MutableLiveData<String>() // due to two way binding works better on string, will be converted before creation of ArticleLending-Object
    val lending_comment = MutableLiveData<String>()
    val lending_return_date = MutableLiveData<String>() // due to two way binding works better on string, will be converted before creation of ArticleLending-Object
//    var lending_is_wear_part = MutableLiveData<Int>()

    fun onLendArticle(){

        /** ToDo
         *   1. Get ArticleID from Scanning QR Code or from Selection in List
         *   2. After Selection or QR Code scan: Pop up Window or navigation to next Fragment
         *   3. Enter Data Who?, Amount?, Return Date? or Flag: Take Forever
         *   4. Build a second method in MongoDBStitchManger: lendArticle( article, takeForEver)
         */

        val returnDate = Date(2020, 2, 12) //ToDo check whats happening here
        // ToDo get Date out of string return date from form

        val myLending = ArticleLending(
            System.currentTimeMillis().toString(),
            lending_who.value!!.toString(),
            lending_amount.value!!.toInt(),
            returnDate
        )

        MongoDBStitchManager.lendArticle(articleId.value!!.toString(), myLending)

    }
}