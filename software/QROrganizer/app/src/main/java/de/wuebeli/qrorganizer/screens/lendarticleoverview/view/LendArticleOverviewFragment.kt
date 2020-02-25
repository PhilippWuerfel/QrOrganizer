package de.wuebeli.qrorganizer.screens.lendarticleoverview.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.WriterException

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentLendArticleOverviewBinding
import de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel.LendArticleOverviewViewModel
import de.wuebeli.qrorganizer.util.onSaveImage
import de.wuebeli.qrorganizer.util.textToImageEncode
import setOnSingleClickListener

/**
 *   1. Displays the QR Code Image of selected article
 *   QR Code can be shared by long click on QR Code Image (share E-Mail, WhatsApp, etc.)
 *
 *   2. Contains a button to fill up stock of selected article
 *
 *   3. Shows a list of lend Articles
 *   information:
 *      Name of the borrower
 *      Name of article
 *      Current lending amount
 *      Wear part (true or false)
 *      Return Date
 *
 */


class LendArticleOverviewFragment : Fragment() {

//    private var articleId = ""

    private lateinit var viewModel: LendArticleOverviewViewModel

    private lateinit var dataBinding: FragmentLendArticleOverviewBinding

    private val lendArticleListAdapter =
        LendArticleListAdapter(
            arrayListOf()
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lend_article_overview,
            container,
            false
        )

        viewModel = ViewModelProviders.of(this).get(LendArticleOverviewViewModel::class.java)

        dataBinding.lendArticleOverviewViewModel = viewModel

        dataBinding.lifecycleOwner = this

        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.let {
            viewModel.articleId.value = LendArticleOverviewFragmentArgs.fromBundle(it).articleId
            dataBinding.ivQr.setImageBitmap(textToImageEncode(viewModel.articleId.value.toString()))
        }

        // load data the very first time view is created
        if (viewModel.eventLoadingFinish.value == null) {
            viewModel.refresh(viewModel.articleId.value.toString())
        }

        dataBinding.lendArticleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lendArticleListAdapter
        }

        dataBinding.refreshLayoutReturn.setOnRefreshListener {
            dataBinding.lendArticleList.visibility = View.GONE
            dataBinding.listError.visibility = View.GONE
            dataBinding.loadingView.visibility = View.VISIBLE
            viewModel.refresh(viewModel.articleId.value.toString())
            dataBinding.refreshLayoutReturn.isRefreshing = false
        }

        dataBinding.searchViewArticleList.imeOptions = EditorInfo.IME_ACTION_DONE
        dataBinding.searchViewArticleList.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(charSequence: String?): Boolean {
                lendArticleListAdapter.filter.filter(charSequence.toString())
                return false
            }
        })

        // protect from accident: fast unwanted clicks on button which could lead to crash of app
        dataBinding.floatingActionButtonFillUpStock
            .setOnSingleClickListener(View.OnClickListener { onFillUpStockClicked() })
        dataBinding.ivQr.setOnLongClickListener { onQrImageLongClicked() }

        observeViewModel()

    }

    private fun observeViewModel() {
        // Update the Layout with new Data from ReturnViewModel
        viewModel.lendArticleList.observe(this, Observer { newLendArticleList ->
            newLendArticleList?.let {
                dataBinding.lendArticleList.visibility = View.VISIBLE
                lendArticleListAdapter.updateLendArticleList(newLendArticleList)
            }
        })
        // Check if ReturnViewModel lendArticleListLoadError is true --> Show Error-Msg
        viewModel.lendArticleListLoadError.observe(this, Observer { isError ->
            isError?.let {
                dataBinding.listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // Show Spinner if ListViewModel is still loading
        viewModel.eventLoadingFinish.observe(this, Observer { hasFinished ->
            hasFinished?.let {
                // hide listError and lendArticleList if still loading
                if (it) {
                    dataBinding.loadingView.visibility = View.GONE
                } else {
                    dataBinding.loadingView.visibility = View.VISIBLE
                    dataBinding.listError.visibility = View.GONE
                    dataBinding.lendArticleList.visibility = View.GONE
                }
            }
        })
    }

    private fun onFillUpStockClicked() {
        // navigate to DialogFragment
        val action = LendArticleOverviewFragmentDirections.actionLendArticleOverviewFragmentToFillUpDialogFragment(viewModel.articleId.value.toString())
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun onQrImageLongClicked() :  Boolean {
        // share intent on qr image long click
        Log.d("onQrImageLongClicked", "click")
        context?.let {
            viewModel.onQrImageLongClicked(it)
        }

        return true
    }
}
