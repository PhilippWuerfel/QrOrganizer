package de.wuebeli.qrorganizer.screens.lendarticleoverview.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.ItemLendArticleBinding
import de.wuebeli.qrorganizer.model.LendArticle
import kotlinx.android.synthetic.main.item_lend_article.view.*

/**
 *   Adapter to have LendArticleList scrollable, items clickable
 *   and efficient in LendArticleOverviewFragment
 */

class LendArticleListAdapter(val lendArticeList: ArrayList<LendArticle>) :
    RecyclerView.Adapter<LendArticleListAdapter.LendArticleViewHolder>(),
    LendArticleClickListener, Filterable {

    private val lendArticleListFull = lendArticeList

    override fun getFilter(): Filter {
        return NewFilter()
    }

    fun updateLendArticleList(newLendArticleList: List<LendArticle>){
         lendArticeList.clear()
         lendArticeList.addAll(newLendArticleList.sortedBy { it.article_lending.lending_return_date })

        // also update lendArticleListFull
        lendArticleListFull.clear()
        lendArticleListFull.addAll(lendArticeList)

        notifyDataSetChanged()


     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LendArticleViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = DataBindingUtil.inflate<ItemLendArticleBinding>(
            inflater,
            R.layout.item_lend_article,
            parent,
            false
        )
        return LendArticleViewHolder(
            dataBinding
        )
    }

    override fun onBindViewHolder(holder: LendArticleViewHolder, position: Int) {
        holder.dataBinding.lendArticle = lendArticeList[position]
        holder.dataBinding.listener = this
    }

    override fun getItemCount(): Int = lendArticeList.size

    override fun onLendArticleClicked(view: View) {
        // Open DialogFragment to return selected/clicked article
        val articleId = view.text_article_id.text.toString()
        val articleLendingId = view.text_article_lending_id.text.toString()
        val articleLendingAmount = view.text_lend_lending_amount.text.toString().toInt()
        val articleLendingIsWearPart = view.text_is_wear_part_bool.text.toString().toBoolean()

        val action = LendArticleOverviewFragmentDirections
            .actionLendArticleOverviewFragmentToReturnArticleDialogFragment(
                articleId,
                articleLendingId,
                articleLendingAmount,
                articleLendingIsWearPart)
        Navigation.findNavController(view).navigate(action)
    }

    // Inner class for filter
    inner class NewFilter : Filter(){
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            var filteredList : ArrayList<LendArticle> = lendArticeList
            filteredList.clear()

            if(charSequence.isNullOrEmpty()){
                filteredList.addAll(lendArticleListFull)
            }else{
//                val filterPattern = charSequence.toString().toLowerCase().trim()
                val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }
                for( lendArticle : LendArticle in lendArticleListFull){
                    if(lendArticle.article_name.toLowerCase().contains(filterPattern)){
                        filteredList.add(lendArticle)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            results.count = filteredList.size

            lendArticeList.clear()
            lendArticeList.addAll(filteredList)

            return  results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    // using databinding
    class LendArticleViewHolder(var dataBinding: ItemLendArticleBinding) :
        RecyclerView.ViewHolder(dataBinding.root)
}