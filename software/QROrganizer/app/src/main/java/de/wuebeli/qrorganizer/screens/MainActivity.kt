package de.wuebeli.qrorganizer.screens

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.screens.dataset.view.DatasetFragment
import de.wuebeli.qrorganizer.screens.lending.view.LendingSelectionFragment
import de.wuebeli.qrorganizer.screens.qrcreate.QrCreateFragment
import de.wuebeli.qrorganizer.util.PERMISSION_CAMERA
import de.wuebeli.qrorganizer.util.PERMISSION_STORAGE
import kotlinx.android.synthetic.main.activity_main.*

/**
 *   Activity which hosts the NavHostFragment and handles permissions used in app
 */

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

    fun checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                AlertDialog.Builder(this)
                    .setTitle("Camera permission")
                    .setMessage("This app requires access to the phone camera")
                    .setPositiveButton("Ask me"){dialogInterface:DialogInterface?, i:Int -> requestCameraPermission()  }
                    .setNegativeButton("No"){dialogInterface:DialogInterface?, i :Int -> notifyFragment(false)  }
                    .show()
            } else{
                requestCameraPermission()
            }

        }else{
            notifyFragment(true)
        }
    }

    private fun requestWriteStoragePermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),PERMISSION_STORAGE)
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
            PERMISSION_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_STORAGE->{
                if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    notifyQrCreateFragment(true)
                }
                else{
                    notifyQrCreateFragment(false)
                }

            }
            PERMISSION_CAMERA->{
                if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    notifyFragment(true)
                }
                else{
                    notifyFragment(false)
                }
            }
        }
    }

    private fun notifyQrCreateFragment(permissionGranted:Boolean){
        val activeFragment=myNavHostFragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is QrCreateFragment) {
            activeFragment.onPermissionResult(permissionGranted)
        }
    }

    private fun notifyFragment(permissionGranted: Boolean){
        val activeFragment=myNavHostFragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DatasetFragment) {
            activeFragment.onPermissionResult(permissionGranted)
        }else if (activeFragment is LendingSelectionFragment){
            activeFragment.onPermissionResult(permissionGranted)
        }

    }
}
