package de.wuebeli.qrorganizer.screens.dataset.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer

import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.FragmentDatasetBinding
import de.wuebeli.qrorganizer.screens.dataset.viewmodel.DatasetViewModel

/** ToDo
 *   1) Aktuelle Artikelliste
 *   2) Aktueller Bestand
 *   3) Lagerplatz
 *   4) Verliehenen Artikel (Wer, bis wann und Menge)
 *   5) Entnahme im Zeitraum x evtl. mit den verknüpften Kosten
 */
class DatasetFragment : Fragment() {

    private lateinit var viewModel: DatasetViewModel

    private lateinit var dataBinding: FragmentDatasetBinding

    private val articleListAdapter =
        ArticleListDatasetAdapter(
            arrayListOf()
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(DatasetViewModel::class.java)

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dataset,
            container,
            false
        )

        // load data the very first time view is created
        if(viewModel.eventLoadingFinish.value == null){
            viewModel.refresh()
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
            viewModel.refresh()
            dataBinding.refreshLayoutDataset.isRefreshing = false
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        // Update the Layout with new Data from ReturnViewModel
        viewModel.articleList.observe(this, Observer { newArticleList ->
            newArticleList?.let {
                dataBinding.articleList.visibility = View.VISIBLE
                articleListAdapter.updateArticleList(newArticleList)
            }
        })
        // Check if ReturnViewModel lendArticleListLoadError is true --> Show Error-Msg
        viewModel.articleListLoadError.observe(this, Observer { isError ->
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
                    dataBinding.articleList.visibility = View.GONE
                }
            }
        })

        // test observing clicks
//        viewModel.articelItemClicked.observe(this, Observer { onClick ->
//            onClick?.let {
//                dataBinding.
//            }
//        })
    }

}