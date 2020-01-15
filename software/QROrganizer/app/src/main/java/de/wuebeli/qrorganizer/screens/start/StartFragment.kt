package de.wuebeli.qrorganizer.screens.start


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentStartBinding

/**
 * ToDo
 *  create class for master data (Artikelstammdaten)
 *  If values needs to be passed: use safe args (already implemented in build.gradle)
 */

class StartFragment : Fragment() {

    private lateinit var viewModel : StartViewModel

    private lateinit var  dataBinding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_start,
            container,
            false
        )

        viewModel = ViewModelProviders.of(this).get(StartViewModel::class.java)

        dataBinding.buttonStartAdd.setOnClickListener {
            val action =
                StartFragmentDirections.actionStartFragmentToQrCreateFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }
        dataBinding.buttonStartBorrow.setOnClickListener {
            val action =
                StartFragmentDirections.actionStartFragmentToLendingSelectionFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }
        dataBinding.buttonStartShowDataset.setOnClickListener {
            val action =
                StartFragmentDirections.actionStartFragmentToDatasetFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }

        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_start, container, false)
        return dataBinding.root
    }
}
