package de.wuebeli.qrorganizer.screens.qrcreate

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.model.ArticleStorageLocation
import de.wuebeli.qrorganizer.util.MongoDBStitchManager
import kotlinx.android.synthetic.main.fragment_qr_create.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class QrCreateViewModel : ViewModel() {

    // to implement two way data binding on edit text, type needs to be string
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

    //val imageView = MutableLiveData<ImageView>()


    init {
        articleName.value = ""
        articlePrice.value = ""
        articleRoom.value = ""
        articleShelf.value = ""
        articleBox.value = ""
        shop.value = ""
        currentAmount.value = ""
        minAmount.value = ""
    }

    fun onCreateArticle() {
        // before create: check values and convert types

        // create data for document insertion
        val myArticle = ArticleMaster(
            "Sensor_" + System.currentTimeMillis().toString(),
            "TestArticle",
            "",
            Calendar.getInstance().time,
            articlePrice.value!!.toDouble(),
            ArticleStorageLocation("F01", "7", "328"), //ToDo create class for storage location
            20,
            5,
            0,
            "Amazon"
        )

        MongoDBStitchManager.createArticle(myArticle)
    }


    fun onCreateArticleQR(context: Context?) {
        //val articleId = articleName.value + "_" + System.currentTimeMillis().toString()
        try {
            val bitmap = TextToImageEncode(articleName.value+"_"+System.currentTimeMillis().toString())

            //R.id.iv_qr!!.setImageBitmap(bitmap) // ToDo add this when adapter is working
            val path=onSaveImage(bitmap,context)  //permission handling for use of external storage
            Toast.makeText(context,"Image saved in ->$path ",Toast.LENGTH_SHORT).show()

        } catch (e: WriterException) {
            e.printStackTrace()
        }

        // ToDo implement some testing before (currently done in Fragment)
        //  for id: delete empty spaces

        val newArticle = ArticleMaster(
            articleName.value!!.toString() + "_" + System.currentTimeMillis().toString(),
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

        MongoDBStitchManager.createArticle(newArticle)

    }

    private fun onSaveImage(myBitmap: Bitmap?,context: Context?): String {
        val bytes = ByteArrayOutputStream()
        myBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + "/ArticlesQRCode"
        )

        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            //Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs())
            wallpaperDirectory.mkdirs()
        }

        try {

            val f = File(
                wallpaperDirectory, articleName.value+Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg"
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            //Display to Gallery,
            MediaScannerConnection.scanFile( context,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null)
            fo.close()

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    private fun TextToImageEncode(Value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                Value,
                BarcodeFormat.QR_CODE,
                500, 500, null
            )

        } catch (Illegalargumentexception: IllegalArgumentException) {

            return null
        }

        val bitMatrixWidth = bitMatrix.getWidth()

        val bitMatrixHeight = bitMatrix.getHeight()

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {

                pixels[offset + x] = if (bitMatrix.get(x, y))
                    Color.BLACK
                else
                    Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }


}