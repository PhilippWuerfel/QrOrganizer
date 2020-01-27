package de.wuebeli.qrorganizer.screens.qrcreate

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.google.zxing.WriterException
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.model.ArticleStorageLocation
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.*
import kotlinx.coroutines.launch
import java.io.*
import java.util.*

class QrCreateViewModel(application: Application) : BaseViewModel(application) {

    // to implement two way data binding on edit text, type needs to be string
    // note: two way binding for different types might be supported soon
    val articleId = MutableLiveData<String>()

    val articleName = MutableLiveData<String>()
    val articlePrice = MutableLiveData<String>()
    val articleRoom = MutableLiveData<String>()
    val articleShelf = MutableLiveData<String>()
    val articleBox = MutableLiveData<String>()
    val shop = MutableLiveData<String>()
    val currentAmount = MutableLiveData<String>()
    val minAmount = MutableLiveData<String>()

    var imageFile = MutableLiveData<File>()

    init {
        articleId.value = ""
        articleName.value = ""
        articlePrice.value = ""
        articleRoom.value = ""
        articleShelf.value = ""
        articleBox.value = ""
        shop.value = ""
        currentAmount.value = ""
        minAmount.value = ""
    }

    fun onCreateArticleQR(context: Context, view : View) {

        articleName.value?.let {
            // regex empty spaces with _
            articleId.value = it.replace(" ", "_") + "_" + UUID.randomUUID().toString()

            try {
                val bitmap = textToImageEncode(articleId.value.toString())
                imageFile.value = onSaveImage(articleId.value.toString(), bitmap,context)  //permission handling for use of external storage
            } catch (e: WriterException) {
                e.printStackTrace()
            }

            // ToDo implement some testing before (currently done in Fragment)
            //  for id: delete empty spaces

            val newArticle = ArticleMaster(
                articleId.value!!.toString(),
                articleName.value!!.toString(),
                "", // can be empty, will be added from MongoDBStitchManager
                Calendar.getInstance().time,
                articlePrice.value!!.toDouble(),
                ArticleStorageLocation(articleRoom.value!!.toString(), articleBox.value!!.toString(), articleShelf.value!!.toString()),
                currentAmount.value!!.toInt(),
                minAmount.value!!.toInt(),
                0, // on first creation lending amount will be 0
                shop.value!!.toString()
            )

            // launch coroutine from background thread to create new article in MongoDB
            launch {
                MongoDBStitchManager.createArticle(newArticle, object : UploadCallback.UploadCallbackInterface{
                    override fun onError() {
                        Toast.makeText(context,"Upload failed",Toast.LENGTH_SHORT).show()
                    }
                    override fun onFinish() {
                        imageFile.value?.let{
                            onShare(newArticle.articleId,it,context)
                        }
                        // clear backstack to make sure the user cannot move back to qr create form via back button
                        val navController = Navigation.findNavController(view)
                        navController.popBackStack(R.id.startFragment, true)
                        navController.navigate(R.id.startFragment)
                    }
                })
            }

        }
    }
}