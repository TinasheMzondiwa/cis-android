package com.tinashe.hymnal.ui.collections

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tinashe.hymnal.R
import com.tinashe.hymnal.ui.AppBarBehaviour
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment : Fragment(R.layout.fragment_collections) {

    private val viewModel: CollectionsViewModel by viewModels()

    private var appBarBehaviour: AppBarBehaviour? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appBarBehaviour = context as? AppBarBehaviour
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.collections_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                appBarBehaviour?.setAppBarExpanded(false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                appBarBehaviour?.setAppBarExpanded(true)
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.performSearch(query)
                return true
            }
        })
    }
}
