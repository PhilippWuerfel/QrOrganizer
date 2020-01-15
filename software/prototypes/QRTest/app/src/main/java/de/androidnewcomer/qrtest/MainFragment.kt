package de.androidnewcomer.qrtest


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonScan.setOnClickListener(){
            val scanner= IntentIntegrator.forSupportFragment(this)
            scanner.initiateScan()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(getActivity()?.baseContext, "Cancelled", Toast.LENGTH_LONG)
                } else {
                    val action = MainFragmentDirections.actionMainFragmentToArticleOverviewFragment()
                    action.qrID=result.contents
                    Navigation.findNavController(fragment.requireView()).navigate(action)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


}
