package de.wuebeli.qrorganizer.screens.lendarticleoverview.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentReturnArticleDialogBinding
import de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel.ReturnArticleDialogFragmentViewModel
import setOnSingleClickListener

/**
 *   Opens a Dialog to fill up stock of selected article
 */

class ReturnArticleDialogFragment : DialogFragment() {

    private val viewModel: ReturnArticleDialogFragmentViewModel by navGraphViewModels(R.id.navigation)

    private lateinit var dataBinding: FragmentReturnArticleDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // make dialog transparent
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_return_article_dialog,
            container,
            false
        )

        dataBinding.returnArticleDialogFragmentViewModel = viewModel

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // receive arguments
        arguments?.let {
            viewModel.articleId.value = ReturnArticleDialogFragmentArgs.fromBundle(it).articleId
            viewModel.articleLendingId.value = ReturnArticleDialogFragmentArgs.fromBundle(it).articleLendingId
            viewModel.articleLendingAmount.value = ReturnArticleDialogFragmentArgs.fromBundle(it).articleLendingAmount
            viewModel.articleLendingIsWearPart.value = ReturnArticleDialogFragmentArgs.fromBundle(it).articleLendingIsWearPart
        }

        viewModel.articleLendingIsWearPart.value?.let {
            if (it){
                dataBinding.textViewQuestion.text = getString(R.string.text_question_remove_article_wear_part)
            }else{
                dataBinding.textViewQuestion.text = getString(R.string.text_question_return_article)
            }
        }

        // protect from accident: fast unwanted clicks on button which could lead to crash of app
        dataBinding.buttonReturnArticleConfirm
            .setOnSingleClickListener( View.OnClickListener { onReturnConfirmed() })
    }

    private fun onReturnConfirmed(){
        viewModel.onReturnArticle(findNavController(), requireView())
    }

}
