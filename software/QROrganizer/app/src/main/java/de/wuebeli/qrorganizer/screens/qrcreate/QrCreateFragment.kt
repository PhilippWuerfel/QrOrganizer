package de.wuebeli.qrorganizer.screens.qrcreate


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentQrCreateBinding
import de.wuebeli.qrorganizer.screens.MainActivity
import de.wuebeli.qrorganizer.util.textToImageEncode

/**
 * A simple [Fragment] subclass.
 */
class QrCreateFragment : Fragment() {

    private lateinit var viewModel: QrCreateViewModel
    private lateinit var dataBinding: FragmentQrCreateBinding
    private var storageAccessStarted=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

        dataBinding.buttonCreate.setOnClickListener(){onCreateArticleQR()}



        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_qr_create, container, false)
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
            viewModel.onCreateArticleQR(requireContext(), requireView())
        }
    }

    fun onGrantPermission(){
        storageAccessStarted=true
        (activity as MainActivity).checkStoragePermission()
    }


    fun onPermissionResult(permissionGranted:Boolean){

    }

    fun onShare(){
        val intent= Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT,"Blablabla")
        startActivity(intent)
    }





}
