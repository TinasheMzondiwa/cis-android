package com.tinashe.hymnal.ui.support

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.tinashe.hymnal.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SupportViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.text_dashboard)
        viewModel.text.observe(
            viewLifecycleOwner,
            Observer {
                textView.text = it
            }
        )
    }
}
