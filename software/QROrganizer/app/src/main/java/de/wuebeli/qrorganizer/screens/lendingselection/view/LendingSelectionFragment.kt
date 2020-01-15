package de.wuebeli.qrorganizer.screens.lending.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentLendingSelectionBinding
import de.wuebeli.qrorganizer.screens.lending.viewmodel.LendingSelectionViewModel

class LendingSelectionFragment : Fragment() {

    private lateinit var selectionViewModel : LendingSelectionViewModel

    private  lateinit var  dataBinding: FragmentLendingSelectionBinding

    private val articleListAdapter =
        ArticleListLendingAdapter(
            arrayListOf()
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        selectionViewModel = ViewModelProviders.of(this).get(LendingSelectionViewModel::class.java)

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_lending_selection,
            container,
            false
        )

        // load data the very first time view is created
        if(selectionViewModel.eventLoadingFinish.value == null){
            selectionViewModel.refresh()
        }

        dataBinding.floatingActionButtonQrScan.setOnClickListener { onQrScanClicked() }

        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBinding.articleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articleListAdapter
        }

        dataBinding.refreshLayoutDataset.setOnRefreshListener {
            dataBinding.articleList.visibility = View.GONE
            dataBinding.listError.visibility = View.GONE
            dataBinding.loadingView.visibility = View.VISIBLE
            selectionViewModel.refresh()
            dataBinding.refreshLayoutDataset.isRefreshing = false
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // Update the Layout with new Data from ReturnViewModel
        selectionViewModel.articleList.observe(this, Observer { newArticleList ->
            newArticleList?.let {
                dataBinding.articleList.visibility = View.VISIBLE
                articleListAdapter.updateArticleList(newArticleList)
            }
        })
        // Check if ReturnViewModel lendArticleListLoadError is true --> Show Error-Msg
        selectionViewModel.articleListLoadError.observe(this, Observer { isError ->
            isError?.let {
                dataBinding.listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        // Show Spinner if ListViewModel is still loading
        selectionViewModel.eventLoadingFinish.observe(this, Observer { hasFinished ->
            hasFinished?.let {
                // hide listError and lendArticleList if still loading
                if (it) {
                    dataBinding.loadingView.visibility = View.GONE
                }else{
                    dataBinding.loadingView.visibility = View.VISIBLE
                    dataBinding.listError.visibility = View.GONE
                    dataBinding.articleList.visibility = View.GONE
                }
            }
        })
    }

    private fun onQrScanClicked(){
        val action = LendingSelectionFragmentDirections
            .actionLendingSelectionFragmentToQrScanFragment()
        NavHostFragment.findNavController(this).navigate(action)
    }
}
