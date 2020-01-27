package de.wuebeli.qrorganizer.screens.lending.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.ItemArticleBinding
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.util.ArticleClickListener
import kotlinx.android.synthetic.main.item_article.view.*

/**
 *   Adapter to have ArticleList scrollable, items clickable
 *   and efficient in LendSelectionFragment
 */


class ArticleListLendingAdapter(val articleList : ArrayList<ArticleMaster>) : RecyclerView.Adapter<ArticleListLendingAdapter.ArticleViewHolder>(),
    ArticleClickListener {

    fun updateArticleList(newArticleList: List<ArticleMaster>){
        articleList.clear()
        articleList.addAll(newArticleList.sortedBy { it.articleName })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = DataBindingUtil.inflate<ItemArticleBinding>(
            inflater,
            R.layout.item_article,
            parent,
            false
        )
        return ArticleViewHolder(
            dataBinding
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.dataBinding.article = articleList[position]
        holder.dataBinding.listener = this
    }

    override fun getItemCount(): Int = articleList.size

    override fun onArticleClicked(view: View) {

        val articleId = view.text_article_id.text.toString()
        val action = LendingSelectionFragmentDirections.actionLendingSelectionFragmentToLendingFormFragment(articleId)
        Navigation.findNavController(view).navigate(action)

    }

    // using databinding
    class ArticleViewHolder(var dataBinding: ItemArticleBinding) :
        RecyclerView.ViewHolder(dataBinding.root)
}