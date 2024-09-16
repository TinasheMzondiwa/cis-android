package com.tinashe.hymnal.ui.hymns.sing.present

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.tinashe.hymnal.R
import com.tinashe.hymnal.databinding.FragmentHymnVerseBinding
import io.noties.markwon.Markwon

class HymnVerseFragment : Fragment(R.layout.fragment_hymn_verse) {

    private lateinit var binding: FragmentHymnVerseBinding

    private val markworn: Markwon by lazy {
        Markwon.create(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHymnVerseBinding.bind(view)

        val content = arguments?.getString(ARG_VERSE) ?: ""

        if (arguments?.getBoolean(ARG_TYPE_MD) == true) {
            markworn.setMarkdown(binding.verseText, content)
        } else {
            binding.verseText.text = content
        }
    }

    companion object {
        private const val ARG_VERSE = "arg:verse"
        private const val ARG_TYPE_MD = "arg:type_md"

        fun newInstance(
            verse: String,
            isMarkdown: Boolean = false
        ): HymnVerseFragment = HymnVerseFragment().apply {
            arguments = bundleOf(
                ARG_VERSE to verse,
                ARG_TYPE_MD to isMarkdown
            )
        }
    }
}
