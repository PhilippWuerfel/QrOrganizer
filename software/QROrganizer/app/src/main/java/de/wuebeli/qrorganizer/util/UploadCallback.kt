package de.wuebeli.qrorganizer.util

class UploadCallback (val listener : UploadCallbackInterface){

    // callback to check if item exists in MongoDB

    interface UploadCallbackInterface{
        fun onError()

        fun onFinish()
    }
}