package de.wuebeli.qrorganizer.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application) : AndroidViewModel(application), CoroutineScope{
    // AndroidViewModel because application context is required
    // ViewModel would only serve activity context, which is volatile

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

//    override fun onCleared(){
//        super.onCleared()
//        job.cancel()
//    }
}