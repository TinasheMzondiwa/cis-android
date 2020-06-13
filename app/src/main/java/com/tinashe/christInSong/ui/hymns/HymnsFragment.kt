package com.tinashe.christInSong.ui.hymns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tinashe.christInSong.R

class HymnsFragment : Fragment() {

    private lateinit var hymnsViewModel: HymnsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        hymnsViewModel =
                ViewModelProviders.of(this).get(HymnsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_hymns, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        hymnsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}