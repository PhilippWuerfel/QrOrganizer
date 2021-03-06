package de.wuebeli.qrorganizer.screens.qrcreate


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentQrCreateBinding
import de.wuebeli.qrorganizer.screens.MainActivity
import setOnSingleClickListener

/**
 *   1. Shows a form to create a new article (add new document on MongoDB)
 *
 *   2. Fields to expect user input for lending:
 *          ArticleName
 *          Price
 *          Room
 *          Shelf
 *          Box
 *          Shop
 *          Current amount
 *          Minimum amount
 */

class QrCreateFragment : Fragment() {

    private lateinit var viewModel: QrCreateViewModel
    private lateinit var dataBinding: FragmentQrCreateBinding
    private var storageAccessStarted=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Switch SoftInputMode to avoid appearing keyboard from destroying view layout
        activity!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_qr_create,
            container,
            false
        )

        viewModel = ViewModelProviders.of(this).get(QrCreateViewModel::class.java)

        dataBinding.qrCreateViewModel = viewModel

        dataBinding.lifecycleOwner = this

        dataBinding.executePendingBindings()

        // protect from accident: fast unwanted clicks on button which could lead to crash of app
        dataBinding.buttonCreate
            .setOnSingleClickListener( View.OnClickListener{onCreateArticleQR()})

        // Inflate the layout for this fragment
        return dataBinding.root
    }

    private fun onCreateArticleQR() {
        // Make sure viewModel method onCreateArticleQR is not called with empty editText fields
        if (
            dataBinding.editTextName.text.isNullOrEmpty()
            || dataBinding.editTextPrice.text.isNullOrEmpty()
            || dataBinding.editTextRoom.text.isNullOrEmpty()
            || dataBinding.editTextShelf.text.isNullOrEmpty()
            || dataBinding.editTextBox.text.isNullOrEmpty()
            || dataBinding.editTextShop.text.isNullOrEmpty()
            || dataBinding.editTextCurrentStockAmount.text.isNullOrEmpty()
            || dataBinding.editTextMinAmount.text.isNullOrEmpty()
        ) {
            Toast.makeText(getActivity()?.baseContext, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        } else {
            // ask for storage permission and if granted, call viewModel.onCreateArticleQR()
            storageAccessStarted=true
            (activity as MainActivity).checkStoragePermission()
        }
    }

    fun onPermissionResult(permissionGranted:Boolean){
        if(storageAccessStarted&&permissionGranted) {
            context?.let {
                viewModel.onCreateArticleQR(it, requireView())
            }
        }
    }
}
