package com.tinashe.christInSong.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tinashe.christInSong.R

class CollectionsFragment : Fragment() {

    private lateinit var collectionsViewModel: CollectionsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        collectionsViewModel =
                ViewModelProviders.of(this).get(CollectionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_collections, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        collectionsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}