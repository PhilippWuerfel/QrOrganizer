package de.wuebeli.qrorganizer.screens.lendingform.viewmodel

import android.app.Application
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*

class LendingFormViewModel(application: Application) : BaseViewModel(application) {

    val articleId = MutableLiveData<String>()
    val lendingWho = MutableLiveData<String>()
    val lendingAmount = MutableLiveData<String>() // due to two way binding works better on string, will be converted before creation of ArticleLending-Object
    val lendingComment = MutableLiveData<String>()

    fun onLendArticle(view: View) {

        /** ToDo
         *   1. Get ArticleID from Scanning QR Code or from Selection in List
         *   2. After Selection or QR Code scan: Pop up Window or navigation to next Fragment
         *   3. Enter Data Who?, Amount?, Return Date? or Flag: Take Forever
         *   4. Build a second method in MongoDBStitchManger: lendArticle( article, takeForEver)
         */

        // assure lendingComment is not null as user does not need to input
        if(lendingComment.value == null){ lendingComment.value = ""}

        // set default value for returnDate (depreciated as new Date-Format not available in API21)
        var returnDate = Date(1970,1,1)

        val datePickerDateString = view.datePicker_Lending.year.toString()+"-"+(view.datePicker_Lending.month+1).toString()+"-"+view.datePicker_Lending.dayOfMonth.toString()
        // lendingReturnDate.value = datePickerDateString
        // try to parse lendingReturnDate
        try{
            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN).parse(datePickerDateString)
            if(parsedDate != null) {
                returnDate = parsedDate
            }
        }catch (e : java.lang.Exception){
            Log.e("LendingFormViewModel", "Error parsing returnDate: " + e.message)
        }

        // determine whether checkBox for isWearPart is true or false
        val isWearPart = view.checkBox_IsWearPart.isChecked

        // build lendingObject
        val myLending = ArticleLending(
            UUID.randomUUID().toString(),
            lendingWho.value!!.toString(),
            lendingAmount.value!!.toInt(),
            lendingComment.value!!.toString(),
            returnDate,
            isWearPart
        )

        if (isWearPart) {
            // launch coroutine from background thread to upload LendingForm to MongoDB
            launch {
                MongoDBStitchManager.takeArticleForever(
                    articleId.value!!.toString(),
                    myLending,
                    object : UploadCallback.UploadCallbackInterface {
                        override fun onError() {
                            Toast.makeText(
                                view.context,
                                "Upload failed, check form and try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFinish() {
                            val action =
                                LendingFormFragmentDirections.actionLendingFormFragmentToStartFragment()
                            Navigation.findNavController(view).navigate(action)
                        }
                    })
            }
        } else {
            // article is not marked as wear part and needs to be returned
                // launch coroutine from background thread to upload LendingForm to MongoDB
                launch {
                    MongoDBStitchManager.lendArticle(
                        articleId.value!!.toString(),
                        myLending,
                        object : UploadCallback.UploadCallbackInterface {
                            override fun onError() {
                                Toast.makeText(
                                    view.context,
                                    "Upload failed, check form and try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onFinish() {
                                val action =
                                    LendingFormFragmentDirections.actionLendingFormFragmentToStartFragment()
                                Navigation.findNavController(view).navigate(action)
                            }
                        })
                }
        }
    }
}