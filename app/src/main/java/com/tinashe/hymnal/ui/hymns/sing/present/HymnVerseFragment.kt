package com.tinashe.hymnal.ui.hymns.sing.present

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.databinding.FragmentHymnVerseBinding

class HymnVerseFragment : Fragment() {

    private var binding: FragmentHymnVerseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHymnVerseBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val verse = arguments?.getString(ARG_VERSE)
        binding?.verseText?.text = verse
    }

    companion object {
        private const val ARG_VERSE = "arg:verse"

        fun newInstance(verse: String): HymnVerseFragment = HymnVerseFragment().apply {
            arguments = bundleOf(ARG_VERSE to verse)
        }
    }
}
