package de.wuebeli.qrorganizer.screens.lending.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.zxing.integration.android.IntentIntegrator

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentLendingSelectionBinding
import de.wuebeli.qrorganizer.screens.MainActivity
import de.wuebeli.qrorganizer.screens.lending.viewmodel.LendingSelectionViewModel
import de.wuebeli.qrorganizer.util.cameraAccessStarted

/**
 *   Shows list of existing articles including details like amount and order destination
 *   Items are clickable (ArticleListLendingAdapter)
 *   click will navigate to LendingForm
 */


class LendingSelectionFragment : Fragment() {

    private lateinit var selectionViewModel : LendingSelectionViewModel

    private  lateinit var  dataBinding: FragmentLendingSelectionBinding

    private lateinit var scanner: IntentIntegrator

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

        dataBinding.floatingActionButtonQrScan.setOnClickListener { onQrScanClicked() }

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
        cameraAccessStarted=true
        (activity as MainActivity).checkCameraPermission()
    }

    fun onPermissionResult(permissionGranted:Boolean){
        if (cameraAccessStarted &&permissionGranted) {
            context?.let {
                scanner = IntentIntegrator.forSupportFragment(this)
                scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                scanner.initiateScan()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(getActivity()?.baseContext, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    val action = LendingSelectionFragmentDirections.actionLendingSelectionFragmentToLendingFormFragment(result.contents)
                    Navigation.findNavController(this.requireView()).navigate(action)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
