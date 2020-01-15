package de.wuebeli.qrorganizer.screens.lendingform.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentLendingFormBinding
import de.wuebeli.qrorganizer.screens.lendingform.viewmodel.LendingFormViewModel

/** ToDo
 *   Eingabedaten am Tablet
 *    a) Welchen Artikel  scannen oder manuelles suchen aus der Artikelliste
 *    b) Bis wann
 *    c) Wer
 */
class LendingFormFragment : Fragment() {

    private lateinit var viewModel: LendingFormViewModel
    private lateinit var dataBinding: FragmentLendingFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lending_form,
            container,
            false
        )

        viewModel = ViewModelProviders.of(this).get(LendingFormViewModel::class.java)

        dataBinding.lendingFormViewModel = viewModel

        dataBinding.lifecycleOwner = this

        dataBinding.buttonLendingFinish.setOnClickListener { onFinishClicked() }

        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.let {
            viewModel.articleId.value = LendingFormFragmentArgs.fromBundle(it).articleId
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onFinishClicked(){
        // ToDo add comments

        if (
            dataBinding.editTextLendingWho.text.isNullOrEmpty()
            || dataBinding.editTextLendingAmount.text.isNullOrEmpty()
        ) {
            Toast.makeText(getActivity()?.baseContext, "Please fill out relevant fields", Toast.LENGTH_SHORT).show()

        } else {
            // ToDo implement depending on status of check box two functions for lendArticle (with and without return date)
            viewModel.onLendArticle(requireView())
        }
    }
}
