package de.wuebeli.qrorganizer.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

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