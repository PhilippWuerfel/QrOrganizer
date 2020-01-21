package de.wuebeli.qrorganizer.screens.qrcreate

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.google.zxing.WriterException
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.model.ArticleStorageLocation
import de.wuebeli.qrorganizer.screens.BaseViewModel
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import de.wuebeli.qrorganizer.util.UploadCallback
import de.wuebeli.qrorganizer.util.textToImageEncode
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Exception
import java.util.*

class QrCreateViewModel(application: Application) : BaseViewModel(application) {

    // to implement two way data binding on edit text, type needs to be string
    val articleId = MutableLiveData<String>()

    val articleName = MutableLiveData<String>()
    val articlePrice = MutableLiveData<String>()
//    val articlePrice = MutableLiveData<Double>()
//    val articlePriceMaintainer = MutableLiveData<String>()
    val articleRoom = MutableLiveData<String>()
    val articleShelf = MutableLiveData<String>()
    val articleBox = MutableLiveData<String>()
    val shop = MutableLiveData<String>()
    val currentAmount = MutableLiveData<String>()
    val minAmount = MutableLiveData<String>()

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

    fun onCreateArticleQR(context: Context?, view : View) {
        articleId.value = articleName.value + "_" + System.currentTimeMillis().toString()
        try {
            val bitmap = textToImageEncode(articleId.value.toString())

            //R.id.iv_qr!!.setImageBitmap(bitmap) // ToDo add this when adapter is working
            onSaveImage(bitmap,context)  //permission handling for use of external storage
            //Toast.makeText(context,"Image saved in ->$path ",Toast.LENGTH_SHORT).show()

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

        /*

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


         */
        // launch coroutine from background thread to create new article in MongoDB
        launch {
            MongoDBStitchManager.createArticle(newArticle, object : UploadCallback.UploadCallbackInterface{
                override fun onError() {
                    Toast.makeText(context,"Upload failed",Toast.LENGTH_SHORT).show()
                }
                override fun onFinish() {
                    val action = QrCreateFragmentDirections.actionQrCreateFragmentToStartFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            })
        }
    }

    private fun onSaveImage(myBitmap: Bitmap?,context: Context?){
        val externalStorageState=Environment.getExternalStorageState()
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)){
            val storageDirectory=Environment.getExternalStorageDirectory()
            val dir=File(storageDirectory.absolutePath+"/Article_TU_QRCode/")
            dir.mkdir()

            val file=File(dir,articleId.value!!.toString() + ".jpg")
            try {
                val stream:OutputStream=FileOutputStream(file)
                myBitmap?.compress(Bitmap.CompressFormat.JPEG,90,stream)
                stream.flush()
                stream.close()
                Toast.makeText(context,"IMAGE SAVED ${Uri.parse(file.absolutePath)}",Toast.LENGTH_SHORT).show()
            }

            catch (e:Exception){
                e.printStackTrace()
            }

        }else{
            Toast.makeText(context,"NOT SAVED",Toast.LENGTH_SHORT).show()
        }

    }


}