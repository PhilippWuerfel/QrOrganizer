package de.wuebeli.qrorganizer.screens.lendingform.view


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
import de.wuebeli.qrorganizer.databinding.FragmentLendingFormBinding
import de.wuebeli.qrorganizer.screens.lendingform.viewmodel.LendingFormViewModel
import kotlinx.android.synthetic.main.fragment_lending_form.*
import setOnSingleClickListener


/**
 *   1. Shows a form to lend/borrow the selected article (add an entry on MongoDB in lending array)
 *
 *   2. Displays the ID of selected article
 *
 *   3. Fields to expect user input for lending:
 *          Name of the borrower
 *          Current lending amount (optional)
 *          Wear part (true or false via check box)
 *          Return Date (selected via datepicker) *
 */

class LendingFormFragment : Fragment() {

    private lateinit var viewModel: LendingFormViewModel
    private lateinit var dataBinding: FragmentLendingFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Switch SoftInputMode to avoid appearing keyboard from destroying view layout
        activity!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lending_form,
            container,
            false
        )

        viewModel = ViewModelProviders.of(this).get(LendingFormViewModel::class.java)

        dataBinding.lendingFormViewModel = viewModel

        dataBinding.lifecycleOwner = this

        // protect from accident: fast unwanted clicks on button which could lead to crash of app
        dataBinding.buttonLendingFinish
            .setOnSingleClickListener(View.OnClickListener { onFinishClicked() })

        // set minDate of datePicker to forbid return dates in past
        dataBinding.datePickerLending.minDate= System.currentTimeMillis()-1000

        // check if datePicker information is hold back in savedInstanceState to assure surving screen rotations
        if (savedInstanceState != null){

            // set minDate of datePicker to forbid return dates in past
            dataBinding.datePickerLending.minDate= System.currentTimeMillis()-1000

            dataBinding.datePickerLending.updateDate(savedInstanceState.getInt("year"), savedInstanceState.getInt("month"), savedInstanceState.getInt("day"))
        }

        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.let {
            viewModel.articleId.value = LendingFormFragmentArgs.fromBundle(it).articleId
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // save values of datePicker_Lending
        datePicker_Lending?.let {
            outState.putInt("year", datePicker_Lending.year)
            outState.putInt("month", datePicker_Lending.month)
            outState.putInt("day", datePicker_Lending.dayOfMonth)
        }
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
