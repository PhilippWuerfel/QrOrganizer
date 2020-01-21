package de.wuebeli.qrorganizer.screens.lendingform.viewmodel

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import de.wuebeli.qrorganizer.model.ArticleLending
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.screens.lendingform.view.LendingFormFragmentDirections
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import de.wuebeli.qrorganizer.util.UploadCallback
import kotlinx.android.synthetic.main.fragment_lending_form.view.*
import kotlinx.coroutines.launch
import java.util.*

class LendingFormViewModel(application: Application) : BaseViewModel(application) {

    val articleId = MutableLiveData<String>()

    val lending_who = MutableLiveData<String>()
    val lending_amount = MutableLiveData<String>() // due to two way binding works better on string, will be converted before creation of ArticleLending-Object
    val lending_comment = MutableLiveData<String>()
    val lending_return_date = MutableLiveData<String>() // due to two way binding works better on string, will be converted before creation of ArticleLending-Object
//    var lending_is_wear_part = MutableLiveData<Int>()

    fun onLendArticle(view : View){

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
            returnDate,
            view.checkBox_IsWearPart.isChecked
        )

        if(myLending.lending_is_wear_part){
            // launch coroutine from background thread to upload LendingForm to MongoDB
            launch {
                MongoDBStitchManager.takeArticleForever(articleId.value!!.toString(), myLending, object : UploadCallback.UploadCallbackInterface{
                    override fun onError() {
                        Toast.makeText(view.context,"Upload failed, check form and try again", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFinish() {
                        val action = LendingFormFragmentDirections.actionLendingFormFragmentToStartFragment()
                        Navigation.findNavController(view).navigate(action)
                    }
                })
            }
        }else{
            // launch coroutine from background thread to upload LendingForm to MongoDB
            launch {
                MongoDBStitchManager.lendArticle(articleId.value!!.toString(), myLending, object : UploadCallback.UploadCallbackInterface{
                    override fun onError() {
                        Toast.makeText(view.context,"Upload failed, check form and try again", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFinish() {
                        val action = LendingFormFragmentDirections.actionLendingFormFragmentToStartFragment()
                        Navigation.findNavController(view).navigate(action)
                    }
                })
            }

        }

    }
}