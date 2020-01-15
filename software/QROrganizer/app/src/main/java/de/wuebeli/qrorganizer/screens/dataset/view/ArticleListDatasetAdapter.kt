package de.wuebeli.qrorganizer.screens.dataset.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import de.wuebeli.qrorganizer.R
import de.wuebeli.qrorganizer.databinding.ItemArticleBinding
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.util.ArticleClickListener
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleListDatasetAdapter(val articleList : ArrayList<ArticleMaster>) : RecyclerView.Adapter<ArticleListDatasetAdapter.ArticleViewHolder>(),
    ArticleClickListener {

    fun updateArticleList(newArticleList: List<ArticleMaster>){
        articleList.clear()
        articleList.addAll(newArticleList)
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

        // ToDo clickListener seems not to work yet
        //  also implement navigation to upcoming Fragment here by passing the selected Article
        //  maybe via ViewModelFactory

        val articleId = view.text_article_id.text.toString()
        val action = DatasetFragmentDirections.actionDatasetFragmentToLendArticleOverviewFragment(articleId)
        //action.articleId = articleId
        Navigation.findNavController(view).navigate(action)
    }

    // using databinding
    class ArticleViewHolder(var dataBinding: ItemArticleBinding) :
        RecyclerView.ViewHolder(dataBinding.root)
}