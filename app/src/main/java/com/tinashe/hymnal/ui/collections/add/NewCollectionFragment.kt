package com.tinashe.hymnal.ui.collections.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.databinding.FragmentNewCollectionBinding

class NewCollectionFragment : Fragment() {

    private var binding: FragmentNewCollectionBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentNewCollectionBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }
}