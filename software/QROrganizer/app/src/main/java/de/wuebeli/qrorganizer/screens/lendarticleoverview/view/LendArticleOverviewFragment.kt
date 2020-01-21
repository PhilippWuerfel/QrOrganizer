package de.wuebeli.qrorganizer.screens.lendarticleoverview.view


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentLendArticleOverviewBinding
import de.wuebeli.qrorganizer.screens.lendarticleoverview.viewmodel.LendArticleOverviewViewModel
import de.wuebeli.qrorganizer.util.textToImageEncode
import kotlinx.android.synthetic.main.fragment_lend_article_overview.*

/**
 *  ToDo
 *    1) return borrowed article back to storage --> update stock of article in dataset
 *     a) show currently borrowed articles
 *     b) select one of the borrowed articles which will be returned
 *        + Who returns the article
 *        + Date of return
 *        + Show storage location
 */


/**
 *  ToDo
 *    1) return borrowed article back to storage --> update stock of article in dataset
 *     a) show currently borrowed articles
 *     b) select one of the borrowed articles which will be returned
 *        + Who returns the article
 *        + Date of return
 *        + Show storage location
 *    2) fill up stock of existing article
 *     a) Scan QR-Code or Select article from database
 *     b) Input amount
 */

class LendArticleOverviewFragment : Fragment() {

    private var articleId = ""

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

        viewModel = ViewModelProviders.of(this).get(LendArticleOverviewViewModel::class.java)

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lend_article_overview,
            container,
            false
        )

        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.let {
            articleId = LendArticleOverviewFragmentArgs.fromBundle(it).articleId
            dataBinding.ivQr.setImageBitmap(textToImageEncode(articleId))
        }

        // load data the very first time view is created
        if(viewModel.eventLoadingFinish.value == null){
            viewModel.refresh(articleId)
        }

        dataBinding.lendArticleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lendArticleListAdapter
        }

        dataBinding.refreshLayoutReturn.setOnRefreshListener {
            dataBinding.lendArticleList.visibility = View.GONE
            dataBinding.listError.visibility = View.GONE
            dataBinding.loadingView.visibility = View.VISIBLE
            viewModel.refresh(articleId)
            dataBinding.refreshLayoutReturn.isRefreshing = false
        }

        dataBinding.floatingActionButtonFillUpStock.setOnClickListener { onFillUpStockClicked() }

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
                }else{
                    dataBinding.loadingView.visibility = View.VISIBLE
                    dataBinding.listError.visibility = View.GONE
                    dataBinding.lendArticleList.visibility = View.GONE
                }
            }
        })
    }

    private fun onFillUpStockClicked(){
        viewModel.onFillUpStock()
    }
}
