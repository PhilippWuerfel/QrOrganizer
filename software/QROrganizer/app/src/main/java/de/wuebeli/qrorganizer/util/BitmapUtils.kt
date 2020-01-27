package de.wuebeli.qrorganizer.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception


// create bitmap out of string (used for QR Code generation)
fun textToImageEncode(Value: String): Bitmap? {
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

// used to save generated QR Code on device
fun onSaveImage(fileName: String, myBitmap: Bitmap?,context: Context): File {

    val storageDirectory=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val imageFile= File(storageDirectory,"$fileName.jpg")
    try {
        val stream: OutputStream = FileOutputStream(imageFile)
        myBitmap?.compress(Bitmap.CompressFormat.JPEG,90,stream)
        stream.flush()
        stream.close()

        Log.d("onSaveImage", "IMAGE SAVED ${Uri.parse(imageFile.absolutePath)}")

        return imageFile
    }

    catch (e: Exception){
        e.printStackTrace()
    }
    return imageFile
}

// used to share generated QR Code via E-Mail, WhatsApp, etc. ...
fun onShare(fileName: String, file: File, context: Context){
    // File provider to manage availability (temp. permissions) for external apps
    // of the file which will be shared
    val fileUri = FileProvider.getUriForFile(context, "de.wuebeli.qrorganizer.fileprovider", file)

    // start shareIntent SEND
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        // fileName of article as Subject in E-Mail if user chooses to share via E-Mail
        putExtra(Intent.EXTRA_SUBJECT, fileName)
        putExtra(Intent.EXTRA_STREAM, fileUri)
        type = "image/*"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
}