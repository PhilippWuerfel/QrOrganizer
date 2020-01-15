package de.wuebeli.qrorganizer.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import de.wuebeli.qrorganizer.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        // deactivated because of Design without Action Bar
        // NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        return navController.navigateUp()
    }
}
