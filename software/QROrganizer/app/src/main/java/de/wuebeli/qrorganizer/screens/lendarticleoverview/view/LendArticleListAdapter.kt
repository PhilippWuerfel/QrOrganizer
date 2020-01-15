package de.wuebeli.qrorganizer.screens.lendarticleoverview.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.ItemLendArticleBinding
import de.wuebeli.qrorganizer.model.LendArticle
import kotlinx.android.synthetic.main.item_lend_article.view.*

class LendArticleListAdapter(val lendArticeList: ArrayList<LendArticle>) :
    RecyclerView.Adapter<LendArticleListAdapter.LendArticleViewHolder>(),
    LendArticleClickListener {

     fun updateLendArticleList(newLendArticleList: List<LendArticle>){
         lendArticeList.clear()
         lendArticeList.addAll(newLendArticleList)
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

        // ToDo clickListener
        //  implement option to Return Article here

        val lendArticleId = view.text_lend_article_id.text.toString()
        Toast.makeText(view.context, lendArticleId, Toast.LENGTH_SHORT)
            .show()
    }

    // using databinding
    class LendArticleViewHolder(var dataBinding: ItemLendArticleBinding) :
        RecyclerView.ViewHolder(dataBinding.root)
}