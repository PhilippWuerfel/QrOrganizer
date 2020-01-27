package de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import de.wuebeli.qrorganizer.util.UploadCallback
import kotlinx.coroutines.launch

class FillUpStockDialogFragmentViewModel(application: Application) : BaseViewModel(application) {

    val fillUpStockAmount = MutableLiveData<String>()
    val articleId = MutableLiveData<String>()

    fun onFillUpStock(navController: NavController, view: View) {
        // launch coroutine from background thread to fill up currentStock with refillAmount to MongoDB
        launch {
            MongoDBStitchManager.fillUpArticleStock(
                articleId.value.toString(),
                fillUpStockAmount.value!!.toInt(),
                object : UploadCallback.UploadCallbackInterface {
                    override fun onError() {
                        Toast.makeText(
                            view.context,
                            "Upload failed, check form and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFinish() {
                        // clear fillUpStockAmount
                        fillUpStockAmount.value = ""

                        // clear backstack to make sure the user navigates from datasetfragment back to startfragment
                        navController.popBackStack(R.id.datasetFragment, true)
                        navController.navigate(R.id.datasetFragment)
                    }
                })
        }
    }
}