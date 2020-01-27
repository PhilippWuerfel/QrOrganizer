package de.wuebeli.qrorganizer.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import de.wuebeli.qrorganizer.R

/**
 *   Activity which will be the loading/splash screen on start of the app
 */

class SplashScreen : AppCompatActivity() {
    // set loading time for a few sec
    private val SPLASH_TIME_OUT:Long=1000 // 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))

                // close splash screen activity
                finish()
            }, SPLASH_TIME_OUT
        )
    }
}

