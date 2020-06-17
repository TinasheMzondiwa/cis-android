package com.tinashe.hymnal.ui.hymns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.tinashe.hymnal.data.model.constants.Status
import com.tinashe.hymnal.databinding.FragmentHymnsBinding
import com.tinashe.hymnal.extensions.arch.observeNonNull
import com.tinashe.hymnal.ui.hymns.adapter.HymnListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HymnsFragment : Fragment() {

    private val viewModel: HymnsViewModel by viewModels()

    private var binding: FragmentHymnsBinding? = null

    private val listAdapter: HymnListAdapter = HymnListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHymnsBinding.inflate(inflater, container, false).also {
            binding = it
            binding?.hymnsListView?.apply {
                addItemDecoration(DividerItemDecoration(context, VERTICAL))
                adapter = listAdapter
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.statusLiveData.observeNonNull(viewLifecycleOwner) {
            binding?.apply {
                hymnsListView.isVisible = it != Status.LOADING
                progressBar.isVisible = it == Status.LOADING
            }
        }
        viewModel.hymnListLiveData.observeNonNull(viewLifecycleOwner) {
            listAdapter.submitList(ArrayList(it))
        }
    }
}