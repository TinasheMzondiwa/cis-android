package com.tinashe.hymnal.ui.hymns

import android.app.ActivityOptions
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.tinashe.hymnal.R
import com.tinashe.hymnal.data.model.Hymnal
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.databinding.FragmentHymnsBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.extensions.context.getColorPrimary
import com.tinashe.hymnal.ui.AppBarBehaviour
import com.tinashe.hymnal.ui.hymns.adapter.HymnListAdapter
import com.tinashe.hymnal.ui.hymns.hymnals.HymnalListFragment.Companion.SELECTED_HYMNAL_KEY
import com.tinashe.hymnal.ui.hymns.sing.SingHymnsActivity
import dagger.hilt.android.AndroidEntryPoint
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

@AndroidEntryPoint
class HymnsFragment : Fragment(R.layout.fragment_hymns), MenuProvider {

    private val viewModel: HymnsViewModel by viewModels()
    private lateinit var binding: FragmentHymnsBinding

    private val listAdapter: HymnListAdapter = HymnListAdapter { pair ->
        openSelectedHymn(pair.first.hymnId, pair.second)
    }

    private var appBarBehaviour: AppBarBehaviour? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appBarBehaviour = context as? AppBarBehaviour
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHymnsBinding.bind(view)

        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.hymnsListView.apply {
            addItemDecoration(DividerItemDecoration(context, VERTICAL))
            adapter = listAdapter
        }

        viewModel.showHymnalsPromptLiveData.observe(viewLifecycleOwner) {
            showHymnalsTargetPrompt()
        }
        viewModel.statusLiveData.observeNonNull(viewLifecycleOwner) {
            binding.apply {
                hymnsListView.isVisible = it != Status.LOADING
                progressBar.isVisible = it == Status.LOADING
            }
        }
        viewModel.messageLiveData.observeNonNull(viewLifecycleOwner) {
            binding.snackbar.show(messageText = it)
        }
        viewModel.hymnalTitleLiveData.observeNonNull(viewLifecycleOwner) {
            appBarBehaviour?.setAppBarTitle(it)
        }
        viewModel.selectedHymnIdLiveData.observeNonNull(viewLifecycleOwner) {
            openSelectedHymn(it)
        }
        viewModel.hymnListLiveData.observeNonNull(viewLifecycleOwner) { hymns ->
            listAdapter.submitList(ArrayList(hymns))
        }

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Hymnal>(SELECTED_HYMNAL_KEY)
            ?.observeNonNull(viewLifecycleOwner) { hymnal ->
                viewModel.hymnalSelected(hymnal)
            }
    }

    private fun openSelectedHymn(hymnId: Int, animView: View? = null) {
        val intent = SingHymnsActivity.singIntent(requireContext(), hymnId)
        animView?.let {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                it,
                getString(R.string.transition_shared_element)
            )
            requireActivity().startActivity(intent, options.toBundle())
        } ?: startActivity(intent)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.hymns_list, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.hint_search_hymns)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                appBarBehaviour?.setAppBarExpanded(false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.actions_number -> {
                val fragment = PickHymnFragment.newInstance { number ->
                    viewModel.hymnNumberSelected(requireContext(), number)
                }
                fragment.show(childFragmentManager, fragment.tag)
                true
            }
            R.id.actions_hymnals -> {
                with(findNavController()) {
                    if (currentDestination?.id == R.id.navigation_hymns) {
                        navigate(R.id.action_navigate_to_hymnalListFragment)
                    }
                }
                true
            }
            else -> false
        }
    }

    private fun showHymnalsTargetPrompt() {
        MaterialTapTargetPrompt.Builder(requireActivity())
            .setTarget(R.id.actions_hymnals)
            .setIcon(R.drawable.ic_bookshelf_prompt)
            .setBackgroundColour(requireContext().getColorPrimary())
            .setPrimaryText(R.string.switch_between_hymnals)
            .setSecondaryText(R.string.switch_between_hymnals_message)
            .show()

        viewModel.hymnalsPromptShown()
    }
}
