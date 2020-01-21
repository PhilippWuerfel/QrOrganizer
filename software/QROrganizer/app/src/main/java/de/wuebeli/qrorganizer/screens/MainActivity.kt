package de.wuebeli.qrorganizer.screens

import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.screens.qrcreate.QrCreateFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

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

    fun checkStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder(this)
                    .setTitle("Write Storage Permission")
                    .setMessage("This app requires access to write internal storage")
                    .setPositiveButton("Ask me"){dialog: DialogInterface?, i: Int -> requestWriteStoragePermission()  }
                    .setNegativeButton("No"){dialog, i -> notifyQrCreateFragment(false)
                    }
                    .show()
            }else{
                requestWriteStoragePermission()
            }
        }else{
            notifyQrCreateFragment(true)

        }
    }

    private fun requestWriteStoragePermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),234)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            234->{
                if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    notifyQrCreateFragment(true)
                }
                else{
                    notifyQrCreateFragment(false)
                }
            }
        }
    }

    private fun notifyQrCreateFragment(permissionGranted:Boolean){
        val activeFragment=myNavHostFragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is QrCreateFragment) {
            (activeFragment as QrCreateFragment).onPermissionResult(permissionGranted)
        }
    }
}
