package de.wuebeli.qrorganizer.screens.qrscan


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.zxing.integration.android.IntentIntegrator

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentQrScanBinding
import kotlinx.android.synthetic.main.fragment_qr_scan.*

/**
 * ToDo
 *  QR Scan will be called out of several Fragments but always returns the ID of the scanned article
 *  use this for reusability (onFinished callback or something like this)
 *  Pattern seems to be always the same:
 *      Scan and than return ID to the Caller Fragment
 */
class QrScanFragment : Fragment() {



    private lateinit var viewModel: QrScanViewModel
    private lateinit var dataBinding: FragmentQrScanBinding
    private lateinit var scanner: IntentIntegrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(QrScanViewModel::class.java)

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_qr_scan,
            container,
            false
        )

        dataBinding.buttonScan.setOnClickListener { onInitializeScan() }

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_qr_scan, container, false)
        return dataBinding.root
    }

    fun onInitializeScan(){
        val scanner= IntentIntegrator.forSupportFragment(this)
        scanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(getActivity()?.baseContext, "Cancelled", Toast.LENGTH_LONG)
                } else {
                    Toast.makeText(getActivity()?.baseContext, "Got QR", Toast.LENGTH_LONG)
                    textView.text=result.contents
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


}
