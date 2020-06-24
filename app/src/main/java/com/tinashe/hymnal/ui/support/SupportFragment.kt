package com.tinashe.hymnal.ui.support

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tinashe.hymnal.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportFragment : Fragment(R.layout.fragment_support) {

    private val viewModel: SupportViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.support_menu, menu)
    }
}
