package de.androidnewcomer.qrtest


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_article_overview.*


class ArticleOverviewFragment : Fragment() {

    private var qrID="0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            qrID = ArticleOverviewFragmentArgs.fromBundle(it).qrID
            textView.text=qrID
        }
    }


}
