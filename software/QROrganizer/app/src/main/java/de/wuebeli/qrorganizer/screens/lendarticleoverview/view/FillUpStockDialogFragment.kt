package de.wuebeli.qrorganizer.screens.lendarticleoverview.view


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentFillUpStockDialogBinding
import de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel.FillUpStockDialogFragmentViewModel
import setOnSingleClickListener

/**
 *   Opens a Dialog to return or remove articles form LendArticleList
 */

class FillUpDialogFragment : DialogFragment() {

    private val viewModel: FillUpStockDialogFragmentViewModel by navGraphViewModels(R.id.navigation)

    private lateinit var dataBinding: FragmentFillUpStockDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // make dialog transparent
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_fill_up_stock_dialog,
            container,
            false
        )

        dataBinding.fillUpStockDialogFragmentViewModel = viewModel

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // receive arguments
        arguments?.let {
            viewModel.articleId.value = FillUpDialogFragmentArgs.fromBundle(it).articleId
        }

        dataBinding.buttonFillUpConfirm.setOnSingleClickListener(View.OnClickListener {
            onFillUpConfirmed() })
    }

    private fun onFillUpConfirmed(){
        // check if fillUpStockAmount is entered correctly (handled here to prevent from navigation)
        if( viewModel.fillUpStockAmount.value.isNullOrEmpty()
            || viewModel.fillUpStockAmount.value.equals("-")){
            Toast.makeText(requireContext(), "No amount entered", Toast.LENGTH_SHORT).show()
        }else{
            viewModel.onFillUpStock(findNavController(), requireView())
        }
    }
}
