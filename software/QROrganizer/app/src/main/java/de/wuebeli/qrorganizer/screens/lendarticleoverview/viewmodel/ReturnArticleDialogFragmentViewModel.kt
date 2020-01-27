package de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import de.wuebeli.qrorganizer.util.UploadCallback
import kotlinx.coroutines.launch

class ReturnArticleDialogFragmentViewModel(application: Application) : BaseViewModel(application) {

    val articleId = MutableLiveData<String>()
    val articleLendingId = MutableLiveData<String>()
    val articleLendingAmount = MutableLiveData<Int>()
    val articleLendingIsWearPart = MutableLiveData<Boolean>()

    fun onReturnArticle(navController: NavController, view: View){
        Log.d("onReturnArticle", "Attempt to return article: ${articleId.value.toString()} with lending id: ${articleLendingId.value.toString()} amount: ${articleLendingAmount.value.toString()}")

        // check if ArticleLending is wear part
        articleLendingIsWearPart.value?.let {
            if (it){
                // remove selected LendArticle and do not put lendingAmount back to stock
                // launch coroutine from background thread to return selected article to stock in MongoDB
                launch {
                    MongoDBStitchManager.removeArticleFromLendingList(articleLendingId.value.toString(), articleLendingAmount.value!!.toInt(), object : UploadCallback.UploadCallbackInterface{
                        override fun onError() {
                            Toast.makeText(
                                view.context,
                                "Upload failed, try reload and check connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        override fun onFinish() {
                            // clear backstack to make sure the user navigates from datasetfragment back to startfragment
                            navController.popBackStack(R.id.datasetFragment, true)
                            navController.navigate(R.id.datasetFragment)
                        }
                    })
                }

            }else{
                // return selected LendArticle to stock -> Increase stock of Article bei lendingAmount
                // launch coroutine from background thread to return selected article to stock in MongoDB
                launch {
                    MongoDBStitchManager.returnArticle(articleLendingId.value.toString(), articleLendingAmount.value!!.toInt(), object : UploadCallback.UploadCallbackInterface{
                        override fun onError() {
                            Toast.makeText(
                                view.context,
                                "Upload failed, try reload and check connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        override fun onFinish() {
                            // clear backstack to make sure the user navigates from datasetfragment back to startfragment
                            navController.popBackStack(R.id.datasetFragment, true)
                            navController.navigate(R.id.datasetFragment)
                        }
                    })
                }
            }
        }
    }
}
