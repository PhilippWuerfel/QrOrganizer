package de.wuebeli.qrorganizer.util

class UploadCallback (val listener : UploadCallbackInterface){

    // callback to check if upload successful in MongoDB

    interface UploadCallbackInterface{
        fun onError()

        fun onFinish()
    }
}